package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class GetBTPortParameters {

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
			System.err.println("Set portNum at 255 for all ports");
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

			if (secondArg < 255) {
				byte[] buf = pd69200.pse_get_BT_port_parameters(secondArg);
				if (buf[0] == 0x03) { // Telemetry

					byte port_status = buf[2];
					byte port_mode_CFG1 = buf[3];
					byte port_mode_CFG2 = buf[4];
					byte port_operation_mode = buf[5];
					byte port_add_power = buf[6];
					
					System.out.println("[" + secondArg + "] port_status=" + String.format("0x%02X",port_status) + " port_mode_CFG1="
							+ String.format("0x%02X",port_mode_CFG1) + " port_mode_CFG2=" + String.format("0x%02X",port_mode_CFG2) + " port_operation_mode="
							+ String.format("0x%02X",port_operation_mode) + " port_add_power=" + String.format("0x%02X",port_add_power));
				} else {
					System.out.println("[" + secondArg + "] Telemetry error");
				}
			}else { // 255 
				int port_limit=0;
				if(firstArg==0){//32 ports
					port_limit=32;}
				else if(firstArg==1){//24 ports
					port_limit=24;}
				
					for (int i = 0; i < port_limit; i++) {
						byte[] buf = pd69200.pse_get_BT_port_parameters(i);
						if (buf[0] == 0x03) { // Telemetry

							byte port_status = buf[2];
							byte port_mode_CFG1 = buf[3];
							byte port_mode_CFG2 = buf[4];
							byte port_operation_mode = buf[5];
							byte port_add_power = buf[6];
							
							System.out.println("[" + i + "] port_status=" + String.format("0x%02X",port_status) + " port_mode_CFG1="
									+ String.format("0x%02X",port_mode_CFG1) + " port_mode_CFG2=" + String.format("0x%02X",port_mode_CFG2) + " port_operation_mode="
									+ String.format("0x%02X",port_operation_mode) + " port_add_power=" + String.format("0x%02X",port_add_power));
						} else {
							System.out.println("[" + i + "] Telemetry error");
						}
						Thread.sleep(100);
					}
				
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
