package com.energieip.i2c;

import java.io.IOException;
import java.util.Arrays;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class PD69200 {

	// I2C generics
	private I2CBus bus;
	private I2CBus i2c;
	private I2CDevice device;

	// PD69200 I2C address
	private static final int PD69200_ADDR = 0x3C;
	
	// message tab
	private byte[] tab = new byte[15];

	/**
	 * Main entry Point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new PD69200();
	}

	/**
	 * Default constructor
	 */
	public PD69200() {
		
		init();
		
		

	} //end of constructor
	
	
	/**
	 * initialize I2C
	 */
	private void init(){
		int[] ids;
		try {
			ids = I2CFactory.getBusIds();
				System.out.println("Found follow I2C busses: " + Arrays.toString(ids));
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
				System.out.println("Working with I2C bus " + bus.getBusNumber());
			i2c = I2CFactory.getInstance(bus.getBusNumber());
			device = i2c.getDevice(PD69200_ADDR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedBusNumberException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end of init
	
	
	/**
	 * Enable pse port
	 * @param port
	 */
	private void pse_enable_port(int port){
		// TODO
	}
	
	
	/**
	 * set PSE unlimited
	 * @param echo
	 * @return
	 */
	private  byte[] pse_force_power(byte echo){
       
		tab[0] = (byte) 0x00; // command
        tab[1] = echo; 
        tab[2] = (byte) 0x05; // channel
        tab[3] = (byte) 0x51; // force power
        tab[4] = (byte) 0x80; // all ports
        tab[5] = (byte) 0x01; // force connection
        tab[6] = (byte) 0x4E;
        tab[7] = (byte) 0x4E;
        tab[8] = (byte) 0x4E;
        tab[9] = (byte) 0x4E;
        tab[10] = (byte) 0x4E;
        tab[11] = (byte) 0x4E;
        tab[12] = (byte) 0x4E;
        tab[13] = (byte) 0x00;
        tab[14] = (byte) 0x00;
        
        tab = checksum(tab);
        
        return tab;
	}
	
	
	private byte[] pse_set_power_limit_for_channels(byte echo, byte channel){
		
		tab[0] = (byte) 0x00; // command
        tab[1] = echo; 
        tab[2] = (byte) 0x05; // channel
        tab[3] = (byte) 0x0B; // Supply
        tab[4] = channel; // CH number or 0x80 for all
        tab[5] = (byte) 0xFF; //     60W => EA    65W => FF    90W => NOT SUPPORTED (3 bytes needed)
        tab[6] = (byte) 0xFE; // 	  60W => 60	   65W => FE    90W => NOT SUPPORTED (3 bytes needed)
        tab[7] = (byte) 0x4E;
        tab[8] = (byte) 0x4E;
        tab[9] = (byte) 0x4E;
        tab[10] = (byte) 0x4E;
        tab[11] = (byte) 0x4E;
        tab[12] = (byte) 0x4E;
        tab[13] = (byte) 0x00;
        tab[14] = (byte) 0x00;
        
        tab = checksum(tab);
		
		return tab;
	}
	
	
	
	private byte[] pse_4_pair_ports_parameters(byte echo){
		
		tab[0] = (byte) 0x00; // command
        tab[1] = echo; 
        tab[2] = (byte) 0x05; // channel
        tab[3] = (byte) 0x00; // PortFullInit4Pair
        tab[4] = (byte) 0x80; // CH number or 0x80 for all
        tab[5] = (byte) 0x4E; // default (2 nibbles) 
        tab[6] = (byte) 0xFF; // 
        tab[7] = (byte) 0xFE; //
        tab[8] = (byte) 0x4E; 
        tab[9] = (byte) 0x4E;
        tab[10] = (byte) 0x4E;
        tab[11] = (byte) 0x4E;
        tab[12] = (byte) 0x4E;
        tab[13] = (byte) 0x00;
        tab[14] = (byte) 0x00;
        
        tab = checksum(tab);
		
		return tab;
	}
	
	
	
	
	/**
	 * This method does the checksum
	 * @param tab
	 * @return
	 */
	private byte[] checksum(byte[] tab) {		
		int checksum = 0;
		for (int i = 0; i < tab.length-2; i++) {
				System.out.println("i=" + i + " checksum=" + checksum);
			checksum = checksum + tab[i];
		}
		
		short checksum_short = (short) checksum;
			System.out.println("checksum short=" + checksum_short);
		
		tab[14] = (byte)(checksum_short & 0xff);
		tab[13] = (byte)((checksum_short >> 8) & 0xff);

		return tab;		
	} // end of checksum

} //end of class

// end of code
