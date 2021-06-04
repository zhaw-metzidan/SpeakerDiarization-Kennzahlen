package kpi;

import org.json.simple.JSONObject;

public class Speech {

	// instance variables
	private String speakerName;
	private int dbId;
	private int jsonID;
	
	//KPI
	private int speechStart;
	private int speechTime;
	private int speechEnd;
	private int averageSimilarity;
	
	/**
	 * create new Speech object with values, speakerName, ID, speechStart, speechTime, speechEnd
	 * @param values in form of a JSON object
	 */
	public Speech(JSONObject values) {
		this.speakerName = (String) values.get("Speaker_Name");
		this.jsonID = Integer.parseInt(values.get("ID").toString());
		this.speechStart = Integer.parseInt(values.get("SpeechStart").toString());
		this.speechTime = Integer.parseInt(values.get("SpeechTime").toString());
		this.speechEnd = Integer.parseInt(values.get("SpeechEnd").toString());
		this.averageSimilarity = Integer.parseInt(values.get("Similarity").toString());
	}

	/**
	 * @return the speaker_Name
	 */
	public String getSpeakerName() {
		return speakerName;
	}

	/**
	 * @return the iD
	 */
	public int getJsonId() {
		return jsonID;
	}

	/**
	 * @return the speechStart
	 */
	public int getSpeechStart() {
		return speechStart;
	}

	/**
	 * @return the speechTimeTotal
	 */
	public int getSpeechTime() {
		return speechTime;
	}

	/**
	 * @return the speechEnd
	 */
	public int getSpeechEnd() {
		return speechEnd;
	}

	/**
	 * @return the similarity
	 */
	public int getAverageSimilarity() {
		return averageSimilarity;
	}

	/**
	 * @return the dbID
	 */
	public int getDbId() {
		return dbId;
	}

	/**
	 * @param dbId the dbID to set
	 */
	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	
}
