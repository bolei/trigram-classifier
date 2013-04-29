package edu.cmu.lti.bic.langstat.project.feature;

import java.util.List;
import java.util.Properties;

import edu.cmu.lti.bic.langstat.project.pipeline.Article;
import edu.cmu.lti.bic.langstat.project.pipeline.MiscUtil;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class IllegalTagFeatureExtractor implements FeatureExtractor {
	private static StanfordCoreNLP pipeline;
	static {
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);
	}

	public void extractFeature(List<Article> artList, boolean isTraining) {

		final int SENT_LEN_MAX_CUTOFF = 15;
		final int SENT_LEN_MIN_CUTTOFF = 3;
		int sentCount = 0;
		for (Article art : artList) {
			if (sentCount >= 100) {
				return;
			}
			if (art.getLabel().equals("1")) {
				continue;
			}
			for (String sent : art.getSentences()) {
				sent = sent.replaceAll("<[/]?s>", "").trim().toLowerCase();
				Annotation document = new Annotation(sent);
				pipeline.annotate(document);
				CoreMap sentence = document.get(SentencesAnnotation.class).get(
						0);
				if (sentence.get(TokensAnnotation.class).size() > SENT_LEN_MAX_CUTOFF
						|| sentence.get(TokensAnnotation.class).size() < SENT_LEN_MIN_CUTTOFF) {
					continue;
				}
				sentCount++;
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					System.out
							.print(token.get(TextAnnotation.class)
									+ "_"
									+ MiscUtil.pad(token
											.get(PartOfSpeechAnnotation.class),
											5));
				}
				System.out.println();

			}
		}
	}

}
