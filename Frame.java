import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/* The main graphics class for APathfinding. Controls the window,
 * and all path finding node graphics. Need to work on zoom function,
 * currently only zooms to top left corner rather than towards mouse
 * by Devon Crawford
 */
public class Frame extends JPanel
		implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	ControlHandler ch;
	JFrame window;
	APathfinding pathfinding;
	int size;
	char currentKey = (char) 0;
	Node startNode, endNode;
	
	Timer timer = new Timer(100, this);
	int r = randomWithRange(0, 255);
	int G = randomWithRange(0, 255);
	int b = randomWithRange(0, 255);

	public static void main(String[] args) {
		new Frame();
	}

	public Frame() {
		timer.setDelay(50);
		ch = new ControlHandler(this);
		size = 25;
		setLayout(null);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		// Set up pathfinding
		pathfinding = new APathfinding(this, size);
		pathfinding.setDiagonal(true);
		
		// Set up window
		window = new JFrame();
		window.setContentPane(this);
		window.setTitle("A* Pathfinding Visualization");
		window.getContentPane().setPreferredSize(new Dimension(700, 600));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		// Add all controls
		ch.addAll();
		
		this.revalidate();
		this.repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Grab dimensions of panel
		int height = getHeight();
		int width = getWidth();
		
		// Cikisa giden bir yol bulunamadiysa
		if (pathfinding.isNoPath()) {
			// run button'indaki "Run" yazisini "Clear" olarak degistir.
			ch.getRun().setText("clear");
			
			
			// Renk degistirme animasyonu
			Color flicker = new Color(r, G, b);
			g.setColor(flicker);
			g.fillRect(0, 0, getWidth(), getHeight());

			// Ekranin ortasina "No Path" yazisini ekle.
			ch.noPathTBounds();
			ch.getNoPathT().setVisible(true);
			this.add(ch.getNoPathT());
			//this.revalidate(); //????
		}

		// Cikisa giden bir yol bulunduysa
		if (pathfinding.isComplete()) {
			// run button'indaki "Run" yazisini "Clear" olarak degistir.
			ch.getRun().setText("clear");
			

			// Make the background flicker
			Color flicker = new Color(r, G, b);
			g.setColor(flicker);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		// Kareleri cizer.
		g.setColor(Color.lightGray);
		for (int j = 0; j < this.getHeight(); j += size) {
			for (int i = 0; i < this.getWidth(); i += size) {
				g.drawRect(i, j, size, size);
			}
		}

		// Draws all borders???????????????????????????
		g.setColor(Color.black);
		for (int i = 0; i < pathfinding.getBorderList().size(); i++) {
			g.fillRect(pathfinding.getBorderList().get(i).getX() + 1, pathfinding.getBorderList().get(i).getY() + 1,
					size - 1, size - 1);
		}

		// Butun acik nodelari cizilmesi gerek.
		for (int i = 0; i < pathfinding.getOpenList().size(); i++) {
			Node current = pathfinding.getOpenList().get(i);
			g.setColor(new Color(132, 255, 138));
			g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);

			drawInfo(current, g);
		}

		// Butun kapali nodelarin cizilmesi gerek.
		for (int i = 0; i < pathfinding.getClosedList().size(); i++) {
			Node current = pathfinding.getClosedList().get(i);

			g.setColor(new Color(253, 90, 90));
			g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);

			drawInfo(current, g);
		}

		// Draw all final path nodes
		for (int i = 0; i < pathfinding.getPathList().size(); i++) {
			Node current = pathfinding.getPathList().get(i);

			g.setColor(new Color(32, 233, 255));
			g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);

			drawInfo(current, g);
		}

		// Baslangic koordinatini boyar.
		if (startNode != null) {
			g.setColor(Color.blue);
			g.fillRect(startNode.getX() + 1, startNode.getY() + 1, size - 1, size - 1);
		}
		// Bitis koordinatini boyar.
		if (endNode != null) {
			g.setColor(Color.red);
			g.fillRect(endNode.getX() + 1, endNode.getY() + 1, size - 1, size - 1);
		}
		

		g.setColor(new Color(120, 120, 120, 80));

		// Drawing control panel rectangle
		g.fillRect(10, height-96, 322, 90);

		
		// Position all controls
		ch.position();
		pathfinding.setDiagonal(ch.getDiagonalCheck().isSelected());
	}
	
	// Draws info (f, g, h) on current node
	public void drawInfo(Node current, Graphics g) {
		if (size > 50) {
			Font numbersFont = new Font("arial", Font.BOLD, 12);
			Font smallNumbersFont = new Font("arial", Font.PLAIN, 11);
			g.setFont(numbersFont);
			g.setColor(Color.black);
			g.drawString(Integer.toString(current.getF()), current.getX() + 4, current.getY() + 16);
			g.setFont(smallNumbersFont);
			g.drawString(Integer.toString(current.getG()), current.getX() + 4, current.getY() + size - 7);
			g.drawString(Integer.toString(current.getH()), current.getX() + size - 26, current.getY() + size - 7);
		}
	}

	public void MapCalculations(MouseEvent e) {
		// If left mouse button is clicked
		if (SwingUtilities.isLeftMouseButton(e)) {
			// If 's' is pressed create start node
			if (currentKey == 's') {
				int xRollover = e.getX() % size;
				int yRollover = e.getY() % size;

				if (startNode == null) {
					startNode = new Node(e.getX() - xRollover, e.getY() - yRollover);
				} else {
					startNode.setXY(e.getX() - xRollover, e.getY() - yRollover);
				}
				repaint();
			} 
			// If 'e' is pressed create end node
			else if (currentKey == 'e') {
				int xRollover = e.getX() % size;
				int yRollover = e.getY() % size;

				if (endNode == null) {
					endNode = new Node(e.getX() - xRollover, e.getY() - yRollover);
				} else {
					endNode.setXY(e.getX() - xRollover, e.getY() - yRollover);
				}
				repaint();
			} 
			// Otherwise, create a wall
			else {
				int xBorder = e.getX() - (e.getX() % size);
				int yBorder = e.getY() - (e.getY() % size);

				Node newBorder = new Node(xBorder, yBorder);
				pathfinding.addBorder(newBorder);

				repaint();
			}
		} 
		// If right mouse button is clicked
		else if (SwingUtilities.isRightMouseButton(e)) {
			int mouseBoxX = e.getX() - (e.getX() % size);
			int mouseBoxY = e.getY() - (e.getY() % size);

			// If 's' is pressed remove start node
			if (currentKey == 's') {
				if (startNode != null && mouseBoxX == startNode.getX() && startNode.getY() == mouseBoxY) {
					startNode = null;
					repaint();
				}
			} 
			// If 'e' is pressed remove end node
			else if (currentKey == 'e') {
				if (endNode != null && mouseBoxX == endNode.getX() && endNode.getY() == mouseBoxY) {
					endNode = null;
					repaint();
				}
			} 
			// Otherwise, remove wall
			else {
				int Location = pathfinding.searchBorder(mouseBoxX, mouseBoxY);
				if (Location != -1) {
					pathfinding.removeBorder(Location);
				}
				repaint();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		MapCalculations(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		MapCalculations(e);
	}

	@Override
	// Track mouse on movement
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int height = this.getHeight();
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		currentKey = key;

		// Start if space is pressed
		if (currentKey == KeyEvent.VK_SPACE) {
			ch.getRun().setText("stop");
			start();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		currentKey = (char) 0;
	}
	
	// Starts path finding
	void start() {
		if(startNode != null && endNode != null) {
			pathfinding.setup(startNode, endNode);
			timer.start();
		}
		else {
			System.out.println("ERROR: Needs start and end points to run.");
			ch.getRun().setText("run");
		}
	}
	
	@Override
	// Scales the map with mouse wheel scroll
	public void mouseWheelMoved(MouseWheelEvent m) {
		int rotation = m.getWheelRotation();
		double prevSize = size;
		int scroll = 3;

		// Changes size of grid based on scroll
		if (rotation == -1 && size + scroll < 200) {
			size += scroll;
		} else if (rotation == 1 && size - scroll > 2) {
			size += -scroll;
		}
		pathfinding.setSize(size);
		double ratio = size / prevSize;

		// new X and Y values for Start
		if (startNode != null) {
			int sX = (int) Math.round(startNode.getX() * ratio);
			int sY = (int) Math.round(startNode.getY() * ratio);
			startNode.setXY(sX, sY);
		}

		// new X and Y values for End
		if (endNode != null) {
			int eX = (int) Math.round(endNode.getX() * ratio);
			int eY = (int) Math.round(endNode.getY() * ratio);
			endNode.setXY(eX, eY);
		}

		// new X and Y values for borders
		for (int i = 0; i < pathfinding.getBorderList().size(); i++) {
			int newX = (int) Math.round((pathfinding.getBorderList().get(i).getX() * ratio));
			int newY = (int) Math.round((pathfinding.getBorderList().get(i).getY() * ratio));
			pathfinding.getBorderList().get(i).setXY(newX, newY);
		}

		// New X and Y for Open nodes
		for (int i = 0; i < pathfinding.getOpenList().size(); i++) {
			int newX = (int) Math.round((pathfinding.getOpenList().get(i).getX() * ratio));
			int newY = (int) Math.round((pathfinding.getOpenList().get(i).getY() * ratio));
			pathfinding.getOpenList().get(i).setXY(newX, newY);
		}

		// New X and Y for Closed Nodes
		for (int i = 0; i < pathfinding.getClosedList().size(); i++) {
			if (!Node.isEqual(pathfinding.getClosedList().get(i), startNode)) {
				int newX = (int) Math.round((pathfinding.getClosedList().get(i).getX() * ratio));
				int newY = (int) Math.round((pathfinding.getClosedList().get(i).getY() * ratio));
				pathfinding.getClosedList().get(i).setXY(newX, newY);
			}
		}
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Moves one step ahead in path finding (called on timer)
		if (pathfinding.isRunning()) {
			pathfinding.findPath(pathfinding.getPar());
		}
		// Bittiginde arkaplan renk degistirir.
		if (pathfinding.isComplete() || pathfinding.isNoPath()) {
			r = (int) (Math.random() * ((r + 15) - (r - 15)) + (r - 15));
			G = (int) (Math.random() * ((G + 15) - (G - 15)) + (G - 15));
			b = (int) (Math.random() * ((b + 15) - (b - 15)) + (b - 15));
			
			if (r >= 240 | r <= 15) {
				r = randomWithRange(0, 255);
			}
			if (G >= 240 | G <= 15) {
				G = randomWithRange(0, 255);
			}
			if (b >= 240 | b <= 15) {
				b = randomWithRange(0, 255);
			}
		}
		
		// Actions of run/stop/clear button
		if(e.getActionCommand() != null) {
			if(e.getActionCommand().equals("run") && !pathfinding.isRunning()) {
				ch.getRun().setText("stop");
				start();
			}
			else if(e.getActionCommand().equals("clear")) {
				ch.getRun().setText("run");
				ch.getNoPathT().setVisible(false);
				pathfinding.reset();
			}
			else if(e.getActionCommand().equals("stop")) {
				ch.getRun().setText("start");
				timer.stop();
			}
			else if(e.getActionCommand().equals("start")) {
				ch.getRun().setText("stop");
				timer.start();
			}
		}
		repaint();
	}
	
	// Returns random number between min and max
	int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	

}


