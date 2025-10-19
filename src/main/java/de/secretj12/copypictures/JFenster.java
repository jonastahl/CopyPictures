package de.secretj12.copypictures;

import javax.swing.*;
import java.io.Serial;

public class JFenster extends JPanel {
	@Serial
	private static final long serialVersionUID = 6628062700362798627L;

	public void add(JComponent Component, int x, int y, int width, int height){
		Component.setBounds(x, y, width, height);
		this.add(Component);
	}
	
	public void add(JLabel Component, int x, int y){
		this.add(Component, x, y, Component.getText().length()*12, 15);
	}
	
	public void add(JLabel Component, JFrame Frame, int y){
		int length = Component.getText().length()*12;
		int x = (Frame.getWidth()/2)-(length/2);
		JLabel Componentnew = new JLabel(Component.getText(), JLabel.CENTER);
		this.add(Componentnew, x, y);
	}
	
	public void add(JComponent Component, JFrame Frame, int y, int width, int height){
		this.add(Component, (Frame.getWidth()/2)-(width/2), y, width, height);
	}
	
	public JFenster(){
		this.setLayout(null);
	}
}