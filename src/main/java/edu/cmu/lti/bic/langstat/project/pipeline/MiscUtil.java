package edu.cmu.lti.bic.langstat.project.pipeline;

import java.util.Collections;
import java.util.LinkedList;

import edu.cmu.lti.bic.langstat.project.feature.FeatureExtractor;
import edu.cmu.lti.bic.langstat.project.feature.LingpipePosTaggerFeatureExtractor;

public class MiscUtil {
	public static LinkedList<Article> generateOneSentenceArticle(
			String[] sentences, String[] labels) {
		LinkedList<Article> artList = new LinkedList<Article>();
		for (int i = 0; i < sentences.length; i++) {
			LinkedList<String> artSentences = new LinkedList<String>();
			Collections.addAll(artSentences, new String[] { sentences[i] });
			Article art = new Article(labels[i], artSentences);
			artList.add(art);
		}
		return artList;
	}

	public static void main(String[] args) {
		LinkedList<Article> artList = MiscUtil
				.generateOneSentenceArticle(
						new String[] {
								"WELL WHAT EXACTLY IS EDUCATIONAL PROGRAMMING FOR CHILDREN AND HOW MUCH SHOULD BROADCASTERS BE REQUIRED TO AIR"
										.toLowerCase(),
								"RUSH HOUR SUBWAY COLLISION IN NEW YORK"
										.toLowerCase(),
								"YEAH BUT AND GETTING HIS LESSON".toLowerCase(),
								"SHE THINKS THAT THIS WAS A LETTER OF THE OUTSIDE"
										.toLowerCase(), }, new String[] { "1",
								"1", "0", "0" });
		FeatureExtractor extractor = new LingpipePosTaggerFeatureExtractor();
		extractor.extractFeature(artList, true);
	}

	public static String format(double x) {
		return String.format("%9.3f", x);
	}

	public static String pad(String in, int length) {
		if (in.length() > length)
			return in.substring(0, length - 3) + "...";
		if (in.length() == length)
			return in;
		StringBuilder sb = new StringBuilder(length);
		sb.append(in);
		while (sb.length() < length)
			sb.append(' ');
		return sb.toString();

	}
}
