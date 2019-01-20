package org.slfgm.hackro;

import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class MacroConfig extends JFrame implements NativeKeyListener, WindowListener {
	private static final long serialVersionUID = 1L;
	private static File binds = new File("macro-config/binds");
	private static File enabled = new File("macro-config/enabled");
	private static File macros = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+"/Hackro");
	
	static {
		macros.mkdirs();
	}
	
	public MacroConfig() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
		GlobalScreen.addNativeKeyListener(this);
		GlobalScreen.setEventDispatcher(new SwingDispatchService());
		
		setTitle("Hackro Configuration");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		addWindowListener(this);
		
		JLabel label = new JLabel("Press a key on the macro keypad...");
		label.setFont(new Font("Calibri", 0, 36));
		add(label);
		setSize(700, 300);
		
		disableMacro();
		setVisible(true);
	}
	
	private File chooseMacro(String key) {
		File[] files = macros.listFiles();
		String[] options = new String[files.length];
		for(int i=0; i<files.length; i++) {
			options[i] = files[i].getName();
		}
		int index = JOptionPane.showOptionDialog(this, "Choose a macro for "+key+":", "Macro Selection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		return index==JOptionPane.CLOSED_OPTION ? null : files[index];
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MacroConfig();
			}
		});
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if(e.getKeyCode() == NativeKeyEvent.VC_F24) {
			String key = getKeyPressed();
			File macro = chooseMacro(key);
			if(macro == null) {
				return;
			}
			String path = macro.getAbsolutePath();
			setBindForKey(key, path);
		}
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		try {
			GlobalScreen.unregisterNativeHook();
		}
		catch (NativeHookException ex) {
			ex.printStackTrace();
		}
		enableMacro();
		System.runFinalization();
		System.exit(0);
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
	
	private static boolean hasBindForKey(String key) {
		Scanner scan = null;
		try {
			scan = new Scanner(binds);
		} catch (FileNotFoundException e) {
			return false;
		}
		scan.useDelimiter("\n");
		while(scan.hasNext()) {
			String[] line = scan.next().trim().split(";");
			if(line[0].equals(key)) {
				scan.close();
				return true;
			}
		}
		scan.close();
		return false;
	}
	
	private static void setBindForKey(String key, String path) {
		String bindStr = null;
		try {
			bindStr = new String(Files.readAllBytes(binds.toPath()));
		} catch (IOException e) {
			return;
		}
		if(hasBindForKey(key)) {
			bindStr = bindStr.replaceAll("\n"+key+";.+", "");
		}
		if(!bindStr.isEmpty())
			bindStr += "\n";
		bindStr += key+";"+path;
		try {
			Files.write(binds.toPath(), bindStr.getBytes());
		} catch (IOException e) {
			return;
		}
	}
	
	private static void enableMacro() {
		try {
			Files.write(enabled.toPath(), "1".getBytes());
		} catch (IOException e) {}
	}
	
	private static void disableMacro() {
		try {
			Files.write(enabled.toPath(), "0".getBytes());
		} catch (IOException e) {}
	}
	
	private static String getKeyPressed() {
		try {
			return new String(Files.readAllBytes(new File("C:\\AHK\\2nd-keyboard\\LUAMACROS\\keypressed.txt").toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
