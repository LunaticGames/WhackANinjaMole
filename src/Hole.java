import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;


public class Hole extends JLabel implements ActionListener{
	//using a jlabel to hold a button which will contain our mole and allow it to be clickable
	
	ImageIcon hole = new ImageIcon("./Assets\\hole.png");
	ImageIcon smoke = new ImageIcon("./Assets\\smokecloudresized.gif");
	ImageIcon mole1 = new ImageIcon("./Assets\\graymole.png");
	ImageIcon mole2 = new ImageIcon("./Assets\\greenmole.png");
	ImageIcon mole3 = new ImageIcon("./Assets\\goldmole.png");
	ImageIcon goodScroll = new ImageIcon("./Assets\\Scroll.png");
	ImageIcon badScroll = new ImageIcon("./Assets\\Scroll2.png");
	
	ImageIcon moleToChoose;
	
	
	JLabel container = new JLabel("",JLabel.CENTER);
	JLabel Smoke = new JLabel("",smoke,JLabel.CENTER);
	JLabel Mole = new JLabel("",mole1,JLabel.CENTER);
	
	JButton btn = new JButton(hole);
	
	int smokeTime;
	Timer smokeDurationTimer;
	
	Main parent;
	
	String shadowCloneSound = ".//Assets\\shadow-clone-cancelation-sound-effect.wav";
	String impactSound  = ".//Assets\\WhackSound.wav";
	
	int worthPoints; //how much points its worth
	int score=0;
	
	int showDuration = 2;
	Timer showDurationTimer;
	
	boolean isFull = false;
	boolean isScroll = false;
	boolean scrollType = false; //False will be the good scroll that adds time, true will remove time
	boolean smokeTimerActive = false;
	
	public Hole() {
	btn.setBackground(Color.RED);	
	btn.setOpaque(false);
	btn.setBorder(null);
	btn.addActionListener(this);
	btn.setBounds(0, 0, 160, 160);
	btn.setFocusPainted(false);
	this.setBounds(0, 0, 200, 200);
	container.setBounds(0, 0, 160, 160);
	this.add(container);
	container.add(btn);
	
	}
	
	
	/**choose mole colour**/
	//going to make gold and green moles more rare to make them more of a priority
	public void chooseMoleColour() {
		
		Random rand = new Random(); // make a new random object
		int r = 1 + rand.nextInt(100); //set r to be a random integer from 1-100
		
		//This if statement will be used to choose between the moles or the items
		if(r <= 50) {
			
			moleToChoose = mole1;
			worthPoints = 50;
			isScroll = false;
			
			
		}else if(r <=75 && r>50) {
			
			moleToChoose = mole2;
			worthPoints = 75;
			isScroll = false;
		}else if(r<=85 && r>75) {
			
			moleToChoose = mole3;
			worthPoints = 100;
			isScroll = false;
			
		}else {
			isScroll = true;
			scrollType = rand.nextBoolean(); //set the scroll type to a random bool
			if(scrollType == false) {
				moleToChoose = goodScroll;
			}else {
				moleToChoose = badScroll;
			}
		}
		
		//To make the game more difficult, I will adding some moles to show up for 1 second
		showDuration = rand.nextInt(3);
		//System.out.println(showDuration);
		
	}
	
	public void ShowSmoke() {
		container.remove(Smoke);
		Smoke = new JLabel("",JLabel.CENTER);
		Smoke.setIcon(smoke); //this will play a smoke gif
		Smoke.setBounds(0, 0, 160, 160);
		Smoke.setVisible(true);
		container.add(Smoke);
		container.repaint(); 
		smoketimer(); //starts the countdown so its only on for a smole amount of time
	}
	
	public void ShowMole() {
	if(isFull == false) {
		container.remove(Smoke);
		chooseMoleColour(); //choose grey green or gold
		isFull=true;
		ShowSmoke();
		Mole = new JLabel("",JLabel.CENTER);
		Mole.setIcon(moleToChoose);
		Mole.setVisible(true);
		Mole.setBounds(0, 0, 160, 160);
		container.add(Mole);
		container.repaint();
		showDurationTimer();
		playSound(shadowCloneSound,20.0f);
		
		}else{
			ShowSmoke();
			Mole.setVisible(false);
			container.repaint();
		}
		
	}
	
	public void smoketimer() {
		ActionListener action  = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(smokeTimerActive == true) {
					smokeTime = smokeTime+1;
					 //check if smoke has been active for the 6ms
					if(smokeTime>=6) {
						smokeDurationTimer.stop();
						Smoke.setVisible(false);
						container.remove(Smoke);
						container.repaint();
						smokeTime = 0;
						smokeTimerActive = false;
							}	
						}
					}
				};
			smokeDurationTimer = new Timer(100, action); //run the action we made every millisecond
			smokeDurationTimer.start();
			smokeTimerActive = true;
	}
	
	public void showDurationTimer() {
		ActionListener action  = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					showDuration = showDuration - 1;
					if(showDuration <= 0 ) {
						showDurationTimer.stop();
						container.remove(Mole);
						ShowSmoke();
						
						container.repaint();
						showDuration = 2;
						isFull = false;
					}
					}
				};
			showDurationTimer = new Timer(1000, action); //run the action we made every second
			showDurationTimer.start();
			Smoke.setVisible(true);
			isFull = true;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		//if the correct button is clicked and the mole is shown then add to the score
		if(e.getSource() == btn && isFull==true) {
			container.remove(Smoke);
			ShowMole();
			score = score + worthPoints;
			if(isScroll == false) {
				parent.addToScore(worthPoints); //calls the main class and tells it to add x much on to the score
			}else if(isScroll == true && scrollType == false){
				parent.modifyTime(3);
			}else if(isScroll == true && scrollType == true){
				parent.modifyTime(-3);
			}
			
			btn.setFocusPainted(false);
			playSound(impactSound,0f);
		}
	}
	
}
