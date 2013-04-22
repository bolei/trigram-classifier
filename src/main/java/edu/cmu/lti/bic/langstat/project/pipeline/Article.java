package edu.cmu.lti.bic.langstat.project.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

public class Article {
	private boolean label;
	private LinkedList<String> sentences = new LinkedList<String>();

	public static LinkedList<Article> loadArticles(BufferedReader dataIn,
			BufferedReader tagIn) throws IOException {
		LinkedList<Article> result = new LinkedList<Article>();
		String line = null;
		Article art = null;
		while ((line = dataIn.readLine()) != null) {
			if (line.equals("~~~~~")) {
				art = new Article();
				art.label = tagIn.readLine().equals("1");
				result.add(art);
				continue;
			}
			String[] tokens = line.split("\\s+");
			if (tokens.length < RunPipeline.SENT_MIN_LENGTH) {
				continue;
			}
			art.sentences.add(line);
		}
		return result;
	}

	public boolean isLabel() {
		return label;
	}

	public LinkedList<String> getSentences() {
		return sentences;
	}
}
