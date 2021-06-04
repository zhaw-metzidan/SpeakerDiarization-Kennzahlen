package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import kpi.Speech;

public class LogsParser {

	private String filepathLog = ".\\RedenIstGoldLogs\\";
	private String filepathJSON = ".\\json";
	
	/**
	 * 
	 */
	public LogsParser() {
	}
	
	/**
	 * Extract speeches from logs and create a JSON file for speeches
	 * @param logFileName log file
	 * @param outputJsonFileName json file Name for output
	 */
	public void logsToJson(String logFileName, String outputJsonFileName) {
		try {
			ArrayList<String> speechLogs = parseLogs(logFileName);
			writeJSONFile(speechLogs, outputJsonFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parse logs from logfile and convert to a simplified list of speeches
	 * Combines log entries of multiple frames into a single speech String to carry relevant information
	 * Relevant information = speaker name, speech time (start, duration, end), average similarity of all frames per speech
	 * @param log filename
	 * @return list of logs per speech
	 * @throws Exception 
	 */
	private ArrayList<String> parseLogs(String logFileName) {
		
		ArrayList<String> speechList = new ArrayList<String>();
		try {
			ArrayList<String> output = readFile(logFileName);
			
			String previousSpeakerName = "empty";	// set as "empty" to identify the first new speaker
			String previousLog = "";
			String firstLogOfNewSpeech = "";
			double similarity = 0.0;
			DecimalFormat df2 = new DecimalFormat("###"); // to format similarity
			int lineCounter = 0; // counts amount of parsed lines until a new speaker is identified. used to calculate average similarity
			
			for (int logRow = 0; logRow < output.size(); logRow++) {
				lineCounter++;
				
				String[] splitString = output.get(logRow).split("[,]", 0); //split logRow by commas
				
				//for the very first line of the logs
				if (previousSpeakerName.equals("empty")) {		
					previousSpeakerName = splitString[4];		// set the very first previous speaker
					firstLogOfNewSpeech = output.get(logRow);
				}
				if (splitString[4].equals(previousSpeakerName)) {	// if speaker of current LogRow is still the same as the previous logRow, then it is still the same speech
					
					previousLog = output.get(logRow); // Temporarily save current log
					similarity += Double.parseDouble(splitString[2]); // addition of average similarity values
					
				} else {		// if the speaker is different the speech ended and a new speaker is talking
					similarity = similarity / lineCounter; // calculate average similarity
					previousSpeakerName = splitString[4];		// get name form log to temporarily store previous speaker
					
					speechList.add(previousLog+","+df2.format(similarity*100)+","+firstLogOfNewSpeech.split("[,]", 0)[3]); 	// add last LogRow of speaker to list and add average similarity (%) separated by a comma. add speechstart time at the end
					//variables for next iteration
					firstLogOfNewSpeech =  output.get(logRow);
					lineCounter = 0; // reset lineCounter since a new speaker has been found
					previousLog = output.get(logRow);
					similarity = Double.parseDouble(splitString[2]);	// set new similarity
				}
			}
			//when iteration finished add the last speaker. this is needed one last time because no new speaker was identified but the previous spekaer still needs to be added to the list
			similarity = similarity / (lineCounter+1); // calculate average similarity
			
			speechList.add(previousLog+","+df2.format(similarity*100)+","+firstLogOfNewSpeech.split("[,]", 0)[3]); 	// add last LogRow of speaker to list and add average similarity (%) separated by a comma
			
		} catch (FileNotFoundException e) {
			System.out.println("Log File Not Found!");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return speechList;
	}
	
	/**
	 * create JSON objects for each speech used to write in a JSON file later
	 * @param speechList
	 * @return ArrayList of all JSON objects
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<JSONObject> createJSON(ArrayList<String> speechList) {
		
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		int totalSessionTimer = 0; // used to measure timestamps of each speech
		int id = 0;  // unique identifier for each speech (per session)
		
		for (int i = 0; i < speechList.size(); i++) {
			
			String[] splitString = speechList.get(i).split("[,]", 0); // split string by commas into arrays
			
			JSONObject speakerJson = new JSONObject(); // new json object
						
			//ID
			speakerJson.put("ID", id); // set json value
			id++;
			
			//Name
			speakerJson.put("Speaker_Name", splitString[4]);
			
			//  Speech start
			if (totalSessionTimer == 0) {
				speakerJson.put("SpeechStart", 0);
			}
			else {
				speakerJson.put("SpeechStart", totalSessionTimer);
			}
			
			// Total speech time in seconds
			String[] splitTime = splitString[3].split("[:]", 0);
			int speechTime = Integer.parseInt(splitTime[0])*3600 + Integer.parseInt(splitTime[1])*60 + Integer.parseInt(splitTime[2].split("[.]", 0)[0]);
			splitTime = splitString[6].split("[:]", 0);
			int startTime = Integer.parseInt(splitTime[0])*3600 + Integer.parseInt(splitTime[1])*60 + Integer.parseInt(splitTime[2].split("[.]", 0)[0]);
			speechTime = speechTime - startTime;
//			System.out.println("m: "+Integer.parseInt(spitTime[1]));
//			System.out.println("s: "+spitTime[2].split("[.]", 0)[0]);
//			System.out.println("time in seconds = "+Integer.parseInt(spitTime[0])*3600 + Integer.parseInt(spitTime[1])*60 + Integer.parseInt(spitTime[2].split("[.]", 0)[0]));
			speakerJson.put("SpeechTime", speechTime);
			
			totalSessionTimer += speechTime;
		    
		    // Speech end
			speakerJson.put("SpeechEnd", totalSessionTimer);
			
			// Average Similarity
			speakerJson.put("Similarity", Integer.parseInt(splitString[5]));
//			System.out.println("----");
//			System.out.println(Integer.parseInt(speakerJson.get("SpeechStart").toString()));
//			System.out.println(Integer.parseInt(speakerJson.get("SpeechTime").toString()));
//			System.out.println(Integer.parseInt(speakerJson.get("SpeechEnd").toString()));
		    jsonList.add(speakerJson);
		}
		
	    return jsonList;
	}
	
	/**
	 * write json Objects into File
	 * @param speechLogs
	 * @param outputJsonFileName
	 */
	@SuppressWarnings("unchecked")
	private void writeJSONFile(ArrayList<String> speechLogs, String outputJsonFileName) {
		
		ArrayList<JSONObject> speakerJSON = createJSON(speechLogs);
		
		FileWriter file = null;
		JSONObject speech = new JSONObject();
		speech.put("speech", speakerJSON);
		try {
			file = new FileWriter(filepathJSON+"\\"+outputJsonFileName);
//			String fileName = new SimpleDateFormat("'output-'yyyyMMddHHmm'.json'").format(new Date());
			file.write(speech.toJSONString());
		} catch (IOException  e) {
			 e.printStackTrace();
		} finally {
			try {
				file.flush();
	            file.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	/**
	 * read logfile line by line and return as arraylist
	 * @param logFileName
	 * @return ArrayList with all log entries
	 * @throws Exception
	 */
	private ArrayList<String> readFile(String logFileName) throws Exception{
		ArrayList<String> output = new ArrayList<String>();
		
		File file = new File(filepathLog+logFileName); 
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		
		//read file line by line and add string to arraylist
		while ((line = br.readLine()) != null) {
			output.add(line);
		}
		br.close();
		return output;
	}
}
