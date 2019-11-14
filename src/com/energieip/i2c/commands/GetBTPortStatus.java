package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class GetBTPortStatus {

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
				byte[] buf = pd69200.pse_get_BT_port_status(secondArg);
				if (buf[0] == 0x03) { // Telemetry

					byte port_status = buf[2];
					byte port_mode_CFG1 = buf[3];
					byte assigned_class = buf[4];
					int measured_port_power = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);
					byte port_event = buf[10];

					System.out.println("[" + secondArg + "] port_status=" + port_status + " port_mode_CFG1="
							+ port_mode_CFG1 + " assigned_class=" + assigned_class + " measured_port_power="
							+ measured_port_power + " port_event=" + port_event);
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
						byte[] buf = pd69200.pse_get_BT_port_status(i);
						if (buf[0] == 0x03) { // Telemetry

							byte port_status = buf[2];
							byte port_mode_CFG1 = buf[3];
							byte assigned_class = buf[4];
							int measured_port_power = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);
							byte port_event = buf[10];

							System.out.println("[" + i + "] port_status=" + (byte)port_status + " port_mode_CFG1="
									+ (byte)port_mode_CFG1 + " assigned_class=" + (byte)assigned_class + " measured_port_power="
									+ measured_port_power + " port_event=" + (byte)port_event);
						} else {
							System.out.println("[" + secondArg + "] Telemetry error");
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
