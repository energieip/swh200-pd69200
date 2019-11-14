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
			if (DEBUG) {
				System.out.println("Initializing I2C...");
			}
			ids = I2CFactory.getBusIds();
			if (DEBUG) {
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
			if (DEBUG) {
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

	public void pse_set_temporary_matrix(byte[][] matrix) {
		try {
			for (int i = 0; i < matrix.length; i++) {

				tab[0] = (byte) 0x00; // command
				tab[1] = get_echo();
				tab[2] = (byte) 0x05; // channel
				tab[3] = (byte) 0x43; // temporary matrix
				tab[4] = (byte) i; // logical port
				tab[5] = matrix[i][0]; // Physical port A
				tab[6] = matrix[i][1]; // Physical port B
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
				Thread.sleep(10);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void pse_program_global_matrix() {

		try {
			tab[0] = (byte) 0x00; // command
			tab[1] = get_echo();
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

			device.write(tab);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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

	public boolean pse_save_system_settings() {

		// must wait 50ms before using I2C when sending this command
		// response should come after 50ms

		try {
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

	public boolean pse_reset_command() {

		try {
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

	public boolean pse_restore_factory_default() {

		try {
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

	public int pse_get_total_power() {

		byte[] buf = new byte[15]; // input buffer tab
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

			if (DEBUG) {
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

	public byte[] pse_get_physical_port_number_from_active_matrix(byte logical_port) {

		byte[] buf = new byte[15]; // input buffer tab
		boolean first = true;

		try {
			tab[0] = (byte) 0x02; // command
			tab[1] = get_echo();
			tab[2] = (byte) 0x05; // channel
			tab[3] = (byte) 0x44; // channel matrix
			tab[4] = (byte) logical_port; // channel number
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

			for (int i = 0; i < 5; i++) { // 5 retry

				device.write(tab);

				while (true) {
					int res = device.read(buf, 0, 1);
					if (buf[0] != 0) {
						int pos = device.read(buf, 1, 14);
						// System.out.println("go :" + i + " pos:" + pos);
						break;
					}
				}

				if (buf[0] == 0x03) {
					if (!first)
						break;
					else
						first = false;
				}

				Thread.sleep(100);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buf;
	}

	public String pse_get_BT_class_power(int class_type) {

		byte[] buf = new byte[15]; // input buffer tab
		int class_power_value = 0;
		int added_class_power_value = 0;
		int max_added_class_power_value = 0;

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

			if (DEBUG) {
				printBuffer(buf);
			}

			if (buf[0] == 0x03) { // Telemetry
				class_power_value = ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
				added_class_power_value = buf[4];
				max_added_class_power_value = buf[5];
				if (DEBUG) {
					System.out.println("AddedClassPowerValue=" + class_power_value);
					System.out.println("AddedClassPowerValue=" + buf[4]);
					System.out.println("MaxAddedClassPowerValue=" + buf[5]);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.toString(class_power_value) + " " + Integer.toString(added_class_power_value) + " "
				+ Integer.toString(max_added_class_power_value);
	}

	public String pse_get_BT_system_status() {

		byte[] buf = new byte[15]; // input buffer tab
		int cpu_status_err_codes = 0;
		int factory_default = 0;
		int RAM_private_label = 0;
		int NVM_user_byte = 0;
		int active_devices = 0;
		int found_devices_after_boot = 0;

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

			if (DEBUG) {
				printBuffer(buf);
			}

			if (buf[0] == 0x03) { // Telemetry

				cpu_status_err_codes = byteToInt(buf[3]);
				factory_default = byteToInt(buf[4]);
				RAM_private_label = byteToInt(buf[6]);
				NVM_user_byte = byteToInt(buf[7]);
				active_devices = byteToMSB(buf[8]);
				found_devices_after_boot = byteToLSB(buf[8]);

				if (DEBUG) {
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

		return Integer.toString(cpu_status_err_codes) + " " + Integer.toString(factory_default) + " "
				+ Integer.toString(RAM_private_label) + " " + Integer.toString(NVM_user_byte) + " "
				+ Integer.toString(active_devices) + " " + Integer.toString(found_devices_after_boot);
	}

	public byte[] pse_get_BT_port_parameters(int port_num) {

		byte[] buf = new byte[15]; // input buffer tab
		boolean first = true;

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

			for (int i = 0; i < 5; i++) { // 5 retry

				device.write(tab);

				while (true) {
					int res = device.read(buf, 0, 1);
					if (buf[0] != 0) {
						int pos = device.read(buf, 1, 14);
						// System.out.println("go :" + i + " pos:" + pos);
						break;
					}
				}

				if (buf[0] == 0x03) {
					if (!first)
						break;
					else
						first = false;
				}

				Thread.sleep(100);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buf;
	}

	public byte[] pse_get_BT_port_status(int port_num) {

		byte[] buf = new byte[15]; // input buffer tab
		boolean first = true;

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

			if (DEBUG) {
				printBuffer(tab);
			}

			for (int i = 0; i < 5; i++) { // 5 retry

				device.write(tab);

				while (true) {
					int res = device.read(buf, 0, 1);
					if (buf[0] != 0) {
						int pos = device.read(buf, 1, 14);
						// System.out.println("go :" + i + " pos:" + pos);
						break;
					}
				}

				if (buf[0] == 0x03) {
					if (!first)
						break;
					else
						first = false;
				}
				Thread.sleep(100);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buf;
	}

	public int pse_get_software_version() {

		byte[] buf = new byte[15]; // input buffer tab
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

			if (DEBUG) {
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

	public void pse_set_bt_port_parameters(byte portNum, byte portModeCFG1, byte portModeCFG2, byte portOperationMode,
			byte portAddPower, byte portPriority) {

		try {
			tab[0] = (byte) 0x00; // command
			tab[1] = get_echo();
			tab[2] = (byte) 0x05; // channel
			tab[3] = (byte) 0xC0; // BT Port Config 1
			tab[4] = portNum;
			tab[5] = portModeCFG1;
			tab[6] = portModeCFG2;
			tab[7] = portOperationMode;
			tab[8] = portAddPower;
			tab[9] = portPriority;
			tab[10] = (byte) 0x4E;
			tab[11] = (byte) 0x4E;
			tab[12] = (byte) 0x4E;
			tab[13] = (byte) 0x00;
			tab[14] = (byte) 0x00;

			tab = checksum(tab);
			
			System.out.println("pse_set_bt_port_parameters [" + portNum + "] port_mode_CFG1="
					+ String.format("0x%02X",tab[5]) + " port_mode_CFG2=" + String.format("0x%02X",tab[6]) + " port_operation_mode="
					+ String.format("0x%02X",tab[7]) + " port_add_power=" + String.format("0x%02X",tab[8]) + " port_priority=" + String.format("0x%02X",tab[9]));
			
			boolean flash_ok = false;
			for (int i = 0; i < 10; i++) {
				device.write(tab);
				byte [] buffer = pse_get_BT_port_parameters(portNum);
				if(buffer[3]==portModeCFG1){
					flash_ok=true;
					break;
				}else {
					System.out.println("Retry n°"+i);
					Thread.sleep(500);
					if(i==4){
						System.out.println("I2C reinit...");
						this.initi2c();
						Thread.sleep(1000);
					}
				}
			}
			if(!flash_ok){
				System.out.println("Failed updating port "+portNum);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	// LSB is the right part
	public int byteToLSB(byte b) {
		byte result = (byte) (b >> 4);
		return result;
	}

	// MSB is the left part
	public int byteToMSB(byte b) {
		byte result = (byte) ((b & 0xFF) >> 4);
		return byteToInt(result);
	}

	public int byteToInt(byte b) {
		int a = 0;
		a = b;
		if (a < 0) {
			a = a + 256;
		}
		return a;
	}

	private byte[] checksum(byte[] tab) {
		short checksum = 0;
		for (int i = 0; i < tab.length - 2; i++) {
			checksum = (short) (checksum + tab[i]);
			if (tab[i] < 0) {
				checksum = (short) (checksum + 256);
			}
		}
		tab[13] = (byte) ((checksum >> 8) & 0xff);
		tab[14] = (byte) (checksum & 0xff);

		return tab;
	} // end of checksum

	private byte get_echo() {
		echo++;
		if (echo > 0x78) {
			echo = 0;
		}
		return echo;
	}

	@SuppressWarnings("unused")
	public void printBuffer(byte[] buf2) {
		System.out.println("[DEBUG] **********************");
		for (int j = 0; j < buf2.length; j++) {
			System.out.println("[DEBUG] buf[" + j + "]=" + (buf2[j]));
		}
		System.out.println("[DEBUG] **********************");

	} // end of printBuffer

} // end of class

// end of code
