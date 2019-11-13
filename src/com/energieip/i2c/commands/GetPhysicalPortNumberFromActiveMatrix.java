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
			System.err.println("Tips: set portnum at 255 for all ports");
			System.err.println("i.e.: GetPhysicalPortNumberFromActiveMatrix 0 5");
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
			if(secondArg==255){
				byte[][] matrix = new byte[48][2];
				for (int i = 0; i < 48; i++) {
					byte[] buf = pd69200.pse_get_physical_port_number_from_active_matrix((byte)i);
					matrix[i][0] = buf[2];
					matrix[i][1] = buf[3];
					System.out.println("["+i+"] " + buf[2] + " " + buf[3]);
				}
			}else {
				byte[] buf = pd69200.pse_get_physical_port_number_from_active_matrix((byte)secondArg);
				//pd69200.printBuffer(buf);
				System.out.println("["+secondArg+"] " + buf[2] + " " + buf[3]);
			}
		} catch (NumberFormatException e) {
			System.err.println("Arguments" + args[0] + " and " + args[1] + " must be integers.");
			System.exit(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

} // end of class
