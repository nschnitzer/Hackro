package org.slfgm.hackro;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

public class MacroRecorder extends JFrame implements KeyListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private static File macros = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+"/Hackro");
	
	static {
		macros.mkdirs();
	}
	
	private boolean recording = false;
	private List<Integer> buffer = new ArrayList<>();
	
	private JButton button = new JButton("Start Recording");
	private JLabel label = new JLabel("Use this tool to record macros.");

	public MacroRecorder() {
		setTitle("Hackro Recorder");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		label.addKeyListener(this);
		getContentPane().add(label, BorderLayout.NORTH);
		
		button.setPreferredSize(new Dimension(700, 300));
		button.setFont(new Font("Calibri", 0, 36));
		button.addActionListener(this);
		getContentPane().add(button, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
		label.requestFocusInWindow();
		label.setFocusTraversalKeysEnabled(false);
	}

	public static void main(String... args) {
		new MacroRecorder();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(recording) {
			buffer.add(e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(recording) {
			buffer.add(-e.getKeyCode());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		label.requestFocusInWindow();
		if(recording) {
			String macroStr = "";
			for(Integer i : buffer) {
				macroStr += i+"\n";
			}
			if(macroStr.length() > 0) {
				macroStr = macroStr.substring(0, macroStr.length()-1);
			}
			
			String name = JOptionPane.showInputDialog(this, "Name the macro: ", "Hackro Recorder", JOptionPane.QUESTION_MESSAGE);
			String path = macros.getAbsolutePath() + "/" + name;
			File macroFile = new File(path);
			try {
				macroFile.createNewFile();
				Files.write(macroFile.toPath(), macroStr.getBytes());
			} catch (IOException e1) {}
			
			buffer.clear();
			
			button.setText("Start Recording");
		} else {
			button.setText("Stop Recording");
		}
		recording = !recording;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
