package edu.cmu.lti.bic.langstat.project.pcfg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;


import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class trainPosProbability {
	StanfordCoreNLP pipeline = null;
	int total = 0;
	TreeMap<String,HashMap<String,Double>> PosPool;
	HashMap<String,Double> posLables;
	trainPosProbability(){
		Properties props = new Properties();
	    // pos, lemma, ner,
	    props.put("annotators", "tokenize, ssplit,pos");
	    pipeline = new StanfordCoreNLP(props);
	    PosPool = new TreeMap<String,HashMap<String,Double>>();
	    posLables = createPosMap();
	}
	void check(String s){
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    for(CoreLabel t:sentences.get(0).get(TokensAnnotation.class)){
	    	
	    	String word = t.get(TextAnnotation.class);
	    	word=word.toLowerCase();
	        String pos = t.get(PartOfSpeechAnnotation.class);
	        if(!this.posLables.containsKey(pos))
	        	continue;
	        if(!PosPool.containsKey(word))
	        	PosPool.put(word,createPosMap() );
	        HashMap<String,Double> map = PosPool.get(word);
	        map.put(pos, map.get(pos)+1); 
	    }
	   
	}
	TreeMap<String, HashMap<String, Double>> finish(){
		Iterator it =  PosPool.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        HashMap<String, Double> map = (HashMap<String, Double>) pairs.getValue();
			double count = 0;
	        for(String ss:map.keySet()){
				count+=map.get(ss);
			}
	        for(String ss:map.keySet()){
				map.put(ss,1.0*map.get(ss)/count);
			}
	    }
	    return PosPool;
	}
	HashMap<String,Double> createPosMap(){
		HashMap<String,Double> map = new HashMap<String,Double>();
		String[] array = {"CC","CD","DT","EX","FW","IN","JJ","JJR","JJS","LS","MD","NN","NNP","NNPS","NNS","PDT","POS","PRP","PRP$",
				"RB","RBR","RBS","RP","SYM","TO","UH","VB","VBD","VBG","VBN","VBP","VBZ","WDT","WP","WP$","WRB"};
		for(String s:array){
			map.put(s, 0.0);
		}
		
		return map;
	}
	
}
