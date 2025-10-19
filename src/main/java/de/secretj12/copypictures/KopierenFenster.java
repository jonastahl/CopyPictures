package de.secretj12.copypictures;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class KopierenFenster {
	private final JFrame Fenster = new JFrame();
	private final JLabel text = new JLabel("%File%");
	private final JProgressBar ProgressBar = new JProgressBar();
	private final JButton Button = new JButton("Abbrechen");
	private boolean Stopped = false;
	
	KopierenFenster(){
		Fenster.setTitle("CopyPictures");
		Fenster.setSize(448, 187);
		Fenster.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Fenster.setLocationRelativeTo(CopyPictures.Window);
		
		ProgressBar.setStringPainted(true);
		text.setHorizontalAlignment(SwingConstants.LEFT);
		JFenster main = new JFenster();
		main.add(text, 11, 12, 400, 30);
		
		main.add(ProgressBar, 10, 56, 419, 28);
		
		main.add(Button, 113, 103, 208, 34);
		Button.addActionListener(arg0 -> Stopped = true);
		Fenster.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				Stopped = true;
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Fenster.add(main);	
		Fenster.setResizable(false);
		Fenster.setVisible(true);
	}
	
	public void setProgress(int Progress){
		ProgressBar.setValue(Progress);
	}
	
	public void setText(String text){
		this.text.setText(text);
	}
	
	public void setButtonText(String text){
		this.Button.setText(text);
	}
	
	public boolean isStopped(){
		return Stopped;
	}
	
	public void setVisible(boolean Visible){
		if(!Visible){
			Fenster.dispose();
		}
	}
	
	public void setCloseable(boolean Closeable){
		if(Closeable){
			Button.addActionListener(e -> Fenster.dispose());
		}
	}
}
