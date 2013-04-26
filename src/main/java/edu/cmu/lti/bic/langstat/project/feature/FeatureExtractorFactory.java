package edu.cmu.lti.bic.langstat.project.feature;

public class FeatureExtractorFactory {
	public static FeatureExtractor getFeatureExtractor(String name) {
		if (name.equalsIgnoreCase("TestFeatureExtractor")) {
			return new TestFeatureExtractor();
		} else if (name.equalsIgnoreCase("MophoFeatureExtractor")) {
			return new MophoFeatureExtractor();
		}
		return null;
	}
}
