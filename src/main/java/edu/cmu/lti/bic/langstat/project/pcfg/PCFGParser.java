package edu.cmu.lti.bic.langstat.project.pcfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class PCFGParser {
	StanfordCoreNLP pipeline = null;
	/**
	 * @param args
	 */
	PCFGParser(){
		
		Properties props = new Properties();
	    // pos, lemma, ner,
	    props.put("annotators", "tokenize, ssplit,pos, lemma, ner, parse");
	    pipeline = new StanfordCoreNLP(props);
		
	}
	
	double getScore(String s){
		
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    double score = 0;
	    for(CoreMap c:sentences){
	    	//System.out.println(c.toString());
	    	Tree t = c.get(TreeAnnotation.class);
	    	
	    	score+=c.get(TreeAnnotation.class).score()/t.postOrderNodeList().size();
	    }
	    
	    	return 1.0*score/sentences.size();
	}
	
	CoreMap getAnnotatedSentence(String s){
		
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    return sentences.get(0);
	}
	
	double getLowestNotBaseP(String s){
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree t =sentences.get(0).get(TreeAnnotation.class);
	    List<Tree> list = t.postOrderNodeList();
	    double min = 0;
	    String worst = null;
	    for(Tree tt:list){
	    	
	    	double sum =0;
	    	if (Double.isNaN(tt.score()))
	    		continue;
	    	List<Tree> children = tt.getChildrenAsList();
	    	boolean hasNaN = false;
	    	for(Tree ttt:children){
	    		if(Double.isNaN(ttt.score()) )
	    			hasNaN = true;
	    		else
	    			sum+=ttt.score();
	    	}
	    	double mine = tt.score() - sum;
	    	if (!hasNaN&&mine<min){
	    		min = mine;
	    		worst = tt.toString();
	    	}
	    }
	    System.out.println(worst);
		return min;
	}
	
	double getLowestBaseP(String s){
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree t =sentences.get(0).get(TreeAnnotation.class);
	    List<Tree> list = t.postOrderNodeList();
	    double min = 0;
	    String worst = null;
	    for(Tree tt:list){
	    	
	    	double sum =0;
	    	if (Double.isNaN(tt.score()))
	    		continue;
	    	List<Tree> children = tt.getChildrenAsList();
	    	boolean hasNaN = false;
	    	for(Tree ttt:children){
	    		if(Double.isNaN(ttt.score()) )
	    			hasNaN = true;
	    	}
	    	
	    	if (hasNaN&&tt.score()>-30&&tt.score()<min){
	    		min = tt.score();
	    		worst = tt.toString();
	    	}
	    }
	   // System.out.println(worst);
		return min;
	}
	
	double getNumOfS(String s){
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree t =sentences.get(0).get(TreeAnnotation.class);
	    List<Tree> list = t.postOrderNodeList();
	    int count = 0;
	    for(Tree tt:list){
	    	System.out.println(tt.label().toString());
	    	if(tt.label().toString().equals("S"))
	    		count++;
	    }
	   // System.out.println(worst);
		return count;
	}
	double getBase2(String s){
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree t =sentences.get(0).get(TreeAnnotation.class);
	    List<Tree> list = t.getLeaves();
	    //System.out.println();
	    
	   // System.out.println(worst);
		return 1.0*t.postOrderNodeList().size()/list.size();
	}
	
	
	TreeMap<String,Integer> getAllPos(String s){
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree t =sentences.get(0).get(TreeAnnotation.class);
	    List<Tree> list = t.postOrderNodeList();
	    TreeMap<String,Integer> map = createPosMap();
	    int count = 0;
	    for(Tree tt:list){
	    	//System.out.println(tt.label().value());
	    	if(map.containsKey(tt.label().value()))
	    		map.put(tt.label().value(), map.get(tt.label().value())+1);
	    }
	   // System.out.println(worst);
	    map.put("NUMOFNODE",t.size());
		return map;
	}
	
	TreeMap<String,Integer> createPosMap(){
		TreeMap<String,Integer> map = new TreeMap<String,Integer>();
		String[] array = {"S","CC","CD","DT","EX","FW","IN","JJ","JJR","JJS","LS","MD","NN","NNP","NNPS","NNS","PDT","POS","PRP","PRP$",
				"RB","RBR","RBS","RP","SYM","TO","UH","VB","VBD","VBG","VBN","VBP","VBZ","WDT","WP","WP$","WRB"};
		for(String s:array){
			map.put(s, 0);
		}
		
		return map;
	}
	
	
	double getAvgBaseP(String s){
		Annotation document = new Annotation(s);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    Tree t =sentences.get(0).get(TreeAnnotation.class);
	    List<Tree> list = t.postOrderNodeList();
	    double avg = 0;
	    int count =0;
	    ArrayList<Double> bottom = new ArrayList<Double>();
	    for(Tree tt:list){
	    	
	    	double sum =0;
	    	if (Double.isNaN(tt.score()))
	    		continue;
	    	List<Tree> children = tt.getChildrenAsList();
	    	boolean hasNaN = false;
	    	for(Tree ttt:children){
	    		if(Double.isNaN(ttt.score()) )
	    			hasNaN = true;
	    	}
	    	
	    	if (hasNaN&&tt.score()>-30){
	    		bottom.add(tt.score());
	    	}
	    }
	    Double[] bottoms = (Double[])bottom.toArray(new Double[0]);
	    Arrays.sort(bottoms);
	    double sum = 0;
	    for(int i=0;i<10;i++){
	    	sum+=bottoms[i];
	    	System.out.print(bottoms[i]);
	    }
	    System.out.println();
	    
	   // System.out.println(worst);
		return 1.0*sum/5;
	}
	

}
