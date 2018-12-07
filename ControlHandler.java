import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ControlHandler {
	private Frame frame;
	private JLabel noPathT;
	private JLabel timePassed;
	private JCheckBox diagonalCheck;
	private JButton run;
	Dimension npD, npE;
	
	public ControlHandler(Frame frame) {
		this.frame = frame;
	
		noPathT = new JLabel("NO PATH");
		noPathT.setName("noPathT");
		noPathT.setForeground(Color.white);
		Font bigTextFont = new Font("arial", Font.BOLD, 72);
		noPathT.setFont(bigTextFont);
		npD = noPathT.getPreferredSize();
		noPathT.setVisible(false);

		timePassed = new JLabel("aaa");
		timePassed.setName("timePassed");
		timePassed.setForeground(Color.gray);
		Font timePassedFont = new Font("arial", Font.BOLD, 50);
		timePassed.setFont(timePassedFont);
		npE = timePassed.getPreferredSize();
		timePassed.setVisible(false);

		diagonalCheck = new JCheckBox();
		diagonalCheck.setText("Diagonal");
		diagonalCheck.setName("diagonalCheck");
		diagonalCheck.setOpaque(false);
		diagonalCheck.setSelected(true);
		diagonalCheck.setFocusable(false);
		diagonalCheck.setVisible(true);

		run = new JButton();
		run.setText("run");
		run.setName("run");
		run.setFocusable(false);
		run.addActionListener(frame);
		run.setMargin(new Insets(0,0,0,0));
		run.setVisible(true);
	}

	public JCheckBox getDiagonalCheck(){
		return diagonalCheck;
	}
	public JLabel getNoPathT(){
		return noPathT;
	}
	public JButton getRun(){
		return run;
	}
	public JLabel getTimePassed(){
		return timePassed;
	}

	





	// Tum pozisyonlar ayarlandi.
		public void position() {
		diagonalCheck.setBounds(frame.getWidth()/2 + 20, 0, 90, 20);
		run.setBounds(frame.getWidth()/2 - 60, 0, 50, 20);//Yataykoord, dikeykoord, yataybuyukluk, dikeybuyukluk 
		timePassed.setBounds((int)((frame.getWidth()/2 - 50)-(npE.getWidth()/2)), 
		(int)((frame.getHeight()/2)+200), 
		400, (int)npE.getHeight());
		
		noPathT.setBounds((int)((frame.getWidth()/2)-(npD.getWidth()/2)), 
		(int)((frame.getHeight()/2)-70), 
		(int)npD.getWidth(), (int)npD.getHeight());
	}
	
	// Tum itemlar frame'e eklenmeli.
	public void addAll() {
		frame.add(diagonalCheck);
		frame.add(run);
		frame.add(noPathT);
		frame.add(timePassed);
	}
	
}
