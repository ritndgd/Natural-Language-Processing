package nlpProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import stanfordCoreNlp.StanfordParser;

public class ReadData {
	
	public static void main(String[] args) throws IOException {
		String testFile = args[0];
		BufferedReader br = new BufferedReader(new FileReader(testFile));
		StanfordParser parser = new StanfordParser();
		DomainInference infer = new DomainInference();
		String line;
		List<String> questions = new ArrayList<String>();
		List<String> domains = new ArrayList<String>();
		List<String> parseTrees = new ArrayList<String>();
		
		while((line = br.readLine())!=null) {
			questions.add(line);
		}
		
		for(String question:questions) {
			Tree parseTree = parser.parse(question).get(0);
			domains.add(infer.getDomain(question, parseTree));
			parseTrees.add(parseTree.toString());
		}
		
		for(int i=0;i<questions.size();i++) {
			System.out.println("<QUESTION> "+questions.get(i)+"\n<CATEGORY> "+domains.get(i)+"\n<PARSETREE> "+parseTrees.get(i)+"\n\n\n");
		}
		//System.out.println(infer.getDomain("Was Birdman the best movie in 2015?", infer.parse("Was Birdman the best movie in 2015?").get(0)));
	}
}