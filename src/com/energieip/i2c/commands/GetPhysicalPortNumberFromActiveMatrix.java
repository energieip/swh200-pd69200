package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class GetPhysicalPortNumberFromActiveMatrix {

	/**
	 * main entry entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			command(args);
		} else {
			System.err.println("Usage: [Command] i2Cbus portNum");
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
			Thread.sleep(50);
			byte[] buf = pd69200.pse_get_physical_port_number_from_active_matrix((byte)secondArg);
			pd69200.printBuffer(buf);
		} catch (NumberFormatException e) {
			System.err.println("Arguments" + args[0] + " and " + args[1] + " must be integers.");
			System.exit(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

} // end of class
