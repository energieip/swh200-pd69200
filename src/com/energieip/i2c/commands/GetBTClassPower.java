package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class GetBTClassPower {

	/**
	 * main entry entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			command(args);
		} else {
			System.err.println("Usage: [Command] i2Cbus class");
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
			int secondArg = Integer.parseInt(args[1]);
			PD69200 pd69200 = new PD69200(firstArg);
			System.out.println(pd69200.pse_get_BT_class_power(secondArg));
		} catch (NumberFormatException e) {
			System.err.println("Arguments" + args[0] + " and " + args[1] + " must be integers.");
			System.err.println("Argument" + args[1] + " must be from 1 to 8.");
			System.exit(1);
		}	}

} // end of class
