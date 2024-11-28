import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
                          

/**
 * References
 * 
 * 
 * Sounds used
 * https://samplefocus.com/samples/lo-fi-punch [accessed 02/05/2021]
 * https://www.myinstants.com/instant/substitution-jutsu-57585/?utm_source=copy&utm_medium=share [accessed 02/05/2021]
 * 
 * Images used
 * Smoke cloud gif: https://herzenswarme.tumblr.com/post/183299391798/naruto-passion-of-the-shinobi-a-naruto [accessed 02/04/2021]
 * 
 * 
 * 
 * 
 *
 */

public class Main implements ActionListener{
	
	//Variables
	
	//So its easier to reference and change later
	int frameHeight = 900;
	int frameWidth = 1600;
	
	//The different screens 
	JFrame startFrame;
	JFrame infoFrame;
	JFrame gameFrame;
	JFrame endFrame;
	
	
	
	
	boolean inGameTime = false;
	
	JButton startButton;
	JButton exitButton;
	JButton infoButton;
	JButton Return;
	JButton restartButton;
	JButton btmenuButton;
	
	//setting colours for the button
	Color buttonBG = new Color(120, 77, 36);
	Color buttonFG = new Color(255, 255, 255);
	Color textColour = new Color(245, 194, 66);
	
	
	Cursor myCursor;
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Image crosshairImg = toolkit.getImage("./Assets\\hammer.png");
	
	JLabel timeLeftText;
	JLabel scoreText;
	
	//Images
	ImageIcon titleImage;
	ImageIcon title = new ImageIcon("./Assets\\TitleScreen.png");
	ImageIcon bg = new ImageIcon("./Assets\\BambooBackground.png"); //each screen will have the same background
	ImageIcon infoBg = new ImageIcon("./Assets\\instructionsScreen.png");
	
	
	JLabel bgLabel = new JLabel("",bg,JLabel.CENTER);//each screen will use the same background
	
	Hole[] holes = new Hole[9]; //Holes are set up in their own class
	
	String buttonSoundPath = ".//Assets\\ButtonSound.wav";
	
	Font ninjaFont;
	
	int timeLeft = 30;
	Timer gameTimer;
	int score = 0;
	
	public Main(){ //try load the font by finding the file. If this fails it will perform the catch function instead
		try {
			//found
			ninjaFont = Font.createFont(Font.TRUETYPE_FONT, new File("./Assets\\aAsianNinja.ttf")).deriveFont(50f);//Set the font and size
			//System.out.println("font  found");
		}//in the case of an error
		catch(Exception e){
			System.out.println("font not found");
		}
		
		File soundFile = new File(".//Assets\\BackgroundMusic.wav");
		try {
			
			//Make a new audio input stream directed to the sound file
			AudioInputStream InStream = AudioSystem.getAudioInputStream(soundFile);
			
			//sound clip resource loads the input stream and then plays it
			Clip clip = AudioSystem.getClip();
			
			clip.open(InStream);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-20); // Reduce volume by a given amount from the function parameters
			clip.loop(Clip.LOOP_CONTINUOUSLY); //allows the music to play continuously 
			clip.start();
			
		}catch(Exception e) { //If it fails to play then prints the error message
			System.out.println("Something went wrong playing the sound");
		}
		
		
		Init(); //load the first screen

		

	}
	
	//method to call the start screen
	public void Init() {
		
		bgLabel.setIcon(bg);
		bgLabel.removeAll();
		
		startFrame = new JFrame("Whack a Ninja-Mole");
		startFrame.setSize(frameWidth, frameHeight);
		startFrame.setVisible(true);
		startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		startFrame.getContentPane().setLayout(null);
		
		inGameTime = false;
		
		bgLabel.setBounds(0, 0, frameWidth, frameHeight);
		startFrame.add(bgLabel);
		
		JLabel titleLabel = new JLabel("",title,JLabel.CENTER);
		titleLabel.setBounds(0,0,frameWidth,frameHeight);
		bgLabel.add(titleLabel);
		
		startButton = new JButton("PLAY");
		startButton.setBounds(700, 500, 200, 75);
		
		//action listener would allow us to 
		startButton.addActionListener(this);
		startButton.setFont(ninjaFont);
		startButton.setBackground(buttonBG);
		startButton.setForeground(buttonFG);
		startButton.setBorder(null);
		
		infoButton = new JButton("Instructions");
		infoButton.setBounds(600, 640, 400, 75);
		infoButton.addActionListener(this);
		infoButton.setFont(ninjaFont);
		infoButton.setBackground(buttonBG);
		infoButton.setForeground(buttonFG);
		infoButton.setBorder(null);
		
		
		//takes away the blue border highlight or 'focus'
		startButton.setFocusPainted(false);
		infoButton.setFocusPainted(false);
		
		
		bgLabel.add(startButton);
		bgLabel.add(infoButton);
		bgLabel.repaint();
	}
	
	//method to switch to game screen
	public void Game() {
		
		bgLabel.setIcon(bg);
		bgLabel.removeAll();//remove the elements on the screen
		startFrame.dispose();//delete the start screen
		gameFrame = new JFrame("Whack a Ninja-Mole");
		gameFrame.setSize(frameWidth, frameHeight);
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.getContentPane().setLayout(null);
		
		
		
		myCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		myCursor = toolkit.createCustomCursor(crosshairImg, new Point(gameFrame.getX(),gameFrame.getY()), "img");
		
		gameFrame.setCursor(myCursor);
		
		
		bgLabel.setBounds(0, 0, frameWidth, frameHeight);
		gameFrame.add(bgLabel);
		
		timeLeftText = new JLabel("Time left: 30");
		timeLeftText.setBounds(90, 300, 300, 500);
		timeLeftText.setFont(ninjaFont);
		timeLeftText.setForeground(new Color(255,255,255));
		bgLabel.add(timeLeftText);
		
		scoreText = new JLabel("Score:");
		scoreText.setBounds(90, 500, 300, 500);
		scoreText.setFont(ninjaFont);
		scoreText.setForeground(new Color(255,255,255));
		bgLabel.add(scoreText);
		timer();
		
		GridLayout layout = new GridLayout(3,3,0,0);
		JPanel grid = new JPanel();
		grid.setLayout(layout);
		grid.setBackground(buttonBG);
		grid.setOpaque(false);
		grid.setBounds(frameWidth/2-300, frameHeight/2-150, 600, 600);
		bgLabel.add(grid);
		
		//set up holes
		for(int i=0;i<holes.length;i++) {
			//creating a new hole
			holes[i] = new Hole();
			
			//Setting the holes class to use this class as its parent so we can call the add to score function later
			holes[i].parent = this;
			grid.add(holes[i]);
			
		}
		
		
		
		bgLabel.repaint();
		
	}
	
	public void timer() {
		ActionListener action  = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//countdown until the game is over
				timeLeft = timeLeft - 1;
				//System.out.println(timeLeft);
				timeLeftText.setText("Time left: " + timeLeft);
				if(timeLeft >0) {
						Random noOfHolesToFill = new Random();
						int r = 1 + noOfHolesToFill.nextInt(3);
						for(int i = 0; i<r; i++) {
							Random holeToFill = new Random();
							int l = holeToFill.nextInt(9);
							if(holes[l].isFull == false) { //if the mole is empty then call the show method
								holes[l].ShowMole();
								}
							}
							
						}else if(timeLeft<=1) { //when we run out of time call the game over
							gameTimer.stop();
							GameOver(); //show the game over screen
						
						}
					
						
					}
			
				
				};
			gameTimer = new Timer(1000, action); //run the action we made every second
			gameTimer.start();
		
	}
	
	public void addToScore(int x) { //the hole class will call this method when pressed
		score = score + x;
		scoreText.setText("Score: " + score);
	}
	
	public void modifyTime(int x) {
		timeLeft = timeLeft + x;
	}
	
	
	
	public void Instructions() {
		bgLabel.setIcon(infoBg);
		bgLabel.removeAll();
		startFrame.dispose();
		
		infoFrame = new JFrame("Whack a Ninja-Mole");
		infoFrame.setSize(frameWidth, frameHeight);
		infoFrame.setVisible(true);
		infoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		infoFrame.getContentPane().setLayout(null);
		
		
		
		bgLabel.setBounds(0, 0, frameWidth, frameHeight);
		infoFrame.add(bgLabel);
		
		Return = new JButton("Back to Menu");
		Return.setBounds(600, 740, 400, 75);
		Return.addActionListener(this);
		Return.setFont(ninjaFont);
		Return.setBackground(buttonBG);
		Return.setForeground(buttonFG);
		Return.setBorder(null);
		Return.setFocusPainted(false);
		
		bgLabel.add(Return);
		
		
		
	}
	
	
	
	//Method to switch to game over screen
	public void GameOver() {
		bgLabel.removeAll();
		gameFrame.dispose();
		endFrame = new JFrame("Whack a Ninja-Mole");
		endFrame.setSize(frameWidth, frameHeight);
		endFrame.setVisible(true);
		endFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		endFrame.getContentPane().setLayout(null);
		
		
		
		bgLabel.setBounds(0, 0, frameWidth, frameHeight);
		endFrame.add(bgLabel);
		
		JLabel finalScore = new JLabel("Score: " + score,JLabel.CENTER);
		finalScore.setBounds(600, 140, 400, 75);
		finalScore.setFont(ninjaFont);
		finalScore.setForeground(new Color(255,255,255));
		
		restartButton = new JButton("Play again");
		restartButton.setBounds(600, 440, 400, 75);
		restartButton.addActionListener(this);
		restartButton.setFont(ninjaFont);
		restartButton.setBackground(buttonBG);
		restartButton.setForeground(buttonFG);
		restartButton.setBorder(null);
		restartButton.setFocusPainted(false);
		
		bgLabel.add(restartButton);
		
		btmenuButton = new JButton("Back to menu");
		btmenuButton.setBounds(600, 540, 400, 75);
		btmenuButton.addActionListener(this);
		btmenuButton.setFont(ninjaFont);
		btmenuButton.setBackground(buttonBG);
		btmenuButton.setForeground(buttonFG);
		btmenuButton.setBorder(null);
		btmenuButton.setFocusPainted(false);
		
		bgLabel.add(btmenuButton);
		
		
		
		
		int highScore = 0;
	    try {
	        BufferedReader reader = new BufferedReader(new FileReader(new File("./src\\score.txt")));
	        String line = reader.readLine();
	        while (line != null)                 // read the file line by line
	        {
	            try {
	                int scoreFromText = Integer.parseInt(line.trim());   // turn each line to an int
	                if (score > scoreFromText)  //if the score from the game is bigger than the one in the file,
	                { 	
	                    highScore = score; //set the new highscore to the score
	                    FileOutputStream writer = new FileOutputStream(new File("./src\\score.txt"));
	                    writer.write(highScore); //write it
	                    writer.close();
	                }else {
	                	
	                	//highScore = score;
	                	
	                }
	                
	            } catch (Exception error) {
	              System.out.println("invalid line");
	            }
	            line = reader.readLine();
	        }
	        reader.close();

	    } catch (Exception error) { //if it cant find the file print this error message
	        System.err.println("error reading scores from file");
	    }
		
		
		JLabel highScoreTxt = new JLabel("High Score: " + highScore,JLabel.CENTER);
		highScoreTxt.setBounds(600, 240, 400, 75);
		highScoreTxt.setFont(ninjaFont);
		highScoreTxt.setForeground(new Color(255,255,255));
		
		
		bgLabel.add(finalScore);
		bgLabel.add(highScoreTxt);
		bgLabel.repaint();
		
	}
	
	public void playSound(String n, float db) {
		//using the input parameter N we can use this for multiple sounds instead of having to make multiple methods
		File soundFile = new File(n);
		//Using try and catch in case it does not find the sound
		try {
			
			//Make a new audio input stream directed to the sound file
			AudioInputStream InStream = AudioSystem.getAudioInputStream(soundFile);
			
			//sound clip resource loads the input stream and then plays it
			Clip clip = AudioSystem.getClip();
			clip.open(InStream);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-db); // Reduce volume by a given amount from the function parameters
			clip.start();
		}catch(Exception e) { //If it fails to play then prints the error message
			System.out.println("Something went wrong playing the sound");
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource() == startButton) {
			Game();
		}
		if(e.getSource() == infoButton) {
			Instructions();
		}
		if(e.getSource() == Return) {
			infoFrame.dispose();
			Init();
		}
		if(e.getSource() == restartButton) {
			endFrame.dispose();
			timeLeft = 30;
			score = 0;
			Game();
		}
		if(e.getSource() == btmenuButton) {
			endFrame.dispose();
			timeLeft = 30;
			score = 0;
			Init();
		}
	}

}
