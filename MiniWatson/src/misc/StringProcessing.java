package misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import database.AccessDatabase;

public class StringProcessing {
	
	List<String> movieList;
	List<String> albumList;
	List<String> trackList;
	AccessDatabase db = new AccessDatabase();
	
	HashMap<String, String> replacements;
	
	HashMap<String, String> movieNamesProcessed;
	HashMap<String, String> movieNamesProcessedReverse;
	
	HashMap<String, String> albumNamesProcessed;
	HashMap<String, String> albumNamesProcessedReverse;
	
	HashMap<String, String> trackNamesProcessed;
	HashMap<String, String> trackNamesProcessedReverse;
	
	public StringProcessing() {
		
		movieNamesProcessed = new HashMap<String, String>();
		movieNamesProcessedReverse = new HashMap<String, String>();
		
		albumNamesProcessed = new HashMap<String, String>();
		albumNamesProcessedReverse = new HashMap<String, String>();
		
		trackNamesProcessed = new HashMap<String, String>();
		trackNamesProcessedReverse = new HashMap<String, String>();
		
		replacements = new HashMap<String, String>();
		
		replacements.put("-", "MinusSign");
		replacements.put(":","Colon");
		replacements.put(",","Coma");
		replacements.put("0", "NumZero");
		replacements.put("1", "NumOne");
		replacements.put("2", "NumTwo");
		replacements.put("3", "NumThree");
		replacements.put("4", "NumFour");
		replacements.put("5", "NumFive");
		replacements.put("6", "NumSix");
		replacements.put("7", "NumSeven");
		replacements.put("8", "NumEight");
		replacements.put("9", "NumNine");
		
		movieList = db.getAllMovieNames();
		albumList = db.getAllAlbumNames();
		trackList = db.getAllTrackNames();
		
		addProcessedMoviesToMap();
		addProcessedAlbumsToMap();
		addProcessedTracksToMap();
	}
	
	public String processMovieMusicAlbumName(String entity) {
		entity = entity.replace(" ", "");
		for (Entry<String, String> entry : replacements.entrySet()) {
			entity = entity.replace(entry.getKey(), entry.getValue());
		}
		return entity;
	}
	
	public void addProcessedMoviesToMap() {
		for(int i=0;i<movieList.size();i++) {
			String movie = movieList.get(i);
			movieNamesProcessed.put(movie, processMovieMusicAlbumName(movie));
			movieNamesProcessedReverse.put(processMovieMusicAlbumName(movie), movie);
		}
	}
	
	public void addProcessedAlbumsToMap() {
		for(int i=0;i<albumList.size();i++) {
			String album = albumList.get(i);
			albumNamesProcessed.put(album, processMovieMusicAlbumName(album));
			albumNamesProcessedReverse.put(processMovieMusicAlbumName(album), album);
		}
	}
	
	public void addProcessedTracksToMap() {
		for(int i=0;i<trackList.size();i++) {
			String track = trackList.get(i);
			trackNamesProcessed.put(track, processMovieMusicAlbumName(track));
			trackNamesProcessedReverse.put(processMovieMusicAlbumName(track), track);
		}
	}
	
	public void replaceMovieNameInQuestion(String question) {
		for (Entry<String, String> entry : movieNamesProcessed.entrySet()) {
		    question = question.replace(entry.getKey(), entry.getValue());
		}
		System.out.println(question);
	}
	
	public void replaceAlbumNameInQuestion(String question) {
		for (Entry<String, String> entry : albumNamesProcessed.entrySet()) {
		    question = question.replace(entry.getKey(), entry.getValue());
		}
		System.out.println(question);
	}
	
	public void replaceTrackNameInQuestion(String question) {
		for (Entry<String, String> entry : trackNamesProcessed.entrySet()) {
		    question = question.replace(entry.getKey(), entry.getValue());
		}
		System.out.println(question);
	}
	
	public static boolean isSmall(char c) {
		if(c>='a' && c<='z') {
			return true;
		}
		return false;
	}
	
	public static boolean isCapitalOrNum(char c) {
		if(c>='A' && c<='Z' || c>'0' && c<'9') {
			return true;
		}
		return false;
	}
	
	public String preProcess(String question) {
		
		StringBuilder qBuilder  = new StringBuilder(question);
		
		int i=0;
		boolean lastWordWasCapital = false;
		while(qBuilder.charAt(i)!= ' ') {
			i++;
		}
		
		while(i<qBuilder.length()) {
			if(lastWordWasCapital) {
				if(qBuilder.charAt(i)==' ' && isCapitalOrNum(qBuilder.charAt(i+1))) {
					qBuilder.deleteCharAt(i);
				}
			}
			if(qBuilder.charAt(i)==' ' && isCapitalOrNum(qBuilder.charAt(i+1))) {
				lastWordWasCapital = true;
			}
			
			if(qBuilder.charAt(i)==' ' && isSmall(qBuilder.charAt(i+1))) {
				lastWordWasCapital = false;
			}
			
			i++;
		}
		
		return new String(qBuilder);
	}
	
	
	public String postProcess(String s) {
		StringBuilder sBuilder = new StringBuilder(s);
		int i=1;
		while(i<sBuilder.length()) {
			if(isSmall(sBuilder.charAt(i-1)) && isCapitalOrNum(sBuilder.charAt(i))) {
				sBuilder.insert(i, " ");
			}
			i++;
		}
		return new String(sBuilder);
	}
	
	public static void main(String args[]) {
		StringProcessing s = new StringProcessing();
		System.out.println(s.postProcess("Did DeNiro direct SpiderMan2"));
		System.out.println(s.postProcess("Spider"));
	}
}
