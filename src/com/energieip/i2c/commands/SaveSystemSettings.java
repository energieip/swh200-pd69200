package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class SaveSystemSettings {

	/**
	 * main entry entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			command(args);
		} else {
			System.err.println("Usage: [Command] i2Cbus");
			System.exit(1);
		}
	}

	
	/**
	 * command
	 * 
	 * @param args
	 */
	private static void command(String[] args) {
		try {
			int firstArg = Integer.parseInt(args[0]);
			PD69200 pd69200 = new PD69200(firstArg);
			System.out.println(pd69200.pse_save_system_settings());
		} catch (NumberFormatException e) {
			System.err.println("Argument" + args[0] + " must be an integer.");
			System.exit(1);
		}
	}

} // end of class
