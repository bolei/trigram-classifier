package edu.cmu.lti.bic.langstat.project.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

public class Article {
	private static int count = 0;
	private final int id = count++;

	private String label;
	private LinkedList<String> sentences = new LinkedList<String>();
	private int tokenCount;
	private int sentenceCount;

	public Article(String label, LinkedList<String> sentences) {
		this.label = label;
		this.sentences = sentences;
	}

	private Article() {
	}

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

	public int getTokenCount() {
		if (tokenCount == 0) {
			for (String sent : sentences) {
				tokenCount += sent.split("\\s+").length;
			}
		}
		return tokenCount;
	}

	public int getSentenceCount() {
		if (sentenceCount == 0) {
			sentenceCount = sentences.size();
		}
		return sentenceCount;
	}

	public int getId() {
		return id;
	}
}
