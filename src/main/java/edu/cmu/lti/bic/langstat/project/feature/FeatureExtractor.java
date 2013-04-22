package edu.cmu.lti.bic.langstat.project.feature;

import java.util.List;

import edu.cmu.lti.bic.langstat.project.pipeline.Article;

public interface FeatureExtractor {
	public void extractFeature(List<Article> artList);
}
