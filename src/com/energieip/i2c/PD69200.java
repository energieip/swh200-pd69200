package com.energieip.i2c;

import java.io.IOException;
import java.util.Arrays;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class PD69200 implements Runnable {

	// I2C generic
	private I2CBus bus;
	private I2CBus i2c;
	private I2CDevice device;

	// PD69200 I2C address
	private static final int PD69200_ADDR = 0x3C;

	// message tab
	private byte[] tab = new byte[15]; // output buffer tab
	private byte echo = 0;
	private byte[] buf = new byte[15]; // input buffer tab

	// thread
	Thread readThread;
	private int SCAN_RATE = 1000; // in milliseconds

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

		try {

			initi2c();
			initPSE();

			readThread = new Thread(this);
			readThread.start();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end of constructor

	/**
	 * initialize PSE
	 * 
	 * @throws InterruptedException
	 */
	private void initPSE() throws InterruptedException {
		System.out.println("Initializing PSE...");

		// enable 4 pairs and PoH
		pse_set_4_pair_ports_parameters(echo);
		Thread.sleep(100); // wait 100 ms

		// set power limit to 62W (0XFF 0xFE)
		pse_set_4_pair_power_limit(get_echo());
		Thread.sleep(100); // wait 100 ms
	}

	/**
	 * initialize I2C
	 */
	private void initi2c() {
		int[] ids;
		try {
			System.out.println("Initializing I2C...");
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

	private byte[] pse_set_power_limit_for_channels(byte echo, byte channel) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0x0B; // Supply
		tab[4] = channel; // CH number or 0x80 for all
		tab[5] = (byte) 0xFF; // 60W => EA 65W => FF 90W => NOT SUPPORTED (3
								// bytes needed)
		tab[6] = (byte) 0xFE; // 60W => 60 65W => FE 90W => NOT SUPPORTED (3
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

	private byte[] pse_set_4_pair_ports_parameters(byte echo) {

		tab[0] = (byte) 0x00; // command
		tab[1] = echo;
		tab[2] = (byte) 0x05; // channel
		tab[3] = (byte) 0xAF; // PortFullInit4Pair
		tab[4] = (byte) 0x80; // CH number or 0x80 for all
		tab[5] = (byte) 0x4E; // default (2 nibbles)
		tab[6] = (byte) 0xFF; // PPL4Pair
		tab[7] = (byte) 0xFE; // PPL4Pair
		tab[8] = (byte) 0x4E; // Priority
		tab[9] = (byte) 0x02; // PoH
		tab[10] = (byte) 0x00; // Sum_as_TPPL, Energy management
		tab[11] = (byte) 0x02; // PortPM2
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

	/**
	 * This method does the checksum
	 * 
	 * @param tab
	 * @return
	 */
	private byte[] checksum(byte[] tab) {
		int checksum = 0;
		for (int i = 0; i < tab.length - 2; i++) {
			//System.out.println("i=" + i + " checksum=" + checksum);
			checksum = checksum + tab[i];
		}

		short checksum_short = (short) checksum;
		//System.out.println("checksum short=" + checksum_short);

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
		if (echo > 0xFE) {
			echo = 0;
		}
		return echo;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				System.out.println("Requesting total power...");
				buf = new byte[15];
				tab = pse_get_total_power(get_echo());
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
				printBuffer(buf);
				extractData(buf);
				Thread.sleep(SCAN_RATE);
			} catch (InterruptedException e) {
				// Nothing to do
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} // end of thread

	
	private void extractData(byte[] buffer) {
		if(buffer[0]==0x03){ // Telemetry
			int power = ((buffer[2] & 0xff) << 8) | (buffer[3] & 0xff);	
			System.out.println("power (W)=" + power);
		}
		
		
	}

	private void printBuffer(byte[] buf2) {
		System.out.println("**********************");
		for (int j = 0; j < buf.length; j++) {
			System.out.println("buf["+j+"]=" + (buf[j]));
		}
		System.out.println("**********************");
		
	} // end of printBuffer

} // end of class

// end of code
