package com.energieip.i2c;

public class Main {

	public static void main(String[] args) {	
		
		PD69200 pd69200_1 = new PD69200(1);
		
		System.out.println(pd69200_1.pse_get_software_version());
		
	}

}
