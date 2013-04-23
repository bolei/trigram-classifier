package edu.cmu.lti.bic.langstat.project.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

public class Article {
	private String label;
	private LinkedList<String> sentences = new LinkedList<String>();

	public static LinkedList<Article> loadArticles(BufferedReader dataIn,
			BufferedReader tagIn, boolean isTraining) throws IOException {
		LinkedList<Article> result = new LinkedList<Article>();
		String line = null;
		Article art = null;
		while ((line = dataIn.readLine()) != null) {
			if (line.trim().equals("~~~~~")) {
				art = new Article();
				if (isTraining == true) {
					art.label = tagIn.readLine();
				}
				result.add(art);
				continue;
			} else {
				art.sentences.add(line);
			}
		}
		return result;
	}

	public String getLabel() {
		return label;
	}

	public LinkedList<String> getSentences() {
		return sentences;
	}
}
