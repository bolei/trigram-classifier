package edu.cmu.lti.bic.langstat.project.feature;

import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.ScoredTagging;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Streams;

import edu.cmu.lti.bic.langstat.project.pipeline.Article;
import edu.cmu.lti.bic.langstat.project.pipeline.MiscUtil;

public class LingpipePosTaggerFeatureExtractor implements FeatureExtractor {

	private static HmmDecoder decoder = null;
	static TokenizerFactory TOKENIZER_FACTORY = new RegExTokenizerFactory(
			"(-|'|\\d|\\p{L})+|\\S");
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

	public void extractFeature(List<Article> artList, boolean isTraining) {
		final int MAX_N_BEST = 1;
		for (Article art : artList) {
			int tokenCount = 0;
			double totalScore = 0;
			for (String sent : art.getSentences()) {
				// tokenize sentence
				char[] cs = sent.replaceAll("<[/]?s>", "").trim().toLowerCase()
						.toCharArray();
				Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs, 0,
						cs.length);
				String[] tokens = tokenizer.tokenize();
				tokenCount += tokens.length;
				List<String> tokenList = Arrays.asList(tokens);

				// Decode
				Iterator<ScoredTagging<String>> nBestIt = decoder
						.tagNBestConditional(tokenList, MAX_N_BEST);
				for (int n = 0; n < MAX_N_BEST && nBestIt.hasNext(); ++n) {
					ScoredTagging<String> scoredTagging = nBestIt.next();
					totalScore += scoredTagging.score();
					System.out.print(n + "   "
							+ MiscUtil.format(scoredTagging.score()) + "  ");
					for (int i = 0; i < tokenList.size(); ++i)
						System.out.print(scoredTagging.token(i) + "_"
								+ MiscUtil.pad(scoredTagging.tag(i), 5));
					System.out.println();
				}
			}
			// System.out.println("===========");
			System.out.println((totalScore / tokenCount) + "\t"
					+ art.getLabel() + "\t" + art.getId());
		}
	}
}
