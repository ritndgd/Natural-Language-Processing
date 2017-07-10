package nlpProject;

import java.util.Scanner;

public class InitialClass {
	
	public static String currentQuery = new String();
	public static String sqlQuery = null;

	public static void main(String[] args) 
	{
		System.out.println("Welcome! This is MiniWatson.");
		System.out.println("Please ask a question. Type 'q' when finished.");
		System.out.println();
		String input;
		Scanner keyboard = new Scanner(System.in);
		do{		
			input = keyboard.nextLine().trim();
			
			if(!input.equalsIgnoreCase("q")){
				currentQuery = input;
				System.out.println("<QUERY>\n" + currentQuery);
				//TODO perform any query processing
				printSQL(); //TODO implement method below
				printAnswer(); //TODO implement method below
				System.out.println();
			}
		}while(!input.equalsIgnoreCase("q"));
		
		keyboard.close();
		System.out.println("Goodbye.");

	}
	
	public static void printSQL(){
		//TODO update this to get and print appropriate SQL
		sqlQuery = ""; // map currentQuery to sqlQuery
		System.out.println("<SQL>\n" + sqlQuery);
	}
	public static void printAnswer(){
		String answer = ""; // execute sqlQuery in order to generate appropriate natural language response 
		System.out.println("<ANSWER>\n" + answer);
	}

}