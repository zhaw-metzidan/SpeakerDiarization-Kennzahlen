package mysql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import kpi.Session;
import kpi.Speaker;
import kpi.Speech;

public class MysqlDBConnector {

	private String databaseSchema;
	private String myDriver = "com.mysql.cj.jdbc.Driver";
	private String myUrl = "jdbc:mysql://localhost:3306?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private String username = "root";
	private String password = "root";
	private Connection connect = null;
	private Statement statement = null;
	
	/**
	 * Constructor.
	 * Will create connection to database upon initialization.
	 */
	public MysqlDBConnector(String databaseSchema) {
		this.databaseSchema = databaseSchema;
		connect = connectToDB();
	}
	
	/**
	 * create a MySQL database connection
	 * @return 
	 */
	private Connection connectToDB() {
		try {
			//load the MySQL driver
			Class.forName(myDriver);  
			return DriverManager.getConnection(myUrl,username,password);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * insert session into database
	 * @param session
	 * @throws SQLException
	 */
	public void insertSession(Session session) throws SQLException {

		String query = " insert into "+databaseSchema+".session values (?, ?, ?, ?, ?)";
		
		// create the mysql insert preparedstatement
		PreparedStatement preparedStmt = this.connect .prepareStatement(query);
		preparedStmt.setString (1, null);
		preparedStmt.setInt (2, session.getSessionTimeTalk());
		preparedStmt.setInt (3, session.getTotalSpeeches());
		preparedStmt.setDouble (4, session.getTotalAverageSpeechTime());
		preparedStmt.setDouble (5, session.getAverageSpeechcountPerSpeaker());
		preparedStmt.execute();
		preparedStmt.close();
	}
	
	/**
	 * select id of session from database and return value as integer
	 * @return id of session
	 * @throws SQLException
	 */
	public int getLastSessionID() throws SQLException {
		
		String query = " SELECT * FROM "+databaseSchema+".session ORDER BY idSession DESC LIMIT 1";
		
		statement = connect.createStatement();
		ResultSet rs = statement.executeQuery(query);
		int idSession=0;
		while (rs.next()) {
			idSession = rs.getInt("idSession");
		}
		statement.close();
		return idSession;
	}
	
	/**
	 * insert speaker into database
	 * @param speaker
	 * @throws SQLException
	 */
	public void insertSpeaker(Speaker speaker) throws SQLException {
		
		String query = " insert into "+databaseSchema+".speaker values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		// create the mysql insert prepared statement
		PreparedStatement preparedStmt = this.connect .prepareStatement(query);
		preparedStmt.setString (1, null);
		preparedStmt.setString (2, speaker.getName());
		preparedStmt.setInt (3, speaker.getTotalSpeechCount());
		preparedStmt.setInt (4, speaker.getTotalTimeTalk());
		preparedStmt.setInt (5, speaker.getTotalTimeSilent());
		preparedStmt.setInt (6, speaker.getLongestSpeech());
		preparedStmt.setInt (7, speaker.getShortestSpeech());
		preparedStmt.setDouble (8, speaker.getAverageSpeechTime());
		preparedStmt.setDouble (9, speaker.getAverageSimilarity());
		preparedStmt.execute();
		preparedStmt.close();
	}
	
	/**
	 * select id of speaker from database and return value as integer
	 * @return id of speaker
	 * @throws SQLException
	 */
	public int getLastSpeakerID() throws SQLException {
		
		String query = " SELECT * FROM "+databaseSchema+".speaker ORDER BY idspeaker DESC LIMIT 1";
		
		statement = connect.createStatement();
		ResultSet rs = statement.executeQuery(query);
		int idSpeaker=0;
		while (rs.next()) {
			idSpeaker = rs.getInt("idspeaker");
		}
		statement.close();
		return idSpeaker;
	}
	
	/**
	 * Insert into junction table of session and speaker
	 * @param id_session
	 * @param id_speaker
	 * @throws SQLException
	 */
	public void insertJunctionSessionSpeaker(int id_session, int id_speaker) throws SQLException {
		
		String query = " insert into "+databaseSchema+".session_speaker values (?, ?)";
		
		// create the mysql insert prepared statement
		PreparedStatement preparedStmt = this.connect .prepareStatement(query);
		preparedStmt.setInt (1, id_session);
		preparedStmt.setInt (2, id_speaker);
		preparedStmt.execute();
		preparedStmt.close();
	}
	

	/**
	 * insert speech into database
	 * @param speechStart
	 * @param speechTime
	 * @param speechEnd
	 * @param similarity
	 * @throws SQLException
	 */
	public void insertSpeech(Speech speech) throws SQLException {
		
		String query = " insert into "+databaseSchema+".speech values (?, ?, ?, ?, ?)";
		
		// create the mysql insert prepared statement
		PreparedStatement preparedStmt = this.connect .prepareStatement(query);
		preparedStmt.setString (1, null);
		preparedStmt.setInt (2, speech.getSpeechStart());
		preparedStmt.setInt (3, speech.getSpeechTime());
		preparedStmt.setInt (4, speech.getSpeechEnd());
		preparedStmt.setInt (5, speech.getAverageSimilarity());
		preparedStmt.execute();
		preparedStmt.close();
	}
	
	/**
	 * Insert into junction table of session and speaker
	 * @param id_speaker
	 * @param id_speech
	 * @throws SQLException
	 */
	public void insertJunctionSpeakerSpeech(int id_speaker, int id_speech) throws SQLException {
		
		String query = " insert into "+databaseSchema+".speaker_speech values (?, ?)";
		
		// create the mysql insert prepared statement
		PreparedStatement preparedStmt = this.connect .prepareStatement(query);
		preparedStmt.setInt (1, id_speaker);
		preparedStmt.setInt (2, id_speech);
		preparedStmt.execute();
		preparedStmt.close();
	}
	
	/**
	 * select id of speech from database and return value as integer
	 * @return id of speech
	 * @throws SQLException
	 */
	public int getLastSpeechID() throws SQLException {
		
		String query = " SELECT * FROM "+databaseSchema+".speech ORDER BY idspeech DESC LIMIT 1";
		
		statement = connect.createStatement();
		ResultSet rs = statement.executeQuery(query);
		int idSpeech=0;
		while (rs.next()) {
			idSpeech = rs.getInt("idspeech");
		}
		statement.close();
		return idSpeech;
	}
	
	/**
	 * insert timeline value with previous speaker that just finished speaking and new speaker that just started speaking
	 * @param idPreviousSpeaker
	 * @param idCurrentSpeaker
	 * @throws SQLException
	 */
	public void insertSpeakerTimeLine(int idPreviousSpeaker, int idCurrentSpeaker) throws SQLException {

		String query = " insert into "+databaseSchema+".speaker_timeline values (?, ?)";
		
		// create the mysql insert prepared statement
		PreparedStatement preparedStmt = this.connect .prepareStatement(query);
		preparedStmt.setInt (1, idPreviousSpeaker);
		preparedStmt.setInt (2, idCurrentSpeaker);
		preparedStmt.execute();
		preparedStmt.close();
	}
	
	/**
	 * close DB connection
	 */
	public void closeConnection(){
		try {
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
