/*package nlpProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.AccessDatabase;

import java.util.Queue;
import java.util.Scanner;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.Tree;
import stanfordCoreNlp.StanfordParser;

public class Semantics {

	private StanfordParser parser;
	private HashMap<Integer, String> tagSequenceMap;
	private HashMap<Integer, String> queryMap;
	private AccessDatabase db;

	public interface SqlBuilder {
		String buildSql (String x, String y);
	}

	public Semantics() {
		parser = new StanfordParser();
		tagSequenceMap = new HashMap<Integer, String>();
		queryMap = new HashMap<Integer, String>();
		db = new AccessDatabase();
		readPropertiesFromFlies();
	}

	public void readPropertiesFromFlies() {
		try {
			InputStream ir = DomainInference.class.getResourceAsStream("/resources/posTags.txt");
			//InputStream ir = DomainInference.class.getResourceAsStream("src/resources/posTags.txt");
			//BufferedReader br = new BufferedReader(new FileReader("src/resources/posTags.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(ir));
			String line;
			while((line=br.readLine())!=null) {
				String[] lineStr = line.split(" ");
				int key = Integer.parseInt(lineStr[0]);
				String tagSequence = lineStr[1];
				tagSequenceMap.put(key, tagSequence.trim());
			}

			InputStream irSql = DomainInference.class.getResourceAsStream("/resources/semanticQueries.txt");
			//InputStream irSql = DomainInference.class.getResourceAsStream("src/resources/semanticQueries.txt");
			//BufferedReader brSql = new BufferedReader(new FileReader("src/resources/semanticQueries.txt")); 
			BufferedReader brSql = new BufferedReader(new InputStreamReader(irSql));
			String lineSql;
			while((line=brSql.readLine())!=null) {
				int space=0;
				while(line.charAt(space)!=' ') {
					space++;
				}

				String num = line.substring(0, space);
				String query = line.substring(space+1, line.length());

				queryMap.put(Integer.parseInt(num), query);
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int getQuestionType(String question) {

		String posTags = parser.getPosTags(question).trim();

		for(Entry<Integer, String> mapEntry:tagSequenceMap.entrySet()) {
			if(mapEntry.getValue().equals(posTags)) {
				return mapEntry.getKey();
			}
		}
		return -1;
	}

	public void getGrammar(String s) {

		String rule="";
		Tree t = parser.parse(s).get(0);
		Queue<Tree> treeQueue= new LinkedList<Tree>();

		List<String> rulesList = new ArrayList<String>();
		Tree[] child = t.children();
		treeQueue.add(child[0]);

		while(!treeQueue.isEmpty()) {
			rule = "";
			Tree tree = treeQueue.remove();
			Tree[] children = tree.children();
			if(!tree.isPreTerminal()) {
				rule = rule + tree.label()+" -> ";
				for(int i=0;i<children.length;i++) {
					rule = rule + children[i].label()+" ";
					treeQueue.add(children[i]);
				}
				rulesList.add(rule);
			}
		}

		System.out.println(rulesList.toString());

		List<TaggedWord> taggedWord= t.taggedYield();
		for(TaggedWord tw:taggedWord) {
			System.out.println(tw.tag()+" -> "+tw.word());
		}
	}

	public void buildQuery(String question) {
		try {
			String posTags = parser.getPosTags(question).trim();
			StringBuilder q = new StringBuilder(question);
			q.deleteCharAt(q.length()-1);
			question = q.toString();

			String[] wordsArray = question.split(" ");
			String[] posTagsArray = posTags.split(",");

			Map<String, String> countryMap = new HashMap<String, String>();
			countryMap.put("French", "France");
			countryMap.put("German", "Germany");
			countryMap.put("British", "UK");
			countryMap.put("Italian", "Italy");
			countryMap.put("American", "USA");

			Map<String, String> bestAwardMap = new HashMap<String, String>();
			bestAwardMap.put("actor", "ACTOR");
			bestAwardMap.put("actress", "ACTRESS");
			bestAwardMap.put("director", "DIRECTOR");


			HashMap<String, String> roleMap = new HashMap<String, String>();
			roleMap.put("actor", "actor");
			roleMap.put("actress", "actor");
			roleMap.put("movie", "movie");
			roleMap.put("director", "director");

			HashMap<String, String> tagWordPairs = new HashMap<String, String>();
			for(int i=0;i<wordsArray.length;i++) {
				tagWordPairs.put(posTagsArray[i], wordsArray[i]);
			}
			int n = getQuestionType(question);
			if(n<1||n>13) {
				throw new Exception();
			}
			String query = queryMap.get(getQuestionType(question));

			switch(getQuestionType(question)){

			case 1:
				query = query.replace("role", roleMap.get(tagWordPairs.get("NN4")));
				query = query.replace("X", tagWordPairs.get("NNP2"));
				break;
			case 2:
				query = query.replace("X", tagWordPairs.get("NNP2"));
				break;
			case 3:
				query = query.replace("X", tagWordPairs.get("NNP2"));
				query = query.replace("Y", tagWordPairs.get("NNP5"));
				break;
			case 4:
				query = query.replace("X", tagWordPairs.get("NNP2"));
				query = query.replace("Y", tagWordPairs.get("CD7"));
				break;
				case 5:
			query = query.replace("X", tagWordPairs.get("POS6"));
			query = query.replace("Y", tagWordPairs.get("NNP2"));
			case 6:
				String queryCopy = query;

				query = query.replace("role", "Person");
				query = query.replace("X", tagWordPairs.get("NNP2"));
				query = query.replace("Y", tagWordPairs.get("CD7"));

				queryCopy = queryCopy.replace("role", "Movie");
				queryCopy = queryCopy.replace(" X%", tagWordPairs.get("NNP2"));
				queryCopy = queryCopy.replace("Y", tagWordPairs.get("CD7"));
				//Hit both.
				break;

			case 7:
				query = query.replace("Y", countryMap.get(tagWordPairs.get("JJ3")));
				query = query.replace("Z", bestAwardMap.get(tagWordPairs.get("NN4")));
				break;

			case 8:
				query = query.replace("X", tagWordPairs.get("NNP5"));
				break;
				case 9:
			query = query.replace("", "");
			case 10:

				query = query.replace("X", bestAwardMap.get(tagWordPairs.get("NN7")));
				query = query.replace("Y", tagWordPairs.get("CD9"));
				break;

			case 11:
				query = query.replace("X", tagWordPairs.get("CD7"));
				break;

			case 12:
				if(tagWordPairs.get("NN2").toLowerCase().equals("movie")){
					query = query.replace("role", "movie");
					query = query.replace("X", "PICTURE");
				}else if(tagWordPairs.get("NN2").toLowerCase().equals("director")){
					query = query.replace("role", "person");
					query = query.replace("X", "DIRECTOR");
				}else{
					query = query.replace("role", "person");
					query = query.replace("X", bestAwardMap.get(tagWordPairs.get("NN2")));
				}
				query = query.replace("Y", tagWordPairs.get("CD7"));
				break;
			case 13:
				query = query.replace("X", bestAwardMap.get(tagWordPairs.get("NN9")));
				query = query.replace("Y", tagWordPairs.get("NNP3"));
			}
			
			System.out.println("<SQL>\n"+query+"\n");
			
			
			System.out.println("<ANSWER>");
			if(n>=1&&n<=8) {
				String ans = db.runQuery(query);
				int ansInt = Integer.parseInt(ans);
				if(ansInt>0) {
					System.out.println("yes");
				}
				else {
					System.out.println("no");
				}
			}
			else {
				System.out.println(db.runQuery(query));
			}
				
		}
		catch(Exception e) {
			System.out.println("I don't know");
		}
		//db.runQuery(query); 
	}

	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);
		Semantics sem = new Semantics();
		String q="";
		System.out.println("Welcome! This is MiniWatson!\n\nPlease ask a question. Type 'q' when finished.");
		while(true) {
			q = sc.nextLine();
			if(q.equals("q")) {
				return;
			}
			System.out.println("<QUERY>\n"+q+"\n");
			sem.buildQuery(q);
			System.out.println("\n");
		}
	}
}*/