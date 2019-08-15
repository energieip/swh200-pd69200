package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class ResetCommandAll {

	/**
	 * main entry entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
			command(args);
	}

	
	/**
	 * command
	 * 
	 * @param args
	 */
	private static void command(String[] args) {
			PD69200 pd69200_0 = new PD69200(0);
			PD69200 pd69200_1 = new PD69200(1);
			System.out.println(pd69200_0.pse_reset_command());
			System.out.println(pd69200_1.pse_reset_command());		
	}

} // end of class
