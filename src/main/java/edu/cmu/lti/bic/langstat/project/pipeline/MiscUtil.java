package edu.cmu.lti.bic.langstat.project.pipeline;

import java.util.Collections;
import java.util.LinkedList;

import edu.cmu.lti.bic.langstat.project.feature.FeatureExtractor;
import edu.cmu.lti.bic.langstat.project.feature.IllegalTagFeatureExtractor;

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
								"IT'S A GREAT LINE THAT I RESIGN SCHOOL BUT THE PRESIDENT HIS TAX BILL PASSED DAYS NOW THE PEOPLE IT'S A TRAGEDY TO SEE"
										.toLowerCase(),
								"AND HE JUST TWO WEEKS AGO WHEN HE WAS ARGUING WITH ONE OF THE TRACK RECORD HE HAS FOUND SOME EVIDENCE THAT THEY BE ALLOWED TO EXPLORE IT SAID THAT HE DID NOT SAY WE'RE NOT GOING TO DETECT SHOWING IT'S SAILING FOR THE RECORD"
										.toLowerCase(),
								"YEAH BUT AND GETTING HIS LESSON".toLowerCase(),
								"SHE THINKS THAT THIS WAS A LETTER OF THE OUTSIDE"
										.toLowerCase(), }, new String[] { "0",
								"0", "0", "0" });
		FeatureExtractor extractor = new IllegalTagFeatureExtractor();
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
