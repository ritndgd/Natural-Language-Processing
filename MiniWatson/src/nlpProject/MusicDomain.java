package nlpProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import database.AccessDatabase;
import misc.StringProcessing;
import stanfordCoreNlp.StanfordParser;

public class MusicDomain {

	private StanfordParser parser;
	private StringProcessing process;
	private HashMap<String, Integer> posTags;
	private HashMap<String, String> tagWordPairs;
	private AccessDatabase db;

	public MusicDomain() {
		db = new AccessDatabase();

		process = new StringProcessing();

		parser = new StanfordParser();

		tagWordPairs = new HashMap<String, String>();

		posTags = new HashMap<String, Integer>();

		posTags.put("VBD1,NNP2,VB3,NNP4", 1);
		posTags.put("VBD1,NNP2,VB3,NN4", 2);
		posTags.put("VBZ1,DT2,NN3,NN4,VBP5,DT6,NN7,NNP8", 3);
		posTags.put("VBZ1,DT2,NN3,NN4,VBP5,DT6,NN7,NN8", 4);
		posTags.put("VBZ1,DT2,NN3,NNP4,VBP5,DT6,NN7,NNP8", 5);
		posTags.put("VBZ1,DT2,NN3,NNP4,VBP5,DT6,NN7,NN8", 6);
		posTags.put("VBZ1,DT2,NN3,JJ4,VBP5,DT6,NN7,NNP8", 7);
		posTags.put("VBZ1,DT2,NN3,JJ4,VBP5,DT6,NN7,NN8", 8);
		posTags.put("VBD1,NNP2,VBN3,IN4,DT5,NNP6", 9);
		posTags.put("VBD1,NNP2,VBN3,IN4,NNP5", 10);
	}

	public String getQueryDidArtistSingTrack(String artist, String track) {
		return "SELECT count(*) "
				+ "FROM Artist art, Album albm, Track trk "
				+ "WHERE art.id = albm.artsitID AND trk.albumID = albm.albumID AND albm.name LIKE '%"+artist+"%'  AND trk.name LIKE '%"+track+"%'";
	}

	public String getQueryArtistBornInCountry(String artist, String ctry) {
		return "SELECT count(*) "
				+ "FROM Artist a "
				+ "WHERE a.name LIKE '%"+artist+"%' AND a.placeOfBith LIKE '%"+ctry+"%'";
	}

	public String getQueryAlbumContainsTrack(String album, String track) {
		return "SELECT count(*) "
				+ "FROM Artist art, Album albm, Track trk "
				+ "WHERE art.id = albm.artsitID AND trk.albumID = albm.albumID AND albm.name LIKE '%"+album+"%'  AND trk.name LIKE '%"+track+"%'";
	}

	public void buildQuery(String question) {
		String questionPosTags = parser.getPosTags(question);
		try {
			if(!posTags.containsKey(questionPosTags)) {
				throw new Exception();
			}
			question = question.substring(0, question.length()-1);
			int qType = posTags.get(questionPosTags);

			String[] wordsArray = question.split(" ");
			String[] posTagsArray = questionPosTags.split(",");

			HashMap<String, String> tagWordPairs = new HashMap<String, String>();
			for(int i=0;i<wordsArray.length;i++) {
				tagWordPairs.put(posTagsArray[i], wordsArray[i]);
			}

			String query = "";
			String artist = "";
			String track = "";
			String album = "";
			String ctry = "";

			switch(qType) {

			case 1:
				artist = process.postProcess(tagWordPairs.get("NNP2"));
				track = process.postProcess(tagWordPairs.get("NNP4"));
				query = getQueryDidArtistSingTrack(artist, track);
				break;

			case 2:
				artist = process.postProcess(tagWordPairs.get("NNP2"));
				track = process.postProcess(tagWordPairs.get("NN4"));
				query = getQueryDidArtistSingTrack(artist, track);
				break;

			case 3:
				album = process.postProcess(tagWordPairs.get("NN4"));
				track = process.postProcess(tagWordPairs.get("NNP8"));
				query = getQueryDidArtistSingTrack(album, track);
				break;

			case 4: 
				album = process.postProcess(tagWordPairs.get("NN4"));
				track = process.postProcess(tagWordPairs.get("NN8"));
				query = getQueryDidArtistSingTrack(album, track);
				break;

			case 5:
				album = process.postProcess(tagWordPairs.get("NNP4"));
				track = process.postProcess(tagWordPairs.get("NNP8"));
				query = getQueryDidArtistSingTrack(album, track);
				break;

			case 6:
				album = process.postProcess(tagWordPairs.get("NNP4"));
				track = process.postProcess(tagWordPairs.get("NN8"));
				query = getQueryDidArtistSingTrack(album, track);
				break;

			case 7:
				album = process.postProcess(tagWordPairs.get("JJ4"));
				track = process.postProcess(tagWordPairs.get("NNP8"));
				query = getQueryDidArtistSingTrack(album, track);
				break;

			case 8: 
				album = process.postProcess(tagWordPairs.get("JJ4"));
				track = process.postProcess(tagWordPairs.get("NN8"));
				query = getQueryDidArtistSingTrack(album, track);
				break;

			case 9:
				artist = process.postProcess(tagWordPairs.get("NNP2"));
				ctry = process.postProcess(tagWordPairs.get("NNP6"));
				query = getQueryArtistBornInCountry(artist, ctry);
				break;

			case 10:
				artist = process.postProcess(tagWordPairs.get("NNP2"));
				ctry = process.postProcess(tagWordPairs.get("NNP5"));
				query = getQueryArtistBornInCountry(artist, ctry);
				break;
			}
			
			System.out.println(query);
			
			String ans = db.runMusicQuery(query);
			int ansInt = Integer.parseInt(ans);
			if(ansInt==0) {
				System.out.println("No");
			}
			else if(ansInt>=1) {
				System.out.println("Yes");
			}
			else {
				throw new Exception();
			}
		}
		catch(Exception e) {
			//System.out.println("I don't know!");
			e.printStackTrace();
		}
	}


	public static void main(String args[]) throws IOException {
		String testFile = "D:/abc.txt";
		BufferedReader br = new BufferedReader(new FileReader(testFile));
		StanfordParser parser = new StanfordParser();
		StringProcessing process = new StringProcessing();

		String line;
		List<String> questions = new ArrayList<String>();

		while((line = br.readLine())!=null) {
			questions.add(process.preProcess(line));
			System.out.println(process.preProcess(line));
		}

		MusicDomain m = new MusicDomain();
		for(int i=0;i<questions.size();i++) {
			m.buildQuery(questions.get(i));
		}
	}
}