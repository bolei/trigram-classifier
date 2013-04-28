package edu.cmu.lti.bic.langstat.project.similar;

import java.io.IOException;
import java.util.Properties;

import de.linguatools.disco.DISCO;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Similarity {
	DISCO disco = null;
	StanfordCoreNLP pipeline = null;
	Similarity() throws IOException{
		DISCO disco = new DISCO("data/similarityModel", false);
		Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit,pos");
	    pipeline = new StanfordCoreNLP(props);
	}
	double getSelfMaxSimilarity(String s) throws IOException{
		String[] tokens = s.split(" ");
		double max = 0;
		for(int i=0;i<tokens.length - 1;i++){
			for(int j=i+3;j<tokens.length;j++){
				double tmp = disco.secondOrderSimilarity(tokens[i], tokens[j]);
				if(tmp>max)
					max = tmp;
			}
			
		}
		return max;
	}
	double getSelfAvgSimilarity(String s) throws IOException{
		String[] tokens = s.split(" ");
		double all = 0;
		int count = 0;
		for(int i=0;i<tokens.length - 1;i++){
			for(int j=i+3;j<tokens.length;j++){
				double tmp = disco.secondOrderSimilarity(tokens[i], tokens[j]);
				if(tmp>=0){
					all+=tmp;
					count++;
				}
			}
			
		}
		return 1.0*all/count;
	}
}
