package edu.cmu.lti.bic.langstat.project.pcfg;


import java.util.Collection;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

class ParserDemo {

  /**
   * The main method demonstrates the easiest way to load a parser.
   * Simply call loadModel and specify the path, which can either be a
   * file or any resource in the classpath.  For example, this
   * demonstrates loading from the models jar file, which you need to
   * include in the classpath for ParserDemo to work.
 * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishFactored.ser.gz");
    LexicalizedParser lp2 = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    if (args.length > 0) {
      demoDP(lp, args[0]);
    } else {
      demoAPI2(lp,lp2);
    }
  }

  /**
   * demoDP demonstrates turning a file into tokens and then parse
   * trees.  Note that the trees are printed by calling pennPrint on
   * the Tree object.  It is also possible to pass a PrintWriter to
   * pennPrint if you want to capture the output.
   */
  public static void demoDP(LexicalizedParser lp, String filename) {
    // This option shows loading and sentence-segmenting and tokenizing
    // a file using DocumentPreprocessor.
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    // You could also create a tokenizer here (as below) and pass it
    // to DocumentPreprocessor
    for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
      Tree parse = lp.apply(sentence);
      parse.pennPrint();
      System.out.println();

      GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
      Collection tdl = gs.typedDependenciesCCprocessed();
      System.out.println(tdl);
      System.out.println();
    }
  }

  /**
   * demoAPI demonstrates other ways of calling the parser with
   * already tokenized text, or in some cases, raw text that needs to
   * be tokenized as a single sentence.  Output is handled with a
   * TreePrint object.  Note that the options used when creating the
   * TreePrint can determine what results to print out.  Once again,
   * one can capture the output by passing a PrintWriter to
   * TreePrint.printTree.
 * @throws IOException 
   */
  public static void demoAPI2(LexicalizedParser lp1,LexicalizedParser lp2) throws IOException{
	  BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	  while(true){
		  String s = bufferRead.readLine();
		  if(s.equals("exit"))
			  break;
		  s=s.toLowerCase();
		  TokenizerFactory<CoreLabel> tokenizerFactory =
			      PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
			    List<CoreLabel> rawWords =
			      tokenizerFactory.getTokenizer(new StringReader(s)).tokenize();
		    Tree parse = lp1.apply(rawWords);
		    List<Tree> base = parse.getLeaves();
		    for(Tree t:base){
		    	System.out.print(t.ancestor(1, parse)+" ");
		    }
		    System.out.println(parse.score());
		    parse = lp2.apply(rawWords);
		    base = parse.getLeaves();
		    for(Tree t:base){
		    	System.out.print(t.ancestor(1, parse)+" ");
		    }
		    System.out.println(parse.score());
	  }  

  }
  
  public static void demoAPI(LexicalizedParser lp) {
    // This option shows parsing a list of correctly tokenized words
    String[] sent = { "This", "is", "an", "easy", "sentence", "." };
    List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
    Tree parse = lp.apply(rawWords);
    parse.pennPrint();
    System.out.println(parse.score());
    System.out.println();

    // This option shows loading and using an explicit tokenizer
    String sent2 = "IT'S A GREAT LINE THAT I RESIGN SCHOOL BUT THE PRESIDENT HIS TAX BILL PASSED DAYS NOW THE PEOPLE IT'S A TRAGEDY TO SEE.";
    TokenizerFactory<CoreLabel> tokenizerFactory =
      PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    List<CoreLabel> rawWords2 =
      tokenizerFactory.getTokenizer(new StringReader(sent2)).tokenize();
    parse = lp.apply(rawWords2);
    System.out.println(parse.score());
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
    System.out.println(tdl);
    System.out.println();

    TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
    tp.printTree(parse);
  }

  private ParserDemo() {} // static methods only

}
