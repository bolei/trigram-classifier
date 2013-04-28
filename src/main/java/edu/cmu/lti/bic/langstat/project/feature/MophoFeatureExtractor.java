package edu.cmu.lti.bic.langstat.project.feature;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.lti.bic.langstat.project.pipeline.Article;
import edu.cmu.lti.bic.langstat.project.pipeline.Pipeline;

public class MophoFeatureExtractor implements FeatureExtractor {

	private static HashMap<String, HashMap<String, Integer>> tokenIndex = new HashMap<String, HashMap<String, Integer>>();
	private static final String MOPH_FILE = "/script/catvar21/catvar21.signed";
	static {
		BufferedReader brIn = new BufferedReader(new InputStreamReader(
				MophoFeatureExtractor.class.getResourceAsStream(MOPH_FILE)));
		try {
			String line = null;
			while ((line = brIn.readLine()) != null) {
				String[] items = line.trim().split("#");
				for (String item : items) {
					String[] subItems = item.split("[_|%]");
					if (!tokenIndex.containsKey(subItems[0])) {
						tokenIndex.put(subItems[0],
								new HashMap<String, Integer>());
					}

					tokenIndex.get(subItems[0]).put(subItems[1],
							Integer.parseInt(subItems[2]));

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				brIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static final List<MorphFeaturesCalculator> calcs = new LinkedList<MorphFeaturesCalculator>();
	static {
		calcs.add(new AverageTagNumMorphFeatureCalculatorImpl());
	}

	// TODO: try other values
	// private static final int THRESH_HOLD = 1;

	public void extractFeature(List<Article> artList, boolean isTraining) {

		HashMap<Article, SentencePOS[]> allPos = new HashMap<Article, MophoFeatureExtractor.SentencePOS[]>();

		// load all short article POS tags with article id
		for (int i = 0; i < artList.size(); i++) {
			System.out.println(i);
			Article art = artList.get(i);
			// if (art.getSentenceCount() > THRESH_HOLD
			// || art.getSentenceCount() == 0) {
			// // run only on short articles
			// continue;
			// }
			SentencePOS[] sentPos = new SentencePOS[art.getSentences().size()];
			allPos.put(art, sentPos);
			for (int j = 0; j < art.getSentences().size(); j++) {
				sentPos[j] = SentencePOS
						.calculatePos(art.getSentences().get(j));
			}
		}

		String outFileName = "morph-feature-" + new Date().getTime() + ".txt";
		String outPath = Pipeline.prop.getProperty("feature.folder");
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outPath + "/" + outFileName));
			// extract POS features
			for (Entry<Article, SentencePOS[]> entry : allPos.entrySet()) {
				StringBuilder sb = new StringBuilder();
				for (MorphFeaturesCalculator calc : calcs) {
					sb.append(calc.calculateFeature(entry) + "\t");
				}
				if (isTraining) {
					sb.append(entry.getKey().getLabel());
				}
				out.println(sb.toString());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			out = null;
		}
	}

	private static class SentencePOS {
		private List<HashMap<String, Integer>> tags = new LinkedList<HashMap<String, Integer>>();

		public static SentencePOS calculatePos(String sentence) {
			SentencePOS pos = new SentencePOS();
			String[] tokens = sentence.split("\\s+");
			for (String tok : tokens) {
				HashMap<String, Integer> val = tokenIndex
						.get(tok.toLowerCase());
				if (val == null) {
					val = new HashMap<String, Integer>();
					// System.out.println("==>" + tok);
				}
				pos.tags.add(val);
			}
			return pos;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (HashMap<String, Integer> tokenPos : tags) {
				if (tokenPos != null && tokenPos.isEmpty() == false) {
					for (Entry<String, Integer> entry : tokenPos.entrySet()) {
						sb.append(entry.getKey() + "\t" + entry.getValue()
								+ "\n");
					}
				} else {
					sb.append("null\n");
				}
				sb.append("======\n");
			}
			return sb.toString();
		}
	}

	private static interface MorphFeaturesCalculator {
		public double calculateFeature(Entry<Article, SentencePOS[]> entry);
	}

	private static class AverageTagNumMorphFeatureCalculatorImpl implements
			MorphFeaturesCalculator {

		public double calculateFeature(Entry<Article, SentencePOS[]> entry) {
			double count = 0;
			SentencePOS[] artPos = entry.getValue();
			Article art = entry.getKey();
			for (SentencePOS sentPos : artPos) {
				// for each sentence
				for (HashMap<String, Integer> tagMap : sentPos.tags) {
					// for each token
					count += tagMap.size();
				}
			}
			if (art.getTokenCount() == 0) {
				return 0;
			}
			return count / art.getTokenCount();
		}
	}

	public static void main(String[] args) {
		FeatureExtractor extractor = new MophoFeatureExtractor();
		LinkedList<String> sentences = new LinkedList<String>();
		Collections
				.addAll(sentences,
						new String[] { "SHE THINKS THAT THIS WAS A LETTER OF THE OUTSIDE" });
		Article art = new Article("0", sentences);

		LinkedList<String> sentences2 = new LinkedList<String>();
		Collections.addAll(sentences2,
				new String[] { "A FINAL NOTE FROM THE NEWS" });
		Article art2 = new Article("1", sentences2);

		LinkedList<Article> artList = new LinkedList<Article>();
		artList.add(art);
		artList.add(art2);
		extractor.extractFeature(artList, true);
	}
}