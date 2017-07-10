package nlpProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.stanford.nlp.trees.Tree;
import stanfordCoreNlp.StanfordParser;

public class Test {

	private StanfordParser parser;
	private HashMap<String, Integer> ruleId;

	public interface SqlBuilder {
		String buildSql (String x, String y);
	}

	public Test() {
		parser = new StanfordParser();
		ruleId = new HashMap<String, Integer>();
		populateRulesInMap();
	}

	public void populateRulesInMap() {
		ruleId.put("S -> VBZ NP NP .", 1);
		ruleId.put("NP -> NNP", 2);
		ruleId.put("NP -> DT NN", 3);
	}

	public void attachSemantics(String rule) {
		int n = ruleId.get(rule);
		switch (n) {
		//case 1: attachSemantics();
		}
	}

	public void parseTree(String s) {

		int nodeNum = 0;

		String rule="";
		Tree t = parser.parse(s).get(0);

		Queue<Tree> treeQueue= new LinkedList<Tree>();
		Queue<String> prodQueue = new LinkedList<String>();


		List<String> rulesList = new ArrayList<String>();
		Tree[] child = t.children();

		treeQueue.add(child[0]);
		nodeNum++;
		prodQueue.add(child[0].label()+""+nodeNum+"");


		while(!treeQueue.isEmpty()) {
			rule = "";
			Tree tree = treeQueue.remove();
			Tree[] children = tree.children();
			rule = rule + prodQueue.remove()+" -> ";
			for(int i=0;i<children.length;i++) {
				if(!tree.isPreTerminal()) {
					treeQueue.add(children[i]);

					nodeNum++;
					prodQueue.add(children[i].label()+""+nodeNum);
				}
				if(!tree.isPreTerminal()) {
					rule = rule + children[i].label()+""+nodeNum+" ";
				}
				else {
					rule = rule + children[i].yieldHasWord().get(0).word();
				}


			}
			System.out.println(rule);
		}
	}

	public static void main(String args[]) throws IOException {

		String testFile = "D:/q2.txt";
		List<String> questions = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(testFile));
		String line;
		while((line = br.readLine())!=null) {
			questions.add(line);
		}


		/*for(String question: questions) {
			//System.out.println(question);
			//System.out.println(sem.getQuestionType(question));
			//System.out.println(sem.parser.getPosTags(question));
			sem.getGrammar(question);
			System.out.println("====================================");
		}
		String q = "Is Kubrick an actor?";
		sem.getGrammar(q);
		//System.out.println(sem.getQuestionType(q));
		//sem.buildQuery(q);
		*/
		
		
		Test t = new Test();
		for(String question: questions) {
			System.out.println(question);
			t.parseTree(question);
			System.out.println("======================================================");
		}
	}
}