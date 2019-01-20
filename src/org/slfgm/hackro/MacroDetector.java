package org.slfgm.hackro;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class MacroDetector implements NativeKeyListener {
	private static File binds = new File("macro-config/binds");
	private static File enabled = new File("macro-config/enabled");
	
	public MacroDetector() {
		try {
			Process luaProcess = Runtime.getRuntime().exec("luamacros/LuaMacros.exe -r SECOND_KEYBOARD_script_for_LUA_MACROS.lua", null, new File("luamacros"));
			Runtime.getRuntime().addShutdownHook(new Thread(new ShutDown(luaProcess)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
		GlobalScreen.addNativeKeyListener(this);
		GlobalScreen.setEventDispatcher(new SwingDispatchService());
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if(e.getKeyCode() == NativeKeyEvent.VC_F24 && enabled()) {
			executeMacro(getBindForKey(getKeyPressed()));
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {}

	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MacroDetector();
			}
		});
	}
	
	private static String getKeyPressed() {
		try {
			return new String(Files.readAllBytes(new File("C:\\AHK\\2nd-keyboard\\LUAMACROS\\keypressed.txt").toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static File getBindForKey(String key) {
		Scanner scan = null;
		try {
			scan = new Scanner(binds);
		} catch (FileNotFoundException e) {
			return null;
		}
		scan.useDelimiter("\n");
		while(scan.hasNext()) {
			String[] line = scan.next().trim().split(";");
			if(line[0].equals(key)) {
				scan.close();
				return new File(line[1]);
			}
		}
		scan.close();
		return null;
	}
	
	private static void useKey(int keyCode) {
		try {
			Robot robot = new Robot();
			if(keyCode < 0) {
				robot.keyRelease(-keyCode);
			} else {
				robot.keyPress(keyCode);
			}
		} catch(AWTException e) {
			
		}
	}
	
	private static void executeMacro(File macro) {
		if(macro == null) {
			return;
		}
		String[] lines = null;
		try {
			lines = new String(Files.readAllBytes(macro.toPath())).split("\n");
		} catch (IOException e) {
			return;
		}
		for(String line : lines) {
			useKey(Integer.parseInt(line.trim()));
		}
	}
	
	private static boolean enabled() {
		try {
			return new String(Files.readAllBytes(enabled.toPath())).equals("1");
		} catch (IOException e) {}
		return false;
	}
	
	private static class ShutDown implements Runnable {
		private Process process;
		
		public ShutDown(Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			System.out.println("Killing process");
			process.destroy();
		}
	}

}
