package game;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.Point;

import javax.swing.*;


@SuppressWarnings("serial")
class FrameMinesweeper extends JFrame implements MouseListener, WindowListener, ActionListener{
	
	public static int width = 600; 
	public static int height = 600;
	
	private Screen screen;
	private Font font;
	
	private JMenuBar menu_bar;
	private JToolBar tool_bar;
	
	private JButton flags_number;
	private JButton tiles_number;
	private static JButton time_number;
	private JCheckBoxMenuItem sounds;
	
	private int insetLeft;
	private int insetTop;
	
	// CONSTRUCTOR	
	public FrameMinesweeper () { 
		
		addMouseListener(this);
		
		Init();
		
		screen = new Screen();
		this.setLayout(new BorderLayout());
		add(screen, BorderLayout.CENTER);
		
		// ------ ToolBar
		tool_bar = new JToolBar();
		tool_bar.setFloatable(false);
		this.add(tool_bar, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		
		Icon icon1 = new ImageIcon("res/tile_green_normal.png");
		Icon icon2 = new ImageIcon("res/flag.png");
		Icon icon3 = new ImageIcon("res/clock.png");
		
		tiles_number = new JButton("Tiles = ");
		flags_number = new JButton("Flags = ");
		time_number = new JButton("Time = ");
		
		tiles_number.setIcon(icon1);
		flags_number.setIcon(icon2);
		time_number.setIcon(icon3);
		
		tiles_number.setBorderPainted(false);
		flags_number.setBorderPainted(false);
		time_number.setBorderPainted(false);
		
		tiles_number.setFocusPainted(false);
		flags_number.setFocusPainted(false);
		time_number.setFocusPainted(false);
		
		tiles_number.setContentAreaFilled(false);
		flags_number.setContentAreaFilled(false);
		time_number.setContentAreaFilled(false);
		
		panel.add(tiles_number);
		panel.add(time_number);
		panel.add(flags_number);
		tool_bar.add(panel);

		addWindowListener(this);
		
		// Menu Bar
		menu_bar = new JMenuBar();
		setJMenuBar(menu_bar);
		
		// Menus
		JMenu menuGame = new JMenu("Game");
		JMenu menuOptions = new JMenu("Options");
		JMenu submnuNewGame = new JMenu("New game");  //sub menu

		menu_bar.add(menuGame);
		menu_bar.add(menuOptions);
		
		// Menu Items
		JMenuItem restart = new JMenuItem("Restart");
		JMenuItem remove_all_flags = new JMenuItem("Remove all flags");
		JMenuItem scoreboard = new JMenuItem("Scoreboard");
		JMenuItem rules = new JMenuItem("How To Play");
		JMenuItem share = new JMenuItem("Share");
		JMenuItem quit = new JMenuItem("Quit");
		JMenuItem easy = new JMenuItem("Easy");
		JMenuItem normal = new JMenuItem("Normal");
		JMenuItem hard = new JMenuItem("Hard");
		JMenuItem average = new JMenuItem("Average");
		sounds = new JCheckBoxMenuItem("Sounds Effect");

		menuGame.add(restart);
		menuGame.add(submnuNewGame);
		menuGame.add(remove_all_flags);
		menuGame.add(scoreboard);
		menuGame.add(average);
		menuOptions.add(sounds);
		menuOptions.add(rules);
		menuOptions.add(share);
		menuOptions.add(quit);
		submnuNewGame.add(easy);
		submnuNewGame.add(normal);
		submnuNewGame.add(hard);

		sounds.setSelected(true);
		
		// Action Command
		restart.setActionCommand("Restart");
		remove_all_flags.setActionCommand("Remove All Flags");
		scoreboard.setActionCommand("Scoreboard");
		rules.setActionCommand("Rules");
		share.setActionCommand("Share");
		quit.setActionCommand("Quit");
		easy.setActionCommand("Easy");
		normal.setActionCommand("Normal");
		hard.setActionCommand("Hard");
		average.setActionCommand("Average");
		sounds.setActionCommand("Sounds Effect");
		
		// Action Listener
		restart.addActionListener(this);
		remove_all_flags.addActionListener(this);
		scoreboard.addActionListener(this);
		rules.addActionListener(this);
		share.addActionListener(this);
		quit.addActionListener(this);
		easy.addActionListener(this);
		normal.addActionListener(this);
		hard.addActionListener(this);
		average.addActionListener(this);
		sounds.addActionListener(this);
		
		// Acceleration
		restart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK)); // ctrl+r
		remove_all_flags.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK)); // ctrl+f
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK)); // alt+f4
		average.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
		
		ImageIcon image = new ImageIcon(".//res//bomb.png");
		setIconImage(image.getImage());

		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(".//res//cursor.png").getImage(),new Point(0,0),"custom cursor"));
		
		// Setting Borders
		setBorders();
	}
	
	private void Init() {
		this.setTitle("Minesweeper");
		this.setResizable(true);
        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getScreenWidth()) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - getScreenHeight()) / 2); // setto la finestra al centro
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public class Screen extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			font = new Font("SansSerif", Font.BOLD, width/WorldMinesweeper.getCOLS() - width/WorldMinesweeper.getCOLS()*50/100); // la grandezza dei numeri all'interno delle caselle ridimensionata in base al numero di celle - il 50% del risultato
			g.setFont(font);
			WorldMinesweeper.draw(g);
		}
	}
	
	private void setBorders() {
		pack();
		insetLeft = getInsets().left;
		insetTop = getInsets().top;
		setSize(width + insetLeft + getInsets().right, height + getInsets().bottom + insetTop + menu_bar.getHeight() + tool_bar.getHeight());
	}

	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
	
	// GETTER
	public static int getScreenWidth(){
		return width;
	}
	
	public static int getScreenHeight(){
		return height;
	}
	
	public boolean isSoundEffectActive() {
		return sounds.isSelected();
	}
	
	// SETTER
	public void setFlagsNumber(int value) {
		flags_number.setText("Flags = " + value);
	}
	
	public void setTilesNumber(int value) {
		tiles_number.setText("Tiles = " + value);
	} 
	
	public static void setTimer(float value) {
		time_number.setText("Time = " + value);
	}
	
	// MOUSE
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		try {
			if(e.getButton() == 1) WorldMinesweeper.left_click(e.getX() - insetLeft, e.getY() - insetTop - menu_bar.getHeight() - tool_bar.getHeight());
			if(e.getButton() == 3) WorldMinesweeper.right_click(e.getX() - insetLeft, e.getY() - insetTop - menu_bar.getHeight() - tool_bar.getHeight());
			screen.repaint();
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Exception --> " + ex);
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {	
		String command = e.getActionCommand();
		
		if(command.equals("Restart")) {
			WorldMinesweeper.reset();
			screen.repaint();
		}
		else if (command.equals("Easy")) { 
			width = 440;
			height = 400;
			new WorldMinesweeper(WorldMinesweeper.DIFFICULTY[0]);
		}
		
		else if (command.equals("Normal")) {
			width = 600;
			height = 600;
			new WorldMinesweeper(WorldMinesweeper.DIFFICULTY[1]); 
		}
		else if (command.equals("Hard")) {
			width = 840;
			height = 840;
			new WorldMinesweeper(WorldMinesweeper.DIFFICULTY[2]);
		}
		else if (command.equals("Remove All Flags")) {
			WorldMinesweeper.removeAllFlags();
			screen.repaint();
		}
		else if (command.equals("Quit")) {
			System.exit(EXIT_ON_CLOSE);
		}
		else if (command.equals("Share")) {
			JOptionPane.showMessageDialog(null, "Share link copied to clipboard!");
	        String testString = "https://github.com/Sahil-jam16/";
	        StringSelection stringSelectionObj = new StringSelection(testString);
	        Clipboard clipboardObj = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboardObj.setContents(stringSelectionObj, null);
		}
		else if (command.equals("Rules")) {
			try {
				WorldMinesweeper.OpenRules();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else if (command.equals("Scoreboard")) {
			try {
				WorldMinesweeper.OpenScoreboard();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else if (command.equals("Average")){
			try{
				JDBCMineSweeper.calculateAverages();
			} catch( Exception e1){
				e1.printStackTrace();
			}
		}
		else;
	}
	
}