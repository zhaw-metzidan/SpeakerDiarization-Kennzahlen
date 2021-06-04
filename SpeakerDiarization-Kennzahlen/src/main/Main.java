package main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import json.JsonParser;
import json.LogsParser;
import kpi.Session;
import kpi.Speaker;
import kpi.Speech;
import mysql.MysqlDBConnector;

public class Main {

	private static String databaseSchema = "`inselspiel-schema`"; 	// database schema name
	private static String jsonFile = "inselspiel1.json"; 			//name of JSON file to be parsed and created if needed (needed if logs need to be transformed to JSON)
	private static String logFile = "inselspiel1.log";				// name of log file (output from RedenIstGold)
	private static boolean parseNewLogs = true; 					// set to true if new logs should be parsed
	private static boolean printToConsole = false; 					// set to true to print all values into console
	private static boolean insertInDB = true; 						// if data should be inserted into database
	
	public static void main(String[] args) {
		
		// If boolean is set true, parse new logs
		if (parseNewLogs) {
			parseLogs(); // parse logs and transform into JSON file
		}
		// parse JSON file to create speechlist
		ArrayList<Speech> speechList = jsonParser();
		
		// create new Session object
		Session session = new Session();
		// calculate session KPI, including speaker KPI
		setSessionKpi(session, speechList);
		
		// print values into console
		if (printToConsole) {
			// print timeline of all speeches in console
			printTimeline(session.getSessionTimeline());
			// print each speaker in console
			printSpeakers(session.getSpeakerList());
			//print out each speaker with each of their speeches in console
			printSpeakerSpeeches(session.getSpeakerList());
		}
		
		// insert session, speaker and speech KPI into DB
		MysqlDBConnector dbcon = new MysqlDBConnector(databaseSchema);
		
		if (insertInDB) {
			System.out.println("Inserting values into databaseschema: "+databaseSchema +" ...");
			try {
				insertKpiIntoDB(session, dbcon);
				insertTimelineToDB(session, dbcon);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dbcon.closeConnection();
			}
			System.out.println("Inserting into database successful!");
			System.out.println("Done!");
		}
		else
			System.out.println("Data was not inserted into DB!\nDone!");
	}
	
	/**
	 * Parse logs and transform into JSON
	 */
	private static void parseLogs() {
		System.out.println("Parsing logs ...");
		// parse RedenIstGold logs and create JSON file
		LogsParser logParser = new LogsParser();
		logParser.logsToJson(logFile, jsonFile);
		System.out.println("Parsing logs successful!");
	}
	
	/**
	 * Parse JSON File and return list of speeches
	 * @return speechList: list of speech objects
	 */
	private static ArrayList<Speech> jsonParser() {
		System.out.println("Parsing JSON: " +jsonFile +" ...");
		JsonParser parser = new JsonParser();
		ArrayList<Speech> speechList = parser.parseJSON(jsonFile);
		System.out.println("Parsing JSON successful!");
		return speechList;
	}
	
	/**
	 * Calculate KPI for session and all speakers
	 * @param session
	 * @param speechList
	 */
	private static void setSessionKpi(Session session, ArrayList<Speech> speechList) {
		System.out.println("Calculating KPI ...");
		session.setSessionKPI(speechList); // calculate KPIs per session with list of speeches as input
		session.setSpeakerKPI();	// calculate KPIs for each speaker
		System.out.println("Calculating KPI successful!");
	}
	
	/**
	 * Connect to database and insert session, speaker and speech KPI into database
	 * @param session
	 * @param dbcon
	 * @throws SQLException
	 */
	private static void insertKpiIntoDB(Session session, MysqlDBConnector dbcon) throws SQLException {
		// insert session into database
		dbcon.insertSession(session);
		
		int sessionID = dbcon.getLastSessionID(); // get ID of last added session
		
		// iterate through each speaker
		for (Speaker speaker : session.getSpeakerList()) {
			dbcon.insertSpeaker(speaker); // insert speaker into database
			speaker.setDbID(dbcon.getLastSpeakerID()); //  get ID of last added speaker from database and set ID value for speaker object
			dbcon.insertJunctionSessionSpeaker(sessionID, speaker.getDbID()); // add junction for speaker and session
			
			// for each speech per speaker insert speech to database
			for (Speech speech : speaker.getSpeechList()) {
				// insert speeches into database
				dbcon.insertSpeech(speech);
				
				speech.setDbId(dbcon.getLastSpeechID()); // get ID of last added speech from database and set ID value for speech object
				
				dbcon.insertJunctionSpeakerSpeech(speaker.getDbID(), speech.getDbId()); // add junction for speech and speaker
			}
		}
	}
	
	/**
	 * Insert Timeline of speaker order into database
	 * @param session
	 * @param dbcon
	 * @throws SQLException
	 */
	private static void insertTimelineToDB(Session session, MysqlDBConnector dbcon) throws SQLException {
		List<String> timeline = session.getSessionTimeline();
		int previousSpeakerID = 0;
		int currentSpeakerID = 0;
		
		for (String string : timeline) {
			String[] splitString = string.split("[,]", 0);  // split string by commas, the first array will be just the name of the speaker

			for (Speaker speaker : session.getSpeakerList()) {
				if (speaker.getName().equals(splitString[0])) {
					currentSpeakerID = speaker.getDbID();
				}
			}
			if (previousSpeakerID == 0 || previousSpeakerID == currentSpeakerID) { // no change of current speaker or its the very first speaker
				previousSpeakerID = currentSpeakerID;
			}
			else {
				dbcon.insertSpeakerTimeLine(previousSpeakerID, currentSpeakerID);
				previousSpeakerID = currentSpeakerID;
			}
		}
	}
	
	/**
	 * print speech timeline in console
	 * @param timeline
	 */
	private static void printTimeline(List<String> timeline) {
		// print timeline of speeches in console
		System.out.println("---- Speech timeline ----");
		for (String speech : timeline){
			System.out.println(speech);
		}
		System.out.println("---- Timeline end ----");
	}
	
	/**
	 * print each speaker in console
	 * @param speakerlist
	 */
	private static void printSpeakers(List<Speaker> speakerlist) {
		System.out.println("---- Speakerlist ---- ");
		for (Speaker speaker : speakerlist) {
			System.out.println("Name: "+speaker.getName() 
			+", TotalSpeechCount: "+speaker.getTotalSpeechCount()
			+", TotalTimeTalk: "+speaker.getTotalTimeTalk()
			+", TotalTimeSilent: "+speaker.getTotalTimeSilent()
			+", LongestSpeech: "+speaker.getLongestSpeech()
			+", ShortestSpeech: "+speaker.getShortestSpeech()
			+", Avg.Similarity: "+ speaker.getAverageSimilarity() 
			+", Avg.speechTime: "+ speaker.getAverageSpeechTime() 
			);
		}
		System.out.println("---- Speakerlist End ---- ");
	}
	
	/**
	 * print out each speaker with each of their speeches in console
	 * @param speakerlist
	 */
	private static void printSpeakerSpeeches(List<Speaker> speakerlist) {
		System.out.println("---- Speaker + Speech ---- ");
		for (int i = 0; i < speakerlist.size() ; i++) {
			System.out.println("New Speaker: "+speakerlist.get(i).getName());
			for (Speech speech : speakerlist.get(i).getSpeechList()) {
				System.out.println("Speech: "+speech.getJsonId()+" :"+speech.getSpeechTime()+" Avg.Similarity: "+speech.getAverageSimilarity()+"%");
			}
		}
		System.out.println("---- Speaker + Speech End ---- ");
	}

}
