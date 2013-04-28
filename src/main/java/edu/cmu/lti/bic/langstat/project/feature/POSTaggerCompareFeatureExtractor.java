package edu.cmu.lti.bic.langstat.project.feature;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Streams;

import edu.cmu.lti.bic.langstat.project.pipeline.Article;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTaggerCompareFeatureExtractor implements FeatureExtractor {
	static TokenizerFactory TOKENIZER_FACTORY = new RegExTokenizerFactory(
			"(-|'|\\d|\\p{L})+|\\S");
	private static HmmDecoder decoder = null;
	static {
		ObjectInputStream objIn;
		try {
			objIn = new ObjectInputStream(
					LingpipePosTaggerFeatureExtractor.class
							.getResourceAsStream("/lingpipe/pos-en-general-brown.HiddenMarkovModel"));
			HiddenMarkovModel hmm = (HiddenMarkovModel) objIn.readObject();
			Streams.closeQuietly(objIn);
			decoder = new HmmDecoder(hmm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static private ArrayList<Set<String>> pennTreebankTagSet = new ArrayList<Set<String>>();
	static private ArrayList<Set<String>> brownTagSet = new ArrayList<Set<String>>();
	static {
		String[] pennTreeArr = new String[] { ".", ",", ":", "'", "\" ", "(",
				")", "#", "$", "CC", "CD", "DT", "EX", "FW", "IN",
				"JJ JJR JJS", "LS", "MD", "NN NNS", "NNP NNPS", "PDT", "POS",
				"PP$ PRP", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH",
				"VB VBP", "VBD", "VBG", "VBN", "VBZ", "WDT WP WP$ WRB" };
		for (String tagSet : pennTreeArr) {
			Set<String> set = new HashSet<String>();
			Collections.addAll(set, tagSet.split("\\s+"));
			pennTreebankTagSet.add(set);
		}
		String[] brownArr = new String[] { ".", ",", ":", "'", "\"", "(", ")",
				"", "", "CC CS", "CD CD$ OD",
				"DT DT$ DTI DTS DTX AT AP AP$ QL QLP ", "EX", "", "IN",
				"JJ JJ$ JJR JJS JJT", "", "MD", "NN NN$ NNS NNS$ NR NR$ NRS",
				"NP NP$ NPS NPS$", "ABL ABN ABX", "",
				"PN$ PP$ PP$$ PPS PPSS PPL PPLS PPO PPS PPSS PN", "RB RN RB$",
				"RBR", "RBT", "RP", "", "TO", "UH", "VB HV BE BEM BER DO",
				"VBD HVD BED BEDZ DOD", "VBG HVG BEG", "VBN HVN BEN",
				"VBZ HVZ BEZ DOZ", "WDT WQL WPO WPS WP$ WRB" };
		for (String tagSet : brownArr) {
			Set<String> set = new HashSet<String>();
			Collections.addAll(set, tagSet.split("\\s+"));
			brownTagSet.add(set);
		}
	}

	private static MaxentTagger stanfordTagger = new MaxentTagger(
			"lib/english-left3words-distsim.tagger");

	public void extractFeature(List<Article> artList, boolean isTraining) {
		final int SENT_LEN_MAX_CUTOFF = 10;
		final int SENT_LEN_MIN_CUTTOFF = 3;
		String fileName = new Date().getTime()
				+ "-pos-tag-compare-feature.data";
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("features/" + fileName));
			for (Article art : artList) {
				double maxDiffRate = 0;
				int sentLen = 0;
				for (String sent : art.getSentences()) {
					sent = sent.replaceAll("<[/]?s>", "").trim().toLowerCase();
					// Lingpipe tagging
					char[] cs = sent.toCharArray();
					Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs, 0,
							cs.length);
					String[] tokens = tokenizer.tokenize();
					if (tokens.length > SENT_LEN_MAX_CUTOFF
							|| tokens.length < SENT_LEN_MIN_CUTTOFF) {
						continue;
					}
					List<String> tokenList = Arrays.asList(tokens);
					LinkedList<String> lingpipeTags = new LinkedList<String>();
					Tagging<String> tagging = decoder.tag(tokenList);
					for (int i = 0; i < tagging.size(); ++i) {
						// System.out.print(tagging.tag(i) + "\t");
						lingpipeTags.add(tagging.tag(i));
					}
					// System.out.println();

					// Stanford tagging
					LinkedList<String> stanfordTags = new LinkedList<String>();
					String taggedString = stanfordTagger
							.tagTokenizedString(prepareWhitespaceSentence(tokens));
					stanfordTags = extractTags(taggedString);

					// compare difference
					if (stanfordTags.size() != lingpipeTags.size()) {
						System.out.println("different length! " + art.getId());
					}

					int tokenCount = Math.max(stanfordTags.size(),
							lingpipeTags.size());
					double diffRate = ((double) countTaggingDifference(
							stanfordTags, lingpipeTags)) / tokenCount;
					if (maxDiffRate < diffRate) {
						maxDiffRate = diffRate;
						sentLen = tokenCount;
					}

					// int tokenCount = Math.max(stanfordTags.size(),
					// lingpipeTags.size());
					//
					// double diffCount = countTaggingDifference(stanfordTags,
					// lingpipeTags);
					// double avgdiff = ((double) diffCount) / tokenCount;
					// out.println(avgdiff + "\t" + tokenCount + "\t"
					// + art.getLabel() + "\t" + art.getId());
				}
				// System.out.println(art.getId());
				System.out.println(maxDiffRate + "\t" + sentLen + "\t"
						+ art.getLabel() + "\t" + art.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	private LinkedList<String> extractTags(String taggedString) {
		LinkedList<String> tags = new LinkedList<String>();
		String[] tokArr = taggedString.split("\\s+");
		for (String tok : tokArr) {
			tags.add(tok.split("_")[1]);
		}
		return tags;
	}

	private static String prepareWhitespaceSentence(String[] tokens) {
		StringBuilder sb = new StringBuilder();
		for (String str : tokens) {
			sb.append(str + " ");
		}
		return sb.toString().trim();
	}

	private static int countTaggingDifference(LinkedList<String> stanfordTags,
			LinkedList<String> lingpipeTags) {
		if (stanfordTags.size() != lingpipeTags.size()) {
			// if two taggers does different tokenization, can't compare
			return Math.max(stanfordTags.size(), lingpipeTags.size());
		}
		int count = 0;
		for (int i = 0; i < stanfordTags.size(); i++) {
			if (compareTag(stanfordTags.get(i), lingpipeTags.get(i)) != true) {
				count++;
			}
		}
		return count;
	}

	private static boolean compareTag(String penn, String brown) {
		int pennIndex = -1;
		for (int i = 0; i < pennTreebankTagSet.size(); i++) {
			if (pennTreebankTagSet.get(i).contains(penn.toUpperCase())) {
				pennIndex = i;
				break;
			}
		}
		if (pennIndex == -1) {
			return false;
		}
		return brownTagSet.get(pennIndex).contains(brown.toUpperCase());
	}

}
