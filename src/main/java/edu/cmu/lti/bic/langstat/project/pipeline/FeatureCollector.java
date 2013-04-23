package edu.cmu.lti.bic.langstat.project.pipeline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FeatureCollector {

	private static final String FEATURE_FILE_NAME = "feature.all.txt";
	private static final String FEATURE_LIST_FILE_NAME = "feature.list.txt";

	public void collectFeatures(String featureFolderPath,
			List<Article> artList, boolean isTraining) {
		long timeStamp = new Date().getTime();
		File folder = new File(featureFolderPath);
		File[] files = folder.listFiles();
		BufferedReader[] featureReaders = new BufferedReader[files.length];
		PrintWriter featureOut = null, featureNameOut = null;
		File featureFile = new File(timeStamp + "-" + FEATURE_FILE_NAME);
		File featureListFile = new File(timeStamp + "-"
				+ FEATURE_LIST_FILE_NAME);
		try {
			// init
			if (featureFile.exists() == false) {
				featureFile.createNewFile();
			}
			if (featureListFile.exists() == false) {
				featureListFile.createNewFile();
			}
			featureOut = new PrintWriter(new BufferedWriter(new FileWriter(
					featureFile)));
			featureNameOut = new PrintWriter(featureListFile);
			for (int i = 0; i < files.length; i++) {
				featureReaders[i] = new BufferedReader(new FileReader(files[i]));
			}

			// get feature names
			for (BufferedReader in : featureReaders) {
				String[] fnames = in.readLine().split("\\s+");
				for (String fname : fnames) {
					featureNameOut.println(fname);
				}
			}

			// combine all features
			LinkedList<String> features;
			for (Article art : artList) {
				features = new LinkedList<String>();
				for (BufferedReader in : featureReaders) {
					Collections.addAll(features, in.readLine().split("\\s+"));
				}
				writeFeature(featureOut, features, art, isTraining);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			for (BufferedReader in : featureReaders) {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					in = null;
				}
			}
			if (featureOut != null) {
				featureOut.close();
				featureOut = null;
			}
			if (featureNameOut != null) {
				featureNameOut.close();
				featureNameOut = null;
			}
		}
	}

	private void writeFeature(PrintWriter featureOut,
			LinkedList<String> features, Article art, boolean isTraining) {
		StringBuilder sb = new StringBuilder();
		for (String feature : features) {
			sb.append(feature + "\t");
		}
		if (isTraining) {
			sb.append(art.getLabel());
		}
		featureOut.println(sb.toString());
	}
}
