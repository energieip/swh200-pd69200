package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class GetTotalPower {

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
			PD69200 pd69200_1 = new PD69200(1);
			PD69200 pd69200_2 = new PD69200(2);			
			int power = pd69200_1.pse_get_total_power() + pd69200_2.pse_get_total_power();
			System.out.println(power);
	}

} // end of class
