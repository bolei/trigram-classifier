package edu.cmu.lti.bic.langstat.project.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunPipeline {

	private static final String TRAIN_SET = "/data/trainingSet.dat";
	private static final String TRAIN_SET_TAG = "/data/trainingSetLabels.dat";

	public static void main(String[] args) {
		String flag = args[0];
		BufferedReader dataIn = null, tagIn = null;
		Pipeline pipeLine = new Pipeline();
		try {
			if (flag.equalsIgnoreCase("train")) {
				dataIn = new BufferedReader(new InputStreamReader(
						RunPipeline.class.getResourceAsStream(TRAIN_SET)));
				tagIn = new BufferedReader(new InputStreamReader(
						RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
				pipeLine.startPipeline(dataIn, tagIn);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dataIn = null;
			}
			if (tagIn != null) {
				try {
					tagIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				tagIn = null;
			}
		}
	}
}
