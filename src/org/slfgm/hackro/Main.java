package org.slfgm.hackro;

public class Main {

	public static void main(String[] args) {
		switch(args[0]) {
		case "config": MacroConfig.main(); break;
		case "detector": MacroDetector.main(); break;
		case "recorder": MacroRecorder.main(); break;
		}
	}

}
