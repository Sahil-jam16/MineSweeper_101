package game;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;



import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.*;

//
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;






class WorldMinesweeper {
	private static int COLS;
	private static int ROWS;
	private static int N_BOMBS; 
	private static int N_FLAGS;
	private static boolean dead;
	private static boolean finish;
	private static boolean started;
	static TileMinesweeper[][] matrix;
	private static TimerMinesweeper timer = new TimerMinesweeper();
	private static String player_name;

	public static String []DIFFICULTY = {"difficultyEasy", "difficultyNormal", "difficultyHard"};
	private static FrameMinesweeper frame;
	private static ScoreMinesweeper score;

	private BufferedImage bomb_img;
	private BufferedImage bomb_no_face_img;
	private BufferedImage flag_img;
	private BufferedImage pressed_img;
	private BufferedImage pressed2_img;
	private BufferedImage normal_img;
	private BufferedImage normal2_img;
	private BufferedImage error_img;

	//CONSTRUCTOR
	public WorldMinesweeper(String difficulty) {

		String current_difficulty;
		if (difficulty.equals(DIFFICULTY[0])) {
			current_difficulty = DIFFICULTY[0];
			COLS = 11;
			ROWS = 11;
		}
		if (difficulty.equals(DIFFICULTY[1])) {
			current_difficulty = DIFFICULTY[1];
			COLS = 15;
			ROWS = 15;
		} 
		if (difficulty.equals(DIFFICULTY[2])) {
			current_difficulty = DIFFICULTY[2];
			COLS = 21;
			ROWS = 21;
		} 
		N_BOMBS = COLS*ROWS*16/100;
		N_FLAGS = N_BOMBS;

		
		scaleImages();
		
		matrix = new TileMinesweeper[ROWS][COLS];

		if (frame != null) {
			frame.setVisible(false); 
			frame.dispose(); //Destroy the JFrame object
		}



		frame = new FrameMinesweeper();

		boolean tile_switch = false;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (tile_switch == false) {
					matrix[i][j] = new TileMinesweeper(i, j, normal_img, bomb_no_face_img, bomb_img, pressed_img, flag_img, error_img);
					tile_switch = true;
				}
				else {
					matrix[i][j] = new TileMinesweeper(i, j, normal2_img, bomb_no_face_img, bomb_img, pressed2_img, flag_img, error_img);
					tile_switch = false;
				}
			}
			
		}

		this.player_name = get_player_name();

		score = new ScoreMinesweeper(player_name,0, timer, difficulty,ROWS,COLS,matrix);
		reset();
	}

	private String get_player_name() {
		String name = JOptionPane.showInputDialog(null, "Please enter your name:");
		JOptionPane.showMessageDialog(null, "All THE BEST , " + name + " !!!");
		return name;
	}

	private void scaleImages() {
		bomb_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/bomb_face.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		bomb_no_face_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/bomb.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		flag_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/flag.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		pressed_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/tile_brown_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		pressed2_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/tile_brown2_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		normal_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/tile_green_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		normal2_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/tile_green2_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		error_img = ImageLoaderMinesweeper.scale(ImageLoaderMinesweeper.loadImage("res/error.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	}
	
	private static void place_all_bombs() {
		for (int i=0; i<N_BOMBS; i++) { 
			place_bomb();
		}
	}
	
	private static void place_bomb() {
		Random random = new Random();
		int tileX = random.nextInt(COLS);
		int tileY = random.nextInt(ROWS);
		
		if (matrix[tileX][tileY].isBomb()) place_bomb();
		else {
			matrix[tileX][tileY].setBomb(true);
		}
		
	}
	
	public static void showNumberOfFlag() {
		int count = 0;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag() && matrix[i][j].isOpened()==false) count++;
			}
		}
		
		N_FLAGS = N_BOMBS-count;
		frame.setFlagsNumber(N_FLAGS);
	}
	
	public static void showWrongFlags() {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag() && !(matrix[i][j].isBomb() || matrix[i][j].isBombFace())) {
					matrix[i][j].setError(true);
					
				}
			}
			
		}
		
	}

	public static void left_click(int x, int y) {
		int x_axis = x/TileMinesweeper.getWidth();
		int y_axis = y/TileMinesweeper.getHeight();

		if (started == false) {
			while(!(matrix[x_axis][y_axis].getAmountOfNearBombs() == 0 && matrix[x_axis][y_axis].isBomb() == false)) {
				reset();
			}
		}
		
		if (dead == false && finish == false) {	
			started = true;
			
			if (timer.isTimeRunning() == false) {
				timer.startTimer();  
			}
			
			if (matrix[x_axis][y_axis].isFlag()) return;
			else if (matrix[x_axis][y_axis].isOpened())return;
			else if (matrix[x_axis][y_axis].isBomb()) {
				dead = true;

				showAllBombs();
				showWrongFlags();


				matrix[x_axis][y_axis].setBomb(false);
				matrix[x_axis][y_axis].setBombFace(true);
				
				// audio configuration
				try {
					openSound(".//res//buttonEffect.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				}
			}
			else if (matrix[x_axis][y_axis].getAmountOfNearBombs() == 0 && matrix[x_axis][y_axis].isBomb() == false) open(x_axis, y_axis);
			
			matrix[x_axis][y_axis].setOpened(true);
			
			if (!dead) {
				// sound
				try {
					String []choose = {"sound_effect_open1.wav", "sound_effect_open2.wav", "sound_effect_open3.wav", "sound_effect_open4.wav"}; 
					Random random = new Random(); 
					int index = random.nextInt(4-1) + 1;
					openSound(".//res//"+choose[index]);
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				}
			}
			
			checkFinish();
			
			if ((finish == true || dead == true) && timer.isTimeRunning() == true ) {
				score.getScore();
				score.writeScoreToFile();
				JDBCMineSweeper.addScoresDatabase(score);
				score.scoreReset();
				timer.stopTimer();
				timer = new TimerMinesweeper();
			}
			
			if (dead == false && finish == true) {
			    try {
				    String result = JOptionPane.showInputDialog(null,player_name , "Congratulations!! Seconds Passed = " + timer.getTimer(), JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				} 
			   
			}
		}

	}
	
	public static void right_click(int x, int y) {
		if(dead == false && finish == false){
			
			if (timer.isTimeRunning() == false) {
				timer.startTimer();
			}
			
			int x_axis = x/TileMinesweeper.getWidth();
			int y_axis = y/TileMinesweeper.getHeight();
			matrix[x_axis][y_axis].placeFlag();
			
			showNumberOfFlag();
			
			// sound
			if (!matrix[x_axis][y_axis].isOpened()) {
				try {
					openSound(".//res//sound_effect_flag.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				}
			}
			
		}
		
	}
	
	public static void openSound(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if (frame.isSoundEffectActive() == false) return;
		
		File file = new File(path);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		clip.start();
	}
	
	
	private static void checkFinish() {
		finish = true; 
		for(int i = 0; i<COLS; i++) {
			for(int j=0; j<ROWS; j++) {
				if (matrix[i][j].isBomb() && matrix[i][j].isOpened()) {
					dead = true;
					return;
				} 
				if (matrix[i][j].isBomb() == false && matrix[i][j].isOpened() == false) {
					finish = false;
					return;
				}
			}
		}
		
	}
	
	
	//TODO: fix
	private void winLayout() {
		removeAllFlags();
		
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Exception --> " + e);
		}

		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isBomb()) {
					try {
						TimeUnit.MILLISECONDS.sleep(100);
						matrix[i][j].placeFlower();
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.err.println("Exception --> " + e);
					}
				}
			}
		}
	}
	
	public static void reset() {

		for(int i=0; i<COLS; i++){
			for(int j=0; j<ROWS; j++){
				matrix[i][j].reset();
			}
		}
		

		dead = false;
		finish = false;
		started = false;
		

		place_all_bombs();
		set_number_of_near_bombs();

		frame.setFlagsNumber(N_BOMBS);
		frame.setTilesNumber(COLS*ROWS);
		frame.setTimer(0);	
		
		// restart timer
		timer.setTimer(0);
		if (timer.isTimeRunning() == true) {
			timer.stopTimer();
			timer = new TimerMinesweeper();
		} 
	}
	
	
	public static void draw(Graphics g){
		Font font = new Font("SansSerif", 0, frame.getScreenWidth()*10/100);
		Rectangle rect = new Rectangle(frame.width, frame.height);
		for(int x = 0;x < COLS;x++){
			for(int y = 0;y < ROWS;y++){
				matrix[x][y].draw(g);
			}
		}
		
		if(dead){
			g.setColor(Color.RED);
			frame.drawCenteredString(g, "Game Over!", rect, font);
		}
		else if(finish){
			g.setColor(Color.GREEN);
			frame.drawCenteredString(g, "You Won!!", rect, font);
		}
	}
	
	public static void OpenRules (){
		ImageIcon icon = new ImageIcon(".//res//info.png");
		JOptionPane.showMessageDialog(null, 
				"Minesweeper rules are very simple: \r\n"
				+ "			- The board is divided into cells, with mines randomly distributed. \r\n"
				+ "			- To win, you need to open all the cells. \r\n"
				+ "			- The number on a cell shows the number of mines adjacent to it. \r\n"
				+ "			- Using this information, you can determine cells that are safe, and cells that contain mines. \r\n"
				+ "			- Cells suspected of being mines can be marked with a flag using the right mouse button.",
				"How To Play",
				JOptionPane.PLAIN_MESSAGE, icon);

	}

	// ######################## Scoreboard ########################
	public static void OpenScoreboard() throws Exception {
		String filePath = "G:\\College Work\\SEMESTER IV\\JAVA LAB\\HUSTLE\\MineSweeper_101\\scores.txt";
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("Scoreboard file not found.");
			return;
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<String> scoreList = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			scoreList.add(line);
		}
		reader.close();

		String new_score = player_name + " -->  Seconds: " + timer.getTimer();
		JPanel pnl = new JPanel();
		pnl.setBounds(61, 11, 81, 140);
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS)); // VerticalLayout
		JLabel[] labels = new JLabel[scoreList.size()];

		for (int i = 0; i < scoreList.size(); i++) {
			String score = scoreList.get(i);
			labels[i] = new JLabel();

			if (score.equals(new_score)) {
				new_score = (i + 1) + ". " + new_score + "\n";
				labels[i].setText(new_score);
				labels[i].setForeground(new Color(50, 205, 50));
			} else {
				labels[i].setText((i + 1) + ". " + score);
			}

			pnl.add(labels[i]);
		}

		ImageIcon icon = new ImageIcon(".//res//trophy.png");
		JOptionPane.showMessageDialog(null, pnl, "Scoreboard", JOptionPane.PLAIN_MESSAGE, icon);
	}
	
	//#######################################################

	private static void open(int x, int y) {
		matrix[x][y].setOpened(true);
		
		if(matrix[x] [y].getAmountOfNearBombs() == 0) {
			int mx = x - 1;
			int gx = x + 1;
			int my = y - 1;
			int gy = y + 1;

			if(mx>=0 && my>=0 && matrix[mx][my].canOpen()) open(mx, my);
			if(mx>=0 && matrix[mx][y].canOpen()) open(mx, y);
			if(mx>=0 && gy<ROWS && matrix[mx][gy].canOpen()) open(mx, gy);
			
			if(my>=0 && matrix[x][my].canOpen()) open(x, my);
			if(gy<ROWS && matrix[x][gy].canOpen()) open(x, gy);
			
			if(gx<COLS && my>=0 && matrix[gx][my].canOpen()) open(gx, my);
			if(gx<COLS && matrix[gx][y].canOpen()) open(gx, y);
			if(gx<COLS && gy<ROWS && matrix[gx][gy].canOpen()) open(gx, gy);
		}
	}
	
	public static void showAllBombs () {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if ((matrix[i][j].isBomb() || matrix[i][j].isBombFace()) && matrix[i][j].isFlag() == false) matrix[i][j].setOpened(true);
			}
		}
	}
	
	public static void removeAllFlags() {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag()) matrix[i][j].placeFlag();
			}
		}
	}
	
	
	
	
	
	// GETTER
	public static int getCOLS() {
		return COLS;
	}
	
	public static int getROWS() {
		return ROWS;
	}

	public static int getN_FLAGS() {
		return N_FLAGS;
	}

	public static void setROWS(int value) {
		ROWS = value;
		reset();
	}

	private static void set_number_of_near_bombs() {
		int number_of_near_bombs = 0;
		for (int i=0; i<COLS; i++) {
			for (int j=0; j<ROWS; j++) {
				if (!matrix[i][j].isBomb()) {
					if (j+1 < ROWS) if (matrix[i][j+1].isBomb()) number_of_near_bombs++;
					if (j-1 >= 0) 	if (matrix[i][j-1].isBomb()) number_of_near_bombs++;
					if (i+1 < COLS) if (matrix[i+1][j].isBomb()) number_of_near_bombs++;
					if (i-1 >= 0) 	if (matrix[i-1][j].isBomb()) number_of_near_bombs++;
					if (i-1 >= 0 && j+1 < ROWS) 	if (matrix[i-1][j+1].isBomb()) number_of_near_bombs++;
					if (i-1 >= 0 && j-1 >= 0) 		if (matrix[i-1][j-1].isBomb()) number_of_near_bombs++;
					if (i+1 < COLS && j+1 < ROWS) 	if (matrix[i+1][j+1].isBomb()) number_of_near_bombs++;
					if (i+1 < COLS && j-1 >= 0) 	if (matrix[i+1][j-1].isBomb()) number_of_near_bombs++;
					
					matrix[i][j].setAmountOfNearBombs(number_of_near_bombs);
					number_of_near_bombs = 0;
				}
				
			}
			
		}	
	}
	
}