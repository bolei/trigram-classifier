package edu.cmu.lti.bic.langstat.project.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.cmu.lti.bic.langstat.project.feature.FeatureExtractor;
import edu.cmu.lti.bic.langstat.project.feature.FeatureExtractorFactory;

public class Pipeline {
	private List<FeatureExtractor> featureExtractors = new LinkedList<FeatureExtractor>();
	public static final Properties prop = new Properties();
	static {
		try {
			prop.load(Pipeline.class
					.getResourceAsStream("/config/pipeline.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pipeline() {
		String[] names = prop.getProperty("feature.extractors").split(",");
		for (String name : names) {
			featureExtractors.add(FeatureExtractorFactory
					.getFeatureExtractor(name));
		}
	}

	public void startPipeline(BufferedReader dataIn, BufferedReader tagIn,
			boolean isTraining) throws IOException {
		// load articles
		List<Article> artList = Article.loadArticles(dataIn, tagIn, isTraining);

		// extract features
		for (FeatureExtractor fe : featureExtractors) {
			fe.extractFeature(artList, isTraining);
		}

		// collect features
		// new FeatureCollector().collectFeatures(
		// prop.getProperty("feature.folder"), artList, isTraining);
	}
}
