package kpi;

import java.util.ArrayList;
import java.util.List;

public class Speaker {

	// instance variables
	private String speakerName;
	private List<Speech> speechList;
	private int dbID;
	//KPI
	private int totalSpeechCount;
	private int totalTimeTalk;
	private int totalTimeSilent;
	private int longestSpeech;
	private int shortestSpeech;
	private double averageSpeechTime;
	private double averageSimilarity;
	
	/**
	 * create new Speaker Object
	 * @param name of speaker
	 */
	public Speaker(String name) {
		this.speakerName = name;
		this.speechList = new ArrayList<Speech>();
		this.totalSpeechCount = 0;
		this.totalTimeTalk = 0;
		this.totalTimeSilent = 0;
		this.longestSpeech = 0;
		this.shortestSpeech = 0;
		this.averageSpeechTime = 0;
		this.dbID = 0;
	}
	/**
	 * calculate KPIs of speaker
	 * @param sessionTimeTalk total time of session including all speakers
	 */
	public void setKPIs(int sessionTimeTalk) {
		//reset KPIs to default value
		resetKPIs();
		
		// set totalSpeechCount
		this.totalSpeechCount = this.speechList.size();
		int counter=0;
		for (Speech speech : speechList) {
			if (speech.getSpeechTime() >= 1) {
				counter++;
			}
		}
		
		//iterate through speechList
		for (Speech speech : this.speechList) {
			
			//set totalTimeTalk
			this.totalTimeTalk += speech.getSpeechTime();
			
			// set longestSpeech
			if (this.longestSpeech < speech.getSpeechTime()) {
				this.longestSpeech = speech.getSpeechTime();
			}
			
			//set shortestSpeech
			if (this.shortestSpeech == 0 || this.shortestSpeech > speech.getSpeechTime()) {
				this.shortestSpeech = speech.getSpeechTime();
			}
			
			// addition of all speechtime values to later calculate the averageSpeechTime
			this.averageSpeechTime += speech.getSpeechTime();
//			if (this.averageSpeechTime == 0) {
//				this.averageSpeechTime = speech.getSpeechTime();
//			} else {
//				this.averageSpeechTime = (this.averageSpeechTime + speech.getSpeechTime())/2;
//			}

			//addition of all similarity values to later calculate the averageSimilarity
			this.averageSimilarity += speech.getAverageSimilarity();
			//calculate and set similarity
//			if (this.averageSimilarity == 0) {
//				this.averageSimilarity = speech.getAverageSimilarity();
//			} else {
//				this.averageSimilarity = (this.averageSimilarity + speech.getAverageSimilarity())/2;
//			}
		}
		
		// set totalTimeSilent
		this.totalTimeSilent = sessionTimeTalk - this.totalTimeTalk;
		
		// calculate average values
		this.averageSpeechTime = this.averageSpeechTime / counter; //calculate average time but ignore registered speeches that take less than 1 second
		this.averageSimilarity = this.averageSimilarity  / this.totalSpeechCount;
	}
	
	/**
	 * Reset KPIs to default value. 
	 */
	private void resetKPIs() {
		this.totalSpeechCount = 0;
		this.totalTimeTalk = 0;
		this.totalTimeSilent = 0;
		this.longestSpeech = 0;
		this.shortestSpeech = 0;
		this.averageSpeechTime = 0;
		this.averageSimilarity = 0;
	}

	/**
	 * Add speech to speech list
	 * @param speech
	 */
	public void addSpeechList(Speech speech) {
		this.speechList.add(speech);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return speakerName;
	}

	/**
	 * @return the speechList
	 */
	public List<Speech> getSpeechList() {
		return speechList;
	}


	/**
	 * @return the totalSpeechCount
	 */
	public int getTotalSpeechCount() {
		return totalSpeechCount;
	}


	/**
	 * @return the totalTimeTalk
	 */
	public int getTotalTimeTalk() {
		return totalTimeTalk;
	}


	/**
	 * @return the totalTimeSilent
	 */
	public int getTotalTimeSilent() {
		return totalTimeSilent;
	}


	/**
	 * @return the longestSpeech
	 */
	public int getLongestSpeech() {
		return longestSpeech;
	}


	/**
	 * @return the shortestSpeech
	 */
	public int getShortestSpeech() {
		return shortestSpeech;
	}


	/**
	 * @return the averageSpeechTime
	 */
	public double getAverageSpeechTime() {
		return averageSpeechTime;
	}

	/**
	 * @return the averageSimilarity
	 */
	public double getAverageSimilarity() {
		return averageSimilarity;
	}
	/**
	 * value of ID from database
	 * @return the dbID
	 */
	public int getDbID() {
		return dbID;
	}
	/**
	 * set with id value from database
	 * @param dbID the dbID to set
	 */
	public void setDbID(int dbID) {
		this.dbID = dbID;
	}
	
}
