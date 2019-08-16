package com.energieip.i2c;

import java.io.IOException;
import java.util.Arrays;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class PD69200 {
	
	// API VERSION
	public final String VERSION = "3.45";

	// I2C generic
	private I2CBus bus;
	private I2CBus i2c;
	private I2CDevice device;

	// PD69200 I2C address
	private final int PD69200_ADDR = 0x3C;
	private int i2c_bus;

	// message tab
	private byte[] tab = new byte[15]; // output buffer tab
	private byte echo = 0;
	private byte[] buf = new byte[15]; // input buffer tab

	// const
	final int POWER_MAX = 6000;
	final int POWER_MIN = 0;
	
	// debug
	boolean DEBUG = false;

	/**
	 * Default constructor
	 * 
	 * @param i2c_bus
	 */
	public PD69200(int _i2c_bus) {

		i2c_bus = _i2c_bus;
		
		// initialize I2C
		initi2c();
		
		// first command, initialize echo
		pse_get_software_version();

	} // end of constructor

	/**
	 * initialize I2C
	 */
	private void initi2c() {
		int[] ids;
		try {
			if(DEBUG){
				System.out.println("Initializing I2C...");
			}
			ids = I2CFactory.getBusIds();
			if(DEBUG){
				System.out.println("Found I2C bus: " + Arrays.toString(ids));
			}
			if (i2c_bus == 0) {
				bus = I2CFactory.getInstance(I2CBus.BUS_0);
			} else if (i2c_bus == 1) {
				bus = I2CFactory.getInstance(I2CBus.BUS_1);
			} else {
				System.err.println("[ERROR], bad I2C bus");
				System.exit(0);
			}
			if(DEBUG){
				System.out.println("Working with I2C bus " + bus.getBusNumber());
			}
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


	private byte[] pse_set_temporary_matrix(byte get_echo, byte l) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0x43; // temporary matrix
		tab[4] = l; // all ports
		tab[5] = (byte) (l * (byte) 2);
		tab[6] = (byte) (l * (byte) 2 + (byte) 1);
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

	private byte[] pse_program_global_matrix(byte get_echo) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07;
		tab[3] = (byte) 0x43; // temporary matrix
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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

	/**
	 * set PSE unlimited
	 * 
	 * @param echo
	 * @return
	 */
	private byte[] pse_force_power(byte echo) {

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

	private byte[] pse_set_individual_mask(byte echo) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07;
		tab[3] = (byte) 0x56;
		tab[4] = (byte) 0x34; // 0x34: New matrix (4 pairs)
		tab[5] = (byte) 0x01; // enable
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

	private byte[] pse_set_power_limit_for_channels(byte echo, byte channel) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0x0B; // Supply
		tab[4] = channel; // CH number or 0x80 for all
		tab[5] = (byte) 0xEA; // 60W => EA 65W => FF 90W => NOT SUPPORTED (3
								// bytes needed)
		tab[6] = (byte) 0x60; // 60W => 60 65W => FE 90W => NOT SUPPORTED (3
								// bytes needed)
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

	private byte[] pse_enable_4_pair_for_channels(byte echo) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0x02; // enable/disable 4 Pair
		tab[4] = (byte) 0x80; // CH number or 0x80 for all
		tab[5] = (byte) 0x01; // enable 4 pair (0x00 for disable)
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

	private byte[] pse_set_4_pair_ports_parameters(byte echo) {

		byte[] tab_new = new byte[15]; // output buffer tab
		/*
		 * System.out.println("DEBUG0"); printBuffer(tab_new);
		 */

		tab_new[0] = (byte) 0x00; // command
		tab_new[1] = echo;
		tab_new[2] = (byte) 0x05; // channel
		tab_new[3] = (byte) 0xAF; // PortFullInit4Pair
		tab_new[4] = (byte) 0x80; // CH number or 0x80 for all
		tab_new[5] = (byte) 0x4E; // default (2 nibbles)
		tab_new[6] = (byte) 0xFF; // PPL4Pair
		tab_new[7] = (byte) 0xFF; // PPL4Pair
		tab_new[8] = (byte) 0x4E; // Priority
		tab_new[9] = (byte) 0x02; // PoH
		tab_new[10] = (byte) 0x00; // Sum_as_TPPL, Energy management
		tab_new[11] = (byte) 0x4E; // PortPM2
		tab_new[12] = (byte) 0x4E;
		tab_new[13] = (byte) 0x00;
		tab_new[14] = (byte) 0x00;

		// System.out.println("DEBUG1");
		// printBuffer(tab_new);

		tab_new = checksum(tab_new);

		// System.out.println("DEBUG2");
		// printBuffer(tab_new);

		return tab_new;
	}

	private byte[] pse_get_4_pair_ports_parameters(byte echo) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0xAF; // PortFullInit4Pair
		tab[4] = (byte) 0x80; // CH number or 0x80 for all
		tab[5] = (byte) 0x4E; // default (2 nibbles)
		tab[6] = (byte) 0x4E; // PPL4Pair
		tab[7] = (byte) 0x4E; // PPL4Pair
		tab[8] = (byte) 0x4E; // Priority
		tab[9] = (byte) 0x4E; // PoH
		tab[10] = (byte) 0x4E; // Sum_as_TPPL, Energy management
		tab[11] = (byte) 0x4E; // PortPM2
		tab[12] = (byte) 0x4E;
		tab[13] = (byte) 0x00;
		tab[14] = (byte) 0x00;

		tab = checksum(tab);

		return tab;
	}

	private byte[] pse_set_4_pair_power_limit(byte echo) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0xAD; // Supply4Pair
		tab[4] = (byte) 0x80; // CH number or 0x80 for all
		tab[5] = (byte) 0xEA; // PPL4Pair EA60=60W
		tab[6] = (byte) 0x60; // PPL4Pair
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

	private byte[] pse_set_4_pair_temporary_power_limit(byte echo) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0xAE; // Supply4Pair
		tab[4] = (byte) 0x80; // CH number or 0x80 for all
		tab[5] = (byte) 0xFF; // PPL4Pair
		tab[6] = (byte) 0xFE; // PPL4Pair
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

	/**
	 * pse_save_system_settings
	 * @return boolean
	 */
	public boolean pse_save_system_settings() {

		// must wait 50ms before using I2C when sending this command
		// response should come after 50ms
		
		try{
		tab[0] = (byte) 0x1; // program
		tab[1] = get_echo();
		tab[2] = (byte) 0x06;
		tab[3] = (byte) 0x0F; // save config
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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
		
		device.write(tab);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return true;
	}
	

	/**
	 * Reset PSE
	 * @return boolean
	 */
	public boolean pse_reset_command() {

		try{
		tab[0] = (byte) 0x0; // command
		tab[1] = get_echo();
		tab[2] = (byte) 0x07; // gloabl
		tab[3] = (byte) 0x55; // reset
		tab[4] = (byte) 0x00;
		tab[5] = (byte) 0x55; // reset
		tab[6] = (byte) 0x00;
		tab[7] = (byte) 0x55; // reset
		tab[8] = (byte) 0x4E;
		tab[9] = (byte) 0x4E;
		tab[10] = (byte) 0x4E;
		tab[11] = (byte) 0x4E;
		tab[12] = (byte) 0x4E;
		tab[13] = (byte) 0x00;
		tab[14] = (byte) 0x00;

		tab = checksum(tab);
		
		device.write(tab);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return true;
	}
	
	/**
	 * pse_restore_factory_default
	 * @return boolean
	 */
	public boolean pse_restore_factory_default() {

		try{
		tab[0] = (byte) 0x01; // program
		tab[1] = get_echo();
		tab[2] = (byte) 0x2D; // restore factory default
		tab[3] = (byte) 0x4E; 
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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
		
		device.write(tab);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return true;
	}

	private byte[] pse_get_system_status(byte echo) {

		tab[0] = (byte) 0x2; // request
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0x3D; // System status
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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
	
	/**
	 * return PSE total power
	 * @return int
	 */
	public int pse_get_total_power() {
		
		int power = 0;

		try {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0x0B; // Supply
		tab[4] = (byte) 0x60; // Total Power
		tab[5] = (byte) 0x4E;
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

		device.write(tab);
	
		while (true) {
			device.read(buf, 0, 1);
			if (buf[0] != 0) {
				device.read(buf, 1, 14);
				break;
			}
		}

		if(DEBUG){
			printBuffer(buf);
		}

		if (buf[0] == 0x03) { // Telemetry
			power = ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);	
		}

	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return power;
}
	
	
	public String pse_get_port_number_from_active_matrix(int channel) {

		int Physical_number_A=0;
		int Physical_number_B=0;
		
		try {
			tab[0] = (byte) 0x02; // command
			tab[1] = get_echo();
			tab[2] = (byte) 0x05; // channel
			tab[3] = (byte) 0x44; // channel matrix
			tab[4] = (byte) channel; // channel number
			tab[5] = (byte) 0x4E;
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

			device.write(tab);
						
			while (true) {
				device.read(buf, 0, 1);
				if (buf[0] != 0) {
					device.read(buf, 1, 14);
					break;
				}
			}

			if(DEBUG) {
				printBuffer(buf);
			}
			
			if (buf[0] == 0x03) { // Telemetry
				Physical_number_A = buf[2];
				Physical_number_B = buf[3];
				if(DEBUG){
					System.out.println("Physical_number_A=" + Physical_number_A);
					System.out.println("Physical_number_B=" + Physical_number_B);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.toString(Physical_number_A)+" "+Integer.toString(Physical_number_B);
	}
	
	
	public String pse_get_BT_class_power(int class_type) {

		int class_power_value=0;
		int added_class_power_value=0;
		int max_added_class_power_value=0;
		
		try {
			tab[0] = (byte) 0x02; // command
			tab[1] = get_echo();
			tab[2] = (byte) 0x07; // global
			tab[3] = (byte) 0xD2; // BT Class Power
			tab[4] = (byte) class_type; // class type
			tab[5] = (byte) 0x4E;
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

			device.write(tab);

			int i = 1;
			
			while (true) {
				int res = device.read(buf, 0, 1);
				if (buf[0] != 0) {
					int pos = device.read(buf, 1, 14);
					break;
				}
			}

			if(DEBUG) {
				printBuffer(buf);
			}
			
			if (buf[0] == 0x03) { // Telemetry
				class_power_value = ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
				added_class_power_value = buf[4];
				max_added_class_power_value = buf[5];
				if(DEBUG){
					System.out.println("AddedClassPowerValue=" + class_power_value);
					System.out.println("AddedClassPowerValue=" + buf[4]);
					System.out.println("MaxAddedClassPowerValue=" + buf[5]);					
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.toString(class_power_value)+" "+Integer.toString(added_class_power_value)+" "+Integer.toString(max_added_class_power_value);
	}
	
	/**
	 * pse_get_BT_system_status
	 * @return String
	 */
	public String pse_get_BT_system_status() {

		int cpu_status_err_codes=0;
		int factory_default=0;
		int RAM_private_label=0;
		int NVM_user_byte=0;
		int active_devices=0;
		int found_devices_after_boot=0;
		
		try {
			tab[0] = (byte) 0x02; // request
			tab[1] = get_echo();
			tab[2] = (byte) 0x07; // global
			tab[3] = (byte) 0xD0; // BT system status
			tab[4] = (byte) 0x4E;
			tab[5] = (byte) 0x4E;
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

			device.write(tab);

			int i = 1;
			
			while (true) {
				int res = device.read(buf, 0, 1);
				if (buf[0] != 0) {
					int pos = device.read(buf, 1, 14);
					break;
				}
			}

			if(DEBUG) {
				printBuffer(buf);
			}
			
			if (buf[0] == 0x03) { // Telemetry
				
				cpu_status_err_codes = byteToInt(buf[3]);
				factory_default = byteToInt(buf[4]);
				RAM_private_label= byteToInt(buf[6]);
				NVM_user_byte = byteToInt(buf[7]);
				active_devices = byteToMSB(buf[8]);
				found_devices_after_boot = byteToLSB(buf[8]);
				
				if(DEBUG){
					System.out.println("cpu_status_err_codes=" + cpu_status_err_codes);
					System.out.println("factory_default=" + factory_default);
					System.out.println("RAM_private_label=" + RAM_private_label);		
					System.out.println("NVM_user_byte=" + NVM_user_byte);		
					System.out.println("active_devices=" + active_devices);
					System.out.println("found_devices_after_boot=" + found_devices_after_boot);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.toString(cpu_status_err_codes)+" "+Integer.toString(factory_default)+" "+Integer.toString(RAM_private_label)+" "+Integer.toString(NVM_user_byte)+" "+Integer.toString(active_devices)+" "+Integer.toString(found_devices_after_boot);
	}
	
	// LSB is the right part
	private int byteToLSB(byte b) {
		byte result = (byte) (b >>4);
		return result;
	}
	
	// MSB is the left part
	private int byteToMSB(byte b) {
		byte result = (byte) ((b & 0xFF) >> 4);
		return byteToInt(result);
	}


	/**
	 * byte To Java Int
	 * @param b byte
	 * @return int
	 */
	private int byteToInt(byte b) {
		int a=0;
		a = b;
		if(a<0){
			a=a+256;
		}
		return a;
	}

	/**
	 * pse_get_BT_port_parameters
	 * @param int port_num
	 * @return string
	 */
	public String pse_get_BT_port_parameters(int port_num) {

		int port_status=0;
		int port_mode_CFG1=0;
		int port_mode_CFG2=0;
		int port_operation_mode=0;
		int add_power_for_port_mode=0;
		int priority=0;		
		
		try {
			tab[0] = (byte) 0x02; // request
			tab[1] = get_echo();
			tab[2] = (byte) 0x05; // channel
			tab[3] = (byte) 0xC0; // BT Port Config1 
			tab[4] = (byte) port_num;
			tab[5] = (byte) 0x4E;
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
			
			if(DEBUG){
				printBuffer(tab);
			}
			
			device.write(tab);
			
			while (true) {
				device.read(buf, 0, 1);
				if (buf[0] != 0) {
					device.read(buf, 1, 14);
					break;
				}
			}

			if(DEBUG) {
				printBuffer(buf);
			}
			
			if (buf[0] == 0x03) { // Telemetry
				
				port_status = buf[2];
				port_mode_CFG1 = buf[3];
				port_mode_CFG2 = buf[4];
				port_operation_mode = buf[5];
				add_power_for_port_mode = buf[6];
				priority = buf[7];
				
				if(DEBUG){
					System.out.println("port_status=" + port_status);
					System.out.println("port_mode_CFG1=" + port_mode_CFG1);
					System.out.println("port_mode_CFG2=" + port_mode_CFG2);		
					System.out.println("port_operation_mode=" + port_operation_mode);		
					System.out.println("add_power_for_port_mode=" + add_power_for_port_mode);
					System.out.println("priority=" + priority);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.toString(port_status)+" "+Integer.toString(port_mode_CFG1)+" "+Integer.toString(port_mode_CFG2)+" "+Integer.toString(port_operation_mode)+" "+Integer.toString(add_power_for_port_mode)+" "+Integer.toString(priority);
	}
	
	/**
	 * pse_get_BT_port_status
	 * @param int port_num
	 * @return String
	 */
	public String pse_get_BT_port_status(int port_num) {

		int port_status=0;
		int port_mode_CFG1=0;
		int assigned_class=0;
		int measured_port_power=0;		
		
		try {
			tab[0] = (byte) 0x02; // request
			tab[1] = get_echo();
			tab[2] = (byte) 0x05; // channel
			tab[3] = (byte) 0xC1; // BT Port status 
			tab[4] = (byte) port_num;
			tab[5] = (byte) 0x4E;
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
			
			if(DEBUG){
				printBuffer(tab);
			}
			
			device.write(tab);
			
			while (true) {
				device.read(buf, 0, 1);
				if (buf[0] != 0) {
					device.read(buf, 1, 14);
					break;
				}
			}

			if(DEBUG) {
				printBuffer(buf);
			}
			
			if (buf[0] == 0x03) { // Telemetry
				
				port_status = buf[2];
				port_mode_CFG1 = buf[3];
				assigned_class = buf[4];
				measured_port_power = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);	
				
				
				if(DEBUG){
					System.out.println("port_status=" + port_status);
					System.out.println("port_mode_CFG1=" + port_mode_CFG1);
					System.out.println("assigned_class=" + assigned_class);		
					System.out.println("measured_port_power=" + measured_port_power);		
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.toString(port_status)+" "+Integer.toString(port_mode_CFG1)+" "+Integer.toString(assigned_class)+" "+Integer.toString(measured_port_power);
	}


	
	
	/**
	 * return PSE software version
	 * @return int
	 */
	public int pse_get_software_version() {

		int version = 0;

		try {
			tab[0] = (byte) 0x02; // command
			tab[1] = get_echo();
			tab[2] = (byte) 0x07; // global
			tab[3] = (byte) 0x1E; // VersionZ
			tab[4] = (byte) 0x21; // SW Version
			tab[5] = (byte) 0x4E;
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

			device.write(tab);

			int i = 1;
			
			while (true) {
				int res = device.read(buf, 0, 1);
				if (buf[0] != 0) {
					int pos = device.read(buf, 1, 14);
					// System.out.println("go :" + i + " pos:" + pos);
					break;
				}
			}

			if(DEBUG){
				printBuffer(buf);
			}

			if (buf[0] == 0x03) { // Telemetry
				version = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version;
	}

	private byte[] pse_get_power_supply_voltage(byte echo) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0x0B; // Supply
		tab[4] = (byte) 0x1A; // Measurement
		tab[5] = (byte) 0x4E;
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

	private byte[] pse_get_all_4_pair_port_power_1(byte echo) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0xB0; // #1
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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

	private byte[] pse_get_all_4_pair_port_power_2(byte echo) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0xB1; // #2
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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

	private byte[] pse_get_all_4_pair_port_power_3(byte echo) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0xB2; // #3
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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

	private byte[] pse_get_all_4_pair_port_power_4(byte echo) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0xB3; // #4
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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

	private byte[] pse_get_all_4_pair_port_power_5(byte echo) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x07; // global
		tab[3] = (byte) 0xB4; // #5
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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

	private byte[] pse_get_4_pair_port_power(byte echo, byte port) {

		tab[0] = (byte) 0x02; // command
		tab[1] = echo;
		tab[2] = (byte) 0x01; // 4 pairs
		tab[3] = port;
		tab[4] = (byte) 0x4E;
		tab[5] = (byte) 0x4E;
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

	private byte[] pse_enable_channels(byte echo) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0x0C;
		tab[4] = (byte) 0x80;
		tab[5] = (byte) 0x4E; // NIBBLES
		tab[6] = (byte) 0x02;
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

	private byte[] pse_get_power_limit(byte echo) {

		tab[0] = (byte) 0x02; // request
		tab[1] = echo;
		tab[2] = (byte) 0x05;
		tab[3] = (byte) 0x0B;
		tab[4] = (byte) 0x01; // first channel
		tab[5] = (byte) 0x4E;
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

	/**
	 * This method does the checksum
	 * 
	 * @param byte[]
	 * @return byte[]
	 */
	private byte[] checksum(byte[] tab) {
		short checksum = 0;
		for (int i = 0; i < tab.length - 2; i++) {
			checksum = (short) (checksum + tab[i]);
			if(tab[i]<0){
				checksum = (short) (checksum + 256);
			}
		}
		tab[13] = (byte) ((checksum >> 8) & 0xff);
		tab[14] = (byte) (checksum & 0xff);
		
		return tab;
	} // end of checksum

	/**
	 * echo++
	 * 
	 * @return
	 */
	private byte get_echo() {
		echo++;
		if (echo > 0x78) {
			echo = 0;
		}
		return echo;
	}

	/**
	 * print buffer content, debug purpose
	 * @param byte[]
	 */
	@SuppressWarnings("unused")
	private void printBuffer(byte[] buf2) {
		System.out.println("[DEBUG] **********************");
		for (int j = 0; j < buf2.length; j++) {
			System.out.println("[DEBUG] buf[" + j + "]=" + (buf2[j]));
		}
		System.out.println("[DEBUG] **********************");

	} // end of printBuffer

} // end of class

// end of code
