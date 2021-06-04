package kpi;

import java.util.ArrayList;
import java.util.List;

public class Session {

	// instance variables
	private List<String> sessionTimeline;
	private List<Speaker> speakerList;
	//KPI
	private int sessionTimeTalk;
	private int totalSpeeches;
	private double totalAverageSpeechTime;
	private double averageSpeechcountPerSpeaker;
	
	/**
	 * Create session object to set instance variables
	 */
	public Session() {
		this.sessionTimeTalk = 0;
		this.totalSpeeches = 0;
		this.totalAverageSpeechTime = 0;
		this.averageSpeechcountPerSpeaker = 0;
		this.sessionTimeline = new ArrayList<String>();
		this.speakerList = new ArrayList<Speaker>();
	}
	
	/**
	 * Set KPIs of this session
	 * Create speaker list
	 * 
	 */
	public void setSessionKPI(ArrayList<Speech> speechList) {
		
		for (Speech speech : speechList) {
			
			//count number of speeches
			this.totalSpeeches++; 
			
			// format and add speech to TimeLine
			this.sessionTimeline.add(speech.getSpeakerName()+","
					+ " Speech: "+this.totalSpeeches+","
					+ " TimeStart: "+speech.getSpeechStart()+"s,"
					+ " TimeEnd: "+speech.getSpeechEnd()+"s,"
					+ " SpeechDuration: "+speech.getSpeechTime()+"s,"
					+ " Similarity: "+speech.getAverageSimilarity()+"%");
			
			
			// add speech time to  total time talking in this session
			this.sessionTimeTalk += speech.getSpeechTime();
			
			// calculate average speech time of all speeches
			if (this.totalAverageSpeechTime == 0) {
				this.totalAverageSpeechTime = speech.getSpeechTime(); // set totalAverageSpeechTime for the first time
			} else {
				this.totalAverageSpeechTime = (this.totalAverageSpeechTime +  speech.getSpeechTime()) / 2; // calculate new totalAverageSpeechTime
			}
			
			// create new Speaker objects
			if (this.speakerList.size() == 0) {
				
				Speaker firstSpeaker = new Speaker(speech.getSpeakerName());
				firstSpeaker.addSpeechList(speech);
				this.speakerList.add(firstSpeaker);
			}
			//at least one speaker has been added before
			else {
				boolean newSpeakerBool = true; // true = new speaker identified, false = no new speaker identified
				// iterate through list of previously added Speakers
				for (Speaker speaker : this.speakerList) {
					
					//if name of previously added speaker is the same as current speaker, then add speech to current speaker but don't create a new speaker
					if (speaker.getName().equals(speech.getSpeakerName())) {
						speaker.addSpeechList(speech);
						newSpeakerBool = false; // no new speaker identified in this speech
					}
				}
				// if speaker has not been added before, create new speaker and add to list
				if (newSpeakerBool) {
					Speaker newSpeaker = new Speaker(speech.getSpeakerName()); //create new speaker
					newSpeaker.addSpeechList(speech);	// add speech to speaker
					this.speakerList.add(newSpeaker);	// add speaker to speaker list
				}
			}
		}
		
		// calculate averageSpeechcountPerSpeaker by dividing number of all speeches by number of speakers 
		this.averageSpeechcountPerSpeaker = this.totalSpeeches / this.speakerList.size();
	}
	
	/**
	 * Set KPI of all Speakers
	 */
	public void setSpeakerKPI() {
		for (Speaker speaker : this.speakerList) {
			speaker.setKPIs(this.sessionTimeTalk);
		}
	}

	/**
	 * @return the sessionTimeTalk
	 */
	public int getSessionTimeTalk() {
		return sessionTimeTalk;
	}

	/**
	 * @return the sessionTimeline
	 */
	public List<String> getSessionTimeline() {
		return sessionTimeline;
	}

	/**
	 * @return the speakerList
	 */
	public List<Speaker> getSpeakerList() {
		return speakerList;
	}

	/**
	 * @return the totalSpeeches
	 */
	public int getTotalSpeeches() {
		return totalSpeeches;
	}

	/**
	 * @return the totalAverageSpeechTime
	 */
	public double getTotalAverageSpeechTime() {
		return totalAverageSpeechTime;
	}

	/**
	 * @return the averageSpeechcountPerSpeaker
	 */
	public double getAverageSpeechcountPerSpeaker() {
		return averageSpeechcountPerSpeaker;
	}
	
}
