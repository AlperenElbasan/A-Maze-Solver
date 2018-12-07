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
import javax.swing.*;


public class Frame extends JPanel
		implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	ControlHandler ch;
	JFrame window;
	APathfinding pathfinding;
	int size;
	char currentKey = (char) 0;
	Node startNode, endNode;
	boolean startWithTemplate = true;
	
	Timer timer = new Timer(100, this);
	int r = randomWithRange(0, 255);
	int G = randomWithRange(0, 255);
	int b = randomWithRange(0, 255);

	long startTime;
	long endTime;
	boolean timePassedFlag;

	public static void main(String[] args) {
		JFrame selectionWindow = new JFrame();
		JButton buttonEmpty = new JButton("Empty");
		JButton buttonTemplate = new JButton("Template");
		JPanel panel = new JPanel();
		panel.add(buttonEmpty);
		panel.add(buttonTemplate);
		selectionWindow.getContentPane().add(panel);
		selectionWindow.setVisible(true);
		selectionWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selectionWindow.setSize(200,100);


		Frame mainframe = new Frame();
		buttonEmpty.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainframe.getWindow().setVisible(true);
				selectionWindow.setVisible(false);
			}
		});
		buttonTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainframe.initBorder();
				mainframe.getWindow().setVisible(true);
				selectionWindow.setVisible(false);
			}
		});



	}


	public Frame() {
		timer.setDelay(50);
		ch = new ControlHandler(this);
		size = 25; //Her karenin buyuklugu
		setLayout(null);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		// Pathfinding'i kurma
		pathfinding = new APathfinding(this, size);
		pathfinding.setDiagonal(true);
		
		// Pencereyi kurma
		window = new JFrame();
		window.setContentPane(this);
		window.setTitle("A* Pathfinding Visualization");
		window.getContentPane().setPreferredSize(new Dimension(700, 600));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setLocationRelativeTo(null);
		
		// Tum itemlari ekle
		ch.addAll();
		this.revalidate();
		this.repaint();
	}
	public JFrame getWindow(){
		return window;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Panelin boyutları
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
			ch.getNoPathT().setVisible(true);
			
			// Gecen zamani goster
			endTime = System.nanoTime();
			ch.getTimePassed().setVisible(true);
			if(timePassedFlag){
				ch.getTimePassed().setText("Time: " + (endTime - startTime)/10000000);
				timePassedFlag = false;
			}
		}

		// Cikisa giden bir yol bulunduysa
		if (pathfinding.isComplete()) {
			// run button'indaki "Run" yazisini "Clear" olarak degistir.
			ch.getRun().setText("clear");
			
			// Gecen zamani goster
			endTime = System.nanoTime();
			ch.getTimePassed().setVisible(true);
			if(timePassedFlag){
				ch.getTimePassed().setText("Time: " + (endTime - startTime)/10000000);
				timePassedFlag = false;
			}


			// Renk degistirme animasyonu
			Color flicker = new Color(r, G, b);
			g.setColor(flicker);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		// Kareleri renklendirir
		g.setColor(Color.lightGray);
		for (int j = 0; j < this.getHeight(); j += size) {
			for (int i = 0; i < this.getWidth(); i += size) {
				g.drawRect(i, j, size, size);
			}
		}

		// Tum duvarlari renklendirir
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

			drawInfo(current, g); // Bu kutucuklarin icine f, g, h fonksiyonunun degerlerini yazar.
		}

		// Butun kapali nodelarin cizilmesi gerek.
		for (int i = 0; i < pathfinding.getClosedList().size(); i++) {
			Node current = pathfinding.getClosedList().get(i);

			g.setColor(new Color(253, 90, 90));
			g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);

			drawInfo(current, g);
		}

		// Path'in cizilmesi(renklendirilmesi)
		for (int i = 0; i < pathfinding.getPathList().size(); i++) {
			Node current = pathfinding.getPathList().get(i);

			g.setColor(new Color(32, 233, 255));
			g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);

			drawInfo(current, g);
		}

		// Baslangic koordinatini renklendirir.
		if (startNode != null) {
			g.setColor(Color.blue);
			g.fillRect(startNode.getX() + 1, startNode.getY() + 1, size - 1, size - 1);
		}
		// Bitis koordinatini renklendirir.
		if (endNode != null) {
			g.setColor(Color.red);
			g.fillRect(endNode.getX() + 1, endNode.getY() + 1, size - 1, size - 1);
		}
		
		//Kontrol panelinin oldugu kisim boyandi.
		g.setColor(new Color(120, 120, 120, 200));
		g.fillRect((height/2) - 20, 0, 190, 25);

		
		// Tum controlHandler itemlarinin yeri belirlendi.
		ch.position();
		pathfinding.setDiagonal(ch.getDiagonalCheck().isSelected());
	}
	
	// Kutucuklara f, g, h degerlerinin yazdirilmasi
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
		// Sol mouse tusuna basildiysa
		if (SwingUtilities.isLeftMouseButton(e)) {
			// "s" tusuna basildiysa
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
			// "e" tusuna basildiysa
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
			// Bunlara basilmadiysa duvar yarat
			else {
				int xBorder = e.getX() - (e.getX() % size);
				int yBorder = e.getY() - (e.getY() % size);

				Node newBorder = new Node(xBorder, yBorder);
				pathfinding.addBorder(newBorder);

				repaint();
			}
		} 
		// Sag mouse tusuna basildiysa
		else if (SwingUtilities.isRightMouseButton(e)) {
			int mouseBoxX = e.getX() - (e.getX() % size);
			int mouseBoxY = e.getY() - (e.getY() % size);

			// "s" basildiysa baslangic kutusunu sil
			if (currentKey == 's') {
				if (startNode != null && mouseBoxX == startNode.getX() && startNode.getY() == mouseBoxY) {
					startNode = null;
					repaint();
				}
			} 
			// "e" basildiysa bitis kutusunu sil
			else if (currentKey == 'e') {
				if (endNode != null && mouseBoxX == endNode.getX() && endNode.getY() == mouseBoxY) {
					endNode = null;
					repaint();
				}
			} 
			// Bunlara basilmadiysa duvari sil
			// Location -1 ise orada duvar yok
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
	// Mouse hareketini takip et
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

		// Space'e basildiysa baslat
		if (currentKey == KeyEvent.VK_SPACE) {
			ch.getRun().setText("stop");
			start();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		currentKey = (char) 0;
	}
	
	// Pathfinding (A* algorithm) baslat
	void start() {
		if(startNode != null && endNode != null) {
			pathfinding.setup(startNode, endNode);
			timePassedFlag = true;
			timer.start();
			startTime = System.nanoTime();
		}
		else {
			System.out.println("ERROR: Needs start and end points to run.");
			ch.getRun().setText("run");
		}
	}
	
	@Override
	// Harita buyuklugunu mouse tekerlegi ile ayarlar
	public void mouseWheelMoved(MouseWheelEvent m) {
		int rotation = m.getWheelRotation();
		double prevSize = size;
		int scroll = 3;

		// Teker yukari donerse haritayi buyut
		if (rotation == -1 && size + scroll < 200) {
			size += scroll;
		} else if (rotation == 1 && size - scroll > 2) {
			size += -scroll;
		}
		pathfinding.setSize(size);
		double ratio = size / prevSize;

		// baslangic node'u icin yeni X, Y degerleri
		if (startNode != null) {
			int sX = (int) Math.round(startNode.getX() * ratio);
			int sY = (int) Math.round(startNode.getY() * ratio);
			startNode.setXY(sX, sY);
		}

		// bitis node'u icin yeni X, Y degerleri
		if (endNode != null) {
			int eX = (int) Math.round(endNode.getX() * ratio);
			int eY = (int) Math.round(endNode.getY() * ratio);
			endNode.setXY(eX, eY);
		}

		// duvarlar icin yeni X, Y degerleri
		for (int i = 0; i < pathfinding.getBorderList().size(); i++) {
			int newX = (int) Math.round((pathfinding.getBorderList().get(i).getX() * ratio));
			int newY = (int) Math.round((pathfinding.getBorderList().get(i).getY() * ratio));
			pathfinding.getBorderList().get(i).setXY(newX, newY);
		}

		// fringe nodelar icin yeni X, Y degerleri
		for (int i = 0; i < pathfinding.getOpenList().size(); i++) {
			int newX = (int) Math.round((pathfinding.getOpenList().get(i).getX() * ratio));
			int newY = (int) Math.round((pathfinding.getOpenList().get(i).getY() * ratio));
			pathfinding.getOpenList().get(i).setXY(newX, newY);
		}

		// Kapali nodelar icin yeni X, Y degerleri
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
		// Pathfinding'de bir adim ileri git
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
		
		// run/stop/clear aksiyonlari.
		//TODO: stop edildiginde gecen sure sabit kalmali
		if(e.getActionCommand() != null) {
			if(e.getActionCommand().equals("run") && !pathfinding.isRunning()) {
				ch.getRun().setText("stop");
				start();
			}
			else if(e.getActionCommand().equals("clear")) {
				ch.getRun().setText("run");
				ch.getNoPathT().setVisible(false);
				ch.getTimePassed().setVisible(false);
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
	
	// Verilen iki sayi arasindan rastgele bir sayi secer
	// Program bittiginde ekrani boyamak icin kullandik
	int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	

	// Program templateli calisacagi zaman template olusturma islemi
	public void initBorder(){
		Node newBorder;
		//Ust ve alt duvarlar cizimi.
		for(int i = 0; i < 700; i+=25){
			newBorder = new Node(i - (i % size), 0);
			pathfinding.addBorder(newBorder);
			
			newBorder = new Node(i - (i % size), 575 - (575 % size));
			pathfinding.addBorder(newBorder);
		}
		//Sag ve sol duvarlar cizimi.
		for(int i = 0; i < 575; i+=25){
			newBorder = new Node(0, i - (i % size));
			pathfinding.addBorder(newBorder);
			
			newBorder = new Node(675 - (675 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}




		for(int i = 0; i < 125; i+=25){
			newBorder = new Node(i - (i % size), 100 - (100 % size));
			pathfinding.addBorder(newBorder);
		}

		for(int i = 100; i < 225; i+=25){
			newBorder = new Node(100 - (100 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 125; i < 250; i+=25){
			newBorder = new Node(i - (i % size), 200 - (200 % size));
			pathfinding.addBorder(newBorder);
		}

		for(int i = 25; i < 125; i+=25){
			newBorder = new Node(200 - (200 % size), i - (i % size));
			pathfinding.addBorder(newBorder);	
		}

		for(int i = 50; i < 225; i+=25){
			newBorder = new Node(250 - (250 % size), i - (i % size));
			pathfinding.addBorder(newBorder);	
		}

		for(int i = 25; i < 250; i+=25){
			newBorder = new Node(300 - (300 % size), i - (i % size));
			pathfinding.addBorder(newBorder);	
		}

		for(int i = 250; i <= 300; i+=25){
			newBorder = new Node(i - (i % size), 250 - (250 % size));
			pathfinding.addBorder(newBorder);
		}

		for(int i = 250; i <= 375; i+=25){
			newBorder = new Node(i - (i % size), 275 - (275 % size));
			pathfinding.addBorder(newBorder);
		}

		for(int i = 225; i <= 375; i+=25){
			newBorder = new Node(200 - (200 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 200; i >= 125; i-=25){
			newBorder = new Node(i - (i % size), 400 - (400 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 375; i >= 350; i-=25){
			newBorder = new Node(125 - (125 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 100; i >= 75; i-=25){
			newBorder = new Node(i - (i % size), 350 - (350 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 350; i <= 475; i+=25){
			newBorder = new Node(75 - (75 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 25; i <= 150; i+=25){
			newBorder = new Node(i - (i % size), 500 - (500 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 200; i >= 125; i-=25){
			newBorder = new Node(i - (i % size), 450 - (450 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 300; i <= 625; i+=25){
			if(i  == 300)
				continue;
			newBorder = new Node(250 - (250 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 100; i <= 350; i+=25){
			newBorder = new Node(425 - (425 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 450; i <= 525; i+=25){
			newBorder = new Node(i - (i % size), 150 - (150 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 550; i <= 600; i+=25){
			newBorder = new Node(i - (i % size), 75 - (75 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 75; i <= 150; i+=25){
			newBorder = new Node(550 - (550 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 500; i <= 650; i+=25){
			newBorder = new Node(i - (i % size), 225 - (225 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 450; i <= 600; i+=25){
			newBorder = new Node(i - (i % size), 300 - (300 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 250; i <= 425; i+=25){
			newBorder = new Node(i - (i % size), 450 - (450 % size));
			pathfinding.addBorder(newBorder);
		}
		for(int i = 375; i <= 450; i+=25){
			newBorder = new Node(425 - (425 % size), i - (i % size));
			pathfinding.addBorder(newBorder);
		}
		endNode = new Node(450, 325);
		startNode = new Node(25, 25);

	}



}


