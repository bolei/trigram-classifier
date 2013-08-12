package edu.cmu.lti.bic.langstat.project.pcfg;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import edu.cmu.lti.bic.langstat.project.pipeline.Article;
import edu.cmu.lti.bic.langstat.project.pipeline.Pipeline;
import edu.cmu.lti.bic.langstat.project.pipeline.RunPipeline;
import edu.cmu.lti.bic.langstat.project.similar.Similarity;
import edu.stanford.nlp.pipeline.*;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;

import edu.stanford.nlp.util.CoreMap;


public class JustForTest {
	private static final String TRAIN_SET = "/data/developmentSet.dat";
	private static final String TRAIN_SET_TAG = "/data/developmentSetLabels.dat";
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		collectRLowBaseP();
		//getSelfSimilarity();
		//trainPOS();
		//collectLowP();
		//getAllTokenNum();
		//getPandCount();
		//collectVarMean();
		//collectAllInfo();
		/*ArrayList<CoreMap> sents = readAllInfo();
		System.out.println("done");
		System.out.println(sents.get(0).get(TreeAnnotation.class).score());
		System.out.println(sents.get(1).get(TreeAnnotation.class).score());*/
		
	}
	static void  collectAllInfo() throws IOException{
		//FileOutputStream fout = new FileOutputStream("object.out");
		ObjectOutputStream oos = null;// = new ObjectOutputStream(fout); 
		BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		PCFGParser p = new PCFGParser();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		ArrayList<CoreMap> sents= new ArrayList<CoreMap>();
		int count = 0;
		int file = 0;
		for(Article a:artList){
			if(count%100==0){
				if(oos!=null){
					oos.writeObject(sents);
					sents.clear();
					oos.close();
				}	
				oos = new ObjectOutputStream(new FileOutputStream("object"+file+".out"));
				file++;
				
			}
			for(String s:a.getSentences()){
				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				CoreMap core = p.getAnnotatedSentence(s);
				sents.add(core);			
			}
			
			System.out.println(count++);
		}
		long startTime = System.nanoTime();
		
		oos.writeObject(sents);
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println(duration);
		oos.close();
	}
	static ArrayList<CoreMap>  readAllInfo() throws IOException, ClassNotFoundException{
		FileInputStream fin = new FileInputStream("object.out");
		   ObjectInputStream ois = new ObjectInputStream(fin);
		   ArrayList<CoreMap> sents= (ArrayList<CoreMap>) ois.readObject();
		   ois.close();
		   return sents;
	}

   static void collectVarMean() throws IOException, ClassNotFoundException{
	   BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		FileWriter fstream = new FileWriter("outVarMean.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		PCFGParser p = new PCFGParser();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		SentenceRreader reader = new SentenceRreader();
		int count = 0;
		for(Article a:artList){
			double var = 0;
			double mean = 0;
			ArrayList<Double> scores = new ArrayList<Double>();
			for(String s:a.getSentences()){
				CoreMap sent = reader.getNext();
				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				int numOfWords = s.split(" +").length;
				if (numOfWords<20)
					continue;
				Tree t = sent.get(TreeAnnotation.class);
				double tmp =  t.score()/t.postOrderNodeList().size();
				scores.add(tmp);
			}
			mean = getMean(scores);
			var = getVar(scores,mean);
			String toConsole = count++ +" "+mean+" "+var;
			System.out.println(toConsole);
			out.write(toConsole);
			out.newLine();
		}
		out.close();  
   }
	
   static double getMean(ArrayList<Double> nums){
	   double sum =0;
	   for(double a:nums)
		   sum+=a;
	   return sum/nums.size();
   }
   
   static double getVar(ArrayList<Double> nums, double mean){
	   double sum =0;
	   for(double a:nums)
		   sum+=(a-mean)*(a-mean);
	   return sum/nums.size();
   }
	
	
	static void  collectFeature() throws IOException{
		BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		
		FileWriter fstream = new FileWriter("out1.2.txt");
		FileWriter fstream2 = new FileWriter("out2.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		BufferedWriter out2 = new BufferedWriter(fstream2);
		
		
		PCFGParser p = new PCFGParser();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		int count = 0;
		for(Article a:artList){
			double score = 0;
			for(String s:a.getSentences()){
				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				int numOfWords = s.split(" +").length;
				if (numOfWords<20)
					continue;
				double tmp = p.getScore(s);
				score += tmp;
				//sSystem.out.println(score+" "+numOfWords);
				//out.write(tmp+" ");
			}
			//out.newLine();
			String toConsole = score/a.getSentences().size()+" "+a.getLabel();
			System.out.println(count+++" "+toConsole);
			out2.write(toConsole);
			out2.newLine();
		}
		out.close();
		out2.close();
		
	}
	
	
	
	static void  collectLowP() throws IOException{
		BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		
		FileWriter fstream = new FileWriter("out1.2.txt");
		FileWriter fstream2 = new FileWriter("out2.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		BufferedWriter out2 = new BufferedWriter(fstream2);
		
		PCFGParser p = new PCFGParser();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		int count = 0;
		for(Article a:artList){
			double score = 0;
			for(String s:a.getSentences()){
				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				int numOfWords = s.split(" +").length;
				double tmp = p.getBase2(s);
				score+=tmp;
				//System.out.println(tmp+ " "+a.getLabel());
				
				//double tmp = p.getAllPos(s)/numOfWords;
				//System.out.println(s);
				//System.out.println(tmp+" "+a.getLabel());
				//sSystem.out.println(score+" "+numOfWords);
				//out.write(tmp+" ");
			}
			//out.newLine();
			String toConsole = score/a.getSentences().size()+" "+a.getLabel();
			System.out.println(toConsole);
			out2.newLine();
		}
		out.close();
		out2.close();
		
	}
	static void  collectRLowBaseP() throws IOException, ClassNotFoundException{
		BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		
		FileWriter fstream = new FileWriter("out1.2.txt");
		FileWriter fstream2 = new FileWriter("out2.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		BufferedWriter out2 = new BufferedWriter(fstream2);
		
		PCFGParser p = new PCFGParser();
		FileInputStream fin = new FileInputStream("data/posP.ob");
		   ObjectInputStream ois = new ObjectInputStream(fin);
		   p.posMap =  (TreeMap<String, HashMap<String, Double>>) ois.readObject();;
		   ois.close();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		int count = 0;
		for(Article a:artList){
			double score = 0;
			for(String s:a.getSentences()){
				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				s= s.toLowerCase();
				int numOfWords = s.split(" +").length;
				//System.out.print(s);
				double tmp = p.getScore(s);
				//score+=tmp;
				System.out.println(tmp+ " "+a.getLabel());
				
				//double tmp = p.getAllPos(s)/numOfWords;
				//System.out.println(s);
				//System.out.println(tmp+" "+a.getLabel());
				//sSystem.out.println(score+" "+numOfWords);
				//out.write(tmp+" ");
			}
			//out.newLine();
			String toConsole = score/a.getSentences().size()+" "+a.getLabel();
			//System.out.println(toConsole);
			out2.newLine();
		}
		out.close();
		out2.close();
		
	}
	static void  getSelfSimilarity() throws IOException{
		BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		
		FileWriter fstream = new FileWriter("out1.2.txt");
		FileWriter fstream2 = new FileWriter("out2.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		BufferedWriter out2 = new BufferedWriter(fstream2);
		Similarity semi = new Similarity();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		int count = 0;
		for(Article a:artList){
			double score = 0;
			for(String s:a.getSentences()){
				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				int numOfWords = s.split(" +").length;
				if(numOfWords<3)
					continue;
				double tmp = semi.getSelfMaxSimilarity(s);
				System.out.print(s);
				System.out.println(tmp+ " "+a.getLabel());
				
				//double tmp = p.getAllPos(s)/numOfWords;
				//System.out.println(s);
				//System.out.println(tmp+" "+a.getLabel());
				//sSystem.out.println(score+" "+numOfWords);
				//out.write(tmp+" ");
			}
			//out.newLine();
			//String toConsole = score/a.getSentences().size()+" "+a.getLabel();
			//System.out.println(toConsole);
			out2.newLine();
		}
		out.close();
		out2.close();
		
	}
	
	static public void getPandCount() throws IOException{
		BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		
		FileWriter fstream = new FileWriter("out1.2.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		BufferedReader in = new BufferedReader(new FileReader("out.txt"));
		
		
		//PCFGParser p = new PCFGParser();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		int count = 0;
		for(Article a:artList){
			double score = 0;
			String onLine = in.readLine();
			String[] words = onLine.split(" ");
			int j=0;
			for(String s:a.getSentences()){

				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				int numOfWords = s.split(" +").length;
				//System.out.println();
				out.write(words[j++]+" "+numOfWords+" "+a.getLabel());
				out.newLine();
			}
		}
		out.close();
		in.close();
	}
	
	static void getAllTokenNum() throws IOException{
		BufferedReader dataIn = null, tagIn = null;
		dataIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET)));
		tagIn = new BufferedReader(new InputStreamReader(RunPipeline.class.getResourceAsStream(TRAIN_SET_TAG)));
		
		FileWriter fstream = new FileWriter("numOfToken.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		
		
		//PCFGParser p = new PCFGParser();
		List<Article> artList = Article.loadArticles(dataIn, tagIn, true);
		System.out.println("done");
		for(Article a:artList){
			int num = 0;
			for(String s:a.getSentences()){
				s= s.replaceFirst("<s>", "");
				s= s.replaceFirst("</s>", ".");
				int numOfWords = s.split(" +").length;
				num+=numOfWords;
			}
			out.write(num+"");
			out.newLine();
		}
		out.close();
		
	}
	static void trainPOS() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("data/LM-train-100MW.txt"));
		trainPosProbability posTrain = new trainPosProbability();
		String s = br.readLine();
		int count=0;
		int gap=100000/100;
		while(s!=null){
			if(count==100000)
				break;
			if(count%gap==0)
				System.out.println(count);
			count++;
			s= s.replaceFirst("<s>", "");
			s= s.replaceFirst("</s>s", "");
			s=s.toLowerCase();
			posTrain.check(s);
			s = br.readLine();
			
		}
		TreeMap<String, HashMap<String, Double>> map = posTrain.finish();
		FileOutputStream fout = new FileOutputStream("data/posP.ob");
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(map);
		oos.close();
		System.out.println("Done");
	}

}
