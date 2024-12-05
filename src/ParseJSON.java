import org.json.JSONException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class ParseJSON {
	static JSONObject obj = null;
	public static void main(String[] args) {
		String jsonString = "{highScore:0}";
		String filePath = "scores.json";
		
		// TODO Auto-generated method stub
		
		try {
			
			File file = new File(filePath);
			if(!file.exists()) {
				obj = new JSONObject(jsonString);
			}else {
				try {
					String content = new String(Files.readAllBytes(file.toPath()));
					obj = new JSONObject(content);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	
			System.out.println("\n\n---Parsing---\n\n");
			int highScore = obj.getInt("highScore");
			try {
				JSONArray scores = obj.getJSONArray("scores");
				System.out.println("Scores: " + scores);
			}catch(JSONException e) {
				e.printStackTrace();
				System.out.println("No Recorded Scores");
			}
			
			System.out.println("High Score: " + highScore);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getHighestScore() {
		try {
			int highScore = obj.getInt("highScore");
			return highScore;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		
	}
	
	
	
	public Object[][] getScoresData() throws JSONException{
		
		JSONArray scores = null;
		try {
			scores = obj.getJSONArray("scores");
			Object[][] tableData = new Object[scores.length()][2];
			for(int i = 0;i<scores.length();i++ ) {
				JSONObject entry = scores.getJSONObject(i);
				tableData[i][0] = entry.get("Date");
				tableData[i][1] = entry.getInt("score");
			}
			return tableData;
		}catch(JSONException e) {
			return null;
		}
	
	}
	
	public void saveScore(int score) throws JSONException {
	
	 if(score > obj.getInt("highScore")) {
		obj.put("highScore", score); 
	 }
	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
	 String date = LocalDateTime.now().format(formatter).toString();
	 JSONArray scores = null;
	try {
		scores = obj.getJSONArray("scores");
	}catch(JSONException e) {
		scores = new JSONArray();
	}
		 
	scores.put(new JSONObject().put("Date", date).put("score",score));
	 
	 
	 obj.put("scores",scores);
	 
	 try(FileWriter file = new FileWriter("scores.json")){
		 file.write(obj.toString(4));
		 System.out.println("JSON file created: " + obj);
	}
	 catch(IOException e) {
		e.printStackTrace();
		}
	}
	
	public boolean isNewHighScore(int score) {
		if(score > getHighestScore()) {
			return true;
		}else {
			return false;
		}
		
	}

}
