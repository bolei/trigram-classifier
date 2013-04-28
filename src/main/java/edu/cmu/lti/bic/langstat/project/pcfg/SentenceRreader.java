package edu.cmu.lti.bic.langstat.project.pcfg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import edu.stanford.nlp.util.CoreMap;

public class SentenceRreader {
	int count = 0;
	String name = "object";
	FileInputStream fin = null;
	int index = 0;
	ArrayList<CoreMap> sents = null;
	CoreMap getNext() throws IOException, ClassNotFoundException{
		
		if (sents!=null&&index==sents.size())
			count++;	
		if(fin==null||index==sents.size()){
		   index = 0;
		   System.out.println("reading.... "+name+count+".out");
			long startTime = System.nanoTime();
			sents = null;
		   fin = new FileInputStream(name+count+".out");
		   ObjectInputStream ois = new ObjectInputStream(fin);	
		   sents = (ArrayList<CoreMap>) ois.readObject();

			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			System.out.println(duration/1000000000+"s");
		   ois.close();
		}
		return sents.get(index++);		
	}
}
