package json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kpi.Speech;

public class JsonParser {

	public JsonParser() {
	}
	
	/**
	 * Parse JSON file and return arraylist with speeches
	 * @param filename json file to parse
	 * @return ArrayList with speech objects
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Speech> parseJSON(String filename) {
		JSONParser jsonParser = new JSONParser();
		JSONArray jsonList = new JSONArray();
		ArrayList<Speech> speechList = new ArrayList<Speech>();
		
		try {
			Object obj = jsonParser.parse( new FileReader(".\\json\\"+filename));
			// A JSON object. 
			JSONObject jsonObject = (JSONObject) obj;
 
			// A JSON array. 
			jsonList = (JSONArray) jsonObject.get("speech");
 
			jsonList.add(obj);
 
			// iterate through all json objects to create Speech objects
			Iterator<JSONObject> iterator = jsonList.iterator();
			while (iterator.hasNext()) {
				JSONObject json = iterator.next();
				Speech speech = new Speech(json);
				speechList.add(speech);
			}
		} catch (StackOverflowError e ) {
			System.out.println("stackoverflow after last json object");
		} catch (NullPointerException e) {
//			System.out.println("NullPointerException");
		} catch (FileNotFoundException e) {
			System.out.println(" JSON File Not Found!!");
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return speechList;
	}
}
