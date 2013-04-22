package edu.cmu.lti.bic.langstat.project.feature;

import java.util.List;

import edu.cmu.lti.bic.langstat.project.pipeline.Article;

public class TestFeatureExtractor implements FeatureExtractor {

	public void extractFeature(List<Article> artList) {
		System.out.println("test feature extractor");
	}

}
