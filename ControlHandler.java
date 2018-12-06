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

/* This class manages all components used on the main
 * control panel (bottom left) Meant to remove some
 * excessive graphics code from "Frame.java" class
 * by Devon Crawford
 */
public class ControlHandler {
	private Frame frame;
	private JLabel noPathT;
	private JCheckBox diagonalCheck;
	private JButton run;
	Dimension npD;
	
	public ControlHandler(Frame frame) {
		this.frame = frame;
	
		noPathT = new JLabel("NO PATH");
		noPathT.setName("noPathT");
		noPathT.setForeground(Color.white);
		Font bigTextFont = new Font("arial", Font.BOLD, 72);
		noPathT.setFont(bigTextFont);
		npD = noPathT.getPreferredSize();
		
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
	

	public void noPathTBounds() {
		noPathT.setBounds((int)((frame.getWidth()/2)-(npD.getWidth()/2)), 
				(int)((frame.getHeight()/2)-70), 
				(int)npD.getWidth(), (int)npD.getHeight());
	}
	





	// diagonalCheck ve run'in pozisyonu ayarlandi.
		public void position() {
		diagonalCheck.setBounds(20, frame.getHeight()-30, 90, 20);
		run.setBounds(20, frame.getHeight()-50, 50, 20);
	}
	
	// diagonalCheck ve run frame'e eklenmeli.
	public void addAll() {
		frame.add(diagonalCheck);
		frame.add(run);
	}
	
}
