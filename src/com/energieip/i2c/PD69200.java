package com.energieip.i2c;

import java.io.IOException;
import java.util.Arrays;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class PD69200  {

	// I2C generic
	private I2CBus bus;
	private I2CBus i2c;
	private I2CDevice device;

	// PD69200 I2C address
	private final int PD69200_ADDR = 0x3C;
	private final int NUMBER_OF_LOGICAL_PORTS = 24;
	private int i2c_bus;

	// message tab
	private byte[] tab = new byte[15]; // output buffer tab
	private byte echo = 0;
	private byte[] buf = new byte[15]; // input buffer tab

	// const
	final int POWER_MAX = 6000;
	final int POWER_MIN = 0;

	/**
	 * Default constructor
	 * 
	 * @param i2c_bus
	 */
	public PD69200(int _i2c_bus) {

		i2c_bus = _i2c_bus;

		initi2c();
		// initPSE();

		// readThread = new Thread(this);
		// readThread.start();

	} // end of constructor

	/**
	 * initialize I2C
	 */
	private void initi2c() {
		int[] ids;
		try {
			System.out.println("Initializing I2C...");
			ids = I2CFactory.getBusIds();
			System.out.println("Found follow I2C busses: " + Arrays.toString(ids));
			if (i2c_bus == 0) {
				bus = I2CFactory.getInstance(I2CBus.BUS_0);
			} else if (i2c_bus == 1) {
				bus = I2CFactory.getInstance(I2CBus.BUS_1);
			} else {
				System.err.println("[ERROR], bad I2C bus");
				System.exit(0);
			}
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

	private void resetPSE() throws InterruptedException, IOException {
		System.out.println("[WAIT] resetting PSE...");
		tab = new byte[15]; // output buffer tab
		tab = pse_reset_to_factory_default(get_echo());
		device.write(tab);

		Thread.sleep(100); // wait 100 ms
	}

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

	private byte[] pse_reset_to_factory_default(byte echo) {

		tab[0] = (byte) 0x01; // program
		tab[1] = echo;
		tab[2] = (byte) 0x2D;
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

	private byte[] pse_save_system_settings(byte echo) {

		// must wait 50ms before using I2C when sending this command
		// response should come after 50ms

		tab[0] = (byte) 0x1; // program
		tab[1] = echo;
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

		return tab;
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

	private byte[] pse_get_total_power(byte echo) {

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

		return tab;
	}

	public byte[] pse_get_software_version() {
		
		
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

		return tab;
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

	private byte[] pse_save_settings(byte echo) {

		tab[0] = (byte) 0x01; // command
		tab[1] = echo;
		tab[2] = (byte) 0x06;
		tab[3] = (byte) 0x0F;
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
	 * @param tab
	 * @return
	 */
	private byte[] checksum(byte[] tab) {
		int checksum = 0;
		for (int i = 0; i < tab.length - 2; i++) {
			// System.out.println("i=" + i + " checksum=" + checksum);
			checksum = checksum + tab[i];
		}

		short checksum_short = (short) checksum;
		// System.out.println("checksum short=" + checksum_short);

		tab[14] = (byte) (checksum_short & 0xff);
		tab[13] = (byte) ((checksum_short >> 8) & 0xff);

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



	private void extractData(byte[] buffer) {
		if (buffer[0] == 0x03) { // Telemetry
			int power = ((buffer[2] & 0xff) << 8) | (buffer[3] & 0xff);
			if (power > POWER_MAX || power < POWER_MIN) {
				// do nothing
			} else {
				System.out.println("power= " + power + " W");
			}

		}

	}

	private void printBuffer(byte[] buf2) {
		System.out.println("**********************");
		for (int j = 0; j < buf2.length; j++) {
			System.out.println("buf[" + j + "]=" + (buf2[j]));
		}
		System.out.println("**********************");

	} // end of printBuffer

} // end of class

// end of code
