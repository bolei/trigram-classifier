package edu.cmu.lti.bic.langstat.project.feature;

public class FeatureExtractorFactory {
	public static FeatureExtractor getFeatureExtractor(String name) {
		if (name.equalsIgnoreCase("MophoFeatureExtractor")) {
			return new MophoFeatureExtractor();
		} else if (name.equalsIgnoreCase("LingpipePosTaggerFeatureExtractor")) {
			return new LingpipePosTaggerFeatureExtractor();
		} else if (name.equalsIgnoreCase("POSTaggerCompareFeatureExtractor")) {
			return new POSTaggerCompareFeatureExtractor();
		}
		return null;
	}
}
