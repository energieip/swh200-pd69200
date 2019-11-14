package com.energieip.i2c.commands;

import java.time.chrono.ThaiBuddhistDate;

import com.energieip.i2c.PD69200;

public class SetBTPortParameters {

	/**
	 * main entry entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 3) {
			command(args);
		} else {
			System.err.println("Usage: [Command] i2Cbus portNum portModeCFG1");
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
			int thirdArg = Integer.parseInt(args[2]);
			PD69200 pd69200 = new PD69200(firstArg);
			Thread.sleep(50);
			
			byte portModeCFG1 =  0x1; // enable by default
			byte portModeCFG1_enable =  0x1;
			byte portModeCFG1_disable =  0x0;
			byte portModeCFG2 = 0x0;
			byte portOperationMode =  0x20;
			byte portAddPower =  0x4B;
			byte portPriority =  0xF;
			
			if(thirdArg==0){
				portModeCFG1=portModeCFG1_disable;
			} else {
				portModeCFG1=portModeCFG1_enable;
			}
			
			if (secondArg < 255) {
				pd69200.pse_set_bt_port_parameters((byte)secondArg, portModeCFG1, portModeCFG2, portOperationMode, portAddPower, portPriority);
			}else { // 255 
				int port_limit=0;	
				if(firstArg==0){//32 ports
					port_limit=32;}
				else if(firstArg==1){//24 ports
					port_limit=24;
				}				
					
				for (int i = 0; i < port_limit; i++) {
					boolean ok = pd69200.pse_set_bt_port_parameters((byte)i, portModeCFG1, portModeCFG2, portOperationMode, portAddPower, portPriority);
					Thread.sleep(100);				
					if(!ok){
						for (int j = 0; j < 3; j++) {
							pd69200.i2cClose();
							pd69200 = new PD69200(firstArg);
							Thread.sleep(50);
							ok = pd69200.pse_set_bt_port_parameters((byte)i, portModeCFG1, portModeCFG2, portOperationMode, portAddPower, portPriority);
							if(ok){
								break;
							}else{
								System.out.println("BAZOOKA #" + j);
							}
						}
						
					}
				}
				System.out.println("done");
				
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
