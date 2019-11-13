package com.energieip.i2c.commands;

import com.energieip.i2c.PD69200;

public class ProgramGlobalMatrix {

	/**
	 * main entry entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			command(args);
		} else {
			System.err.println("Usage: [Command] i2Cbus");
			System.exit(1);
		}
	}

	/**
	 * command
	 * 
	 * @param args
	 */
	private static void command(String[] args) {
	
		byte[][] matrix_3_boards = new byte[48][2];
		matrix_3_boards[0][0] = 0;
		matrix_3_boards[0][1] = 1;
		matrix_3_boards[1][0] = 2;
		matrix_3_boards[1][1] = 3;
		matrix_3_boards[2][0] = 4;
		matrix_3_boards[2][1] = 5;
		matrix_3_boards[3][0] = 6;
		matrix_3_boards[3][1] = 7;
		matrix_3_boards[4][0] = 8;
		matrix_3_boards[4][1] = 9;
		matrix_3_boards[5][0] = 10;
		matrix_3_boards[5][1] = 11;
		matrix_3_boards[6][0] = 12;
		matrix_3_boards[6][1] = 13;
		matrix_3_boards[7][0] = 14;
		matrix_3_boards[7][1] = 15;
		matrix_3_boards[8][0] = 16;
		matrix_3_boards[8][1] = 17;
		matrix_3_boards[9][0] = 18;
		matrix_3_boards[9][1] = 19;
		matrix_3_boards[10][0] = 20;
		matrix_3_boards[10][1] = 21;
		matrix_3_boards[11][0] = 22;
		matrix_3_boards[11][1] = 23;
		matrix_3_boards[12][0] = 24;
		matrix_3_boards[12][1] = 25;
		matrix_3_boards[13][0] = 26;
		matrix_3_boards[13][1] = 27;
		matrix_3_boards[14][0] = 28;
		matrix_3_boards[14][1] = 29;
		matrix_3_boards[15][0] = 30;
		matrix_3_boards[15][1] = 31;
		matrix_3_boards[16][0] = 32;
		matrix_3_boards[16][1] = 33;
		matrix_3_boards[17][0] = 34;
		matrix_3_boards[17][1] = 35;
		matrix_3_boards[18][0] = 36;
		matrix_3_boards[18][1] = 37;
		matrix_3_boards[19][0] = 38;
		matrix_3_boards[19][1] = 39;
		matrix_3_boards[20][0] = 40;
		matrix_3_boards[20][1] = 41;
		matrix_3_boards[21][0] = 42;
		matrix_3_boards[21][1] = 43;
		matrix_3_boards[22][0] = 44;
		matrix_3_boards[22][1] = 45;
		matrix_3_boards[23][0] = 46;
		matrix_3_boards[23][1] = 47;
		matrix_3_boards[24][0] = -1;
		matrix_3_boards[24][1] = -1;
		matrix_3_boards[25][0] = -1;
		matrix_3_boards[25][1] = -1;
		matrix_3_boards[26][0] = -1;
		matrix_3_boards[26][1] = -1;
		matrix_3_boards[27][0] = -1;
		matrix_3_boards[27][1] = -1;
		matrix_3_boards[28][0] = -1;
		matrix_3_boards[28][1] = -1;
		matrix_3_boards[29][0] = -1;
		matrix_3_boards[29][1] = -1;
		matrix_3_boards[30][0] = -1;
		matrix_3_boards[30][1] = -1;
		matrix_3_boards[31][0] = -1;
		matrix_3_boards[31][1] = -1;
		matrix_3_boards[32][0] = -1;
		matrix_3_boards[32][1] = -1;
		matrix_3_boards[33][0] = -1;
		matrix_3_boards[33][1] = -1;
		matrix_3_boards[34][0] = -1;
		matrix_3_boards[34][1] = -1;
		matrix_3_boards[35][0] = -1;
		matrix_3_boards[35][1] = -1;
		matrix_3_boards[36][0] = -1;
		matrix_3_boards[36][1] = -1;
		matrix_3_boards[37][0] = -1;
		matrix_3_boards[37][1] = -1;
		matrix_3_boards[38][0] = -1;
		matrix_3_boards[38][1] = -1;
		matrix_3_boards[39][0] = -1;
		matrix_3_boards[39][1] = -1;
		matrix_3_boards[40][0] = -1;
		matrix_3_boards[40][1] = -1;
		matrix_3_boards[41][0] = -1;
		matrix_3_boards[41][1] = -1;
		matrix_3_boards[42][0] = -1;
		matrix_3_boards[42][1] = -1;
		matrix_3_boards[43][0] = -1;
		matrix_3_boards[43][1] = -1;
		matrix_3_boards[44][0] = -1;
		matrix_3_boards[44][1] = -1;
		matrix_3_boards[45][0] = -1;
		matrix_3_boards[45][1] = -1;
		matrix_3_boards[46][0] = -1;
		matrix_3_boards[46][1] = -1;
		matrix_3_boards[47][0] = -1;
		matrix_3_boards[47][1] = -1;

		
		
		byte[][] matrix_4_boards = new byte[48][2];
		matrix_4_boards[0][0] = 0;
		matrix_4_boards[0][1] = 1;
		matrix_4_boards[1][0] = 2;
		matrix_4_boards[1][1] = 3;
		matrix_4_boards[2][0] = 4;
		matrix_4_boards[2][1] = 5;
		matrix_4_boards[3][0] = 6;
		matrix_4_boards[3][1] = 7;
		matrix_4_boards[4][0] = 8;
		matrix_4_boards[4][1] = 9;
		matrix_4_boards[5][0] = 10;
		matrix_4_boards[5][1] = 11;
		matrix_4_boards[6][0] = 12;
		matrix_4_boards[6][1] = 13;
		matrix_4_boards[7][0] = 14;
		matrix_4_boards[7][1] = 15;
		matrix_4_boards[8][0] = 16;
		matrix_4_boards[8][1] = 17;
		matrix_4_boards[9][0] = 18;
		matrix_4_boards[9][1] = 19;
		matrix_4_boards[10][0] = 20;
		matrix_4_boards[10][1] = 21;
		matrix_4_boards[11][0] = 22;
		matrix_4_boards[11][1] = 23;
		matrix_4_boards[12][0] = 24;
		matrix_4_boards[12][1] = 25;
		matrix_4_boards[13][0] = 26;
		matrix_4_boards[13][1] = 27;
		matrix_4_boards[14][0] = 28;
		matrix_4_boards[14][1] = 29;
		matrix_4_boards[15][0] = 30;
		matrix_4_boards[15][1] = 31;
		matrix_4_boards[16][0] = 32;
		matrix_4_boards[16][1] = 33;
		matrix_4_boards[17][0] = 34;
		matrix_4_boards[17][1] = 35;
		matrix_4_boards[18][0] = 36;
		matrix_4_boards[18][1] = 37;
		matrix_4_boards[19][0] = 38;
		matrix_4_boards[19][1] = 39;
		matrix_4_boards[20][0] = 40;
		matrix_4_boards[20][1] = 41;
		matrix_4_boards[21][0] = 42;
		matrix_4_boards[21][1] = 43;
		matrix_4_boards[22][0] = 44;
		matrix_4_boards[22][1] = 45;
		matrix_4_boards[23][0] = 46;
		matrix_4_boards[23][1] = 47;
		matrix_4_boards[24][0] = 48;
		matrix_4_boards[24][1] = 49;
		matrix_4_boards[25][0] = 50;
		matrix_4_boards[25][1] = 51;
		matrix_4_boards[26][0] = 52;
		matrix_4_boards[26][1] = 53;
		matrix_4_boards[27][0] = 54;
		matrix_4_boards[27][1] = 55;
		matrix_4_boards[28][0] = 56;
		matrix_4_boards[28][1] = 57;
		matrix_4_boards[29][0] = 58;
		matrix_4_boards[29][1] = 59;
		matrix_4_boards[30][0] = 60;
		matrix_4_boards[30][1] = 61;
		matrix_4_boards[31][0] = 62;
		matrix_4_boards[31][1] = 63;
		matrix_4_boards[32][0] = -1;
		matrix_4_boards[32][1] = -1;
		matrix_4_boards[33][0] = -1;
		matrix_4_boards[33][1] = -1;
		matrix_4_boards[34][0] = -1;
		matrix_4_boards[34][1] = -1;
		matrix_4_boards[35][0] = -1;
		matrix_4_boards[35][1] = -1;
		matrix_4_boards[36][0] = -1;
		matrix_4_boards[36][1] = -1;
		matrix_4_boards[37][0] = -1;
		matrix_4_boards[37][1] = -1;
		matrix_4_boards[38][0] = -1;
		matrix_4_boards[38][1] = -1;
		matrix_4_boards[39][0] = -1;
		matrix_4_boards[39][1] = -1;
		matrix_4_boards[40][0] = -1;
		matrix_4_boards[40][1] = -1;
		matrix_4_boards[41][0] = -1;
		matrix_4_boards[41][1] = -1;
		matrix_4_boards[42][0] = -1;
		matrix_4_boards[42][1] = -1;
		matrix_4_boards[43][0] = -1;
		matrix_4_boards[43][1] = -1;
		matrix_4_boards[44][0] = -1;
		matrix_4_boards[44][1] = -1;
		matrix_4_boards[45][0] = -1;
		matrix_4_boards[45][1] = -1;
		matrix_4_boards[46][0] = -1;
		matrix_4_boards[46][1] = -1;
		matrix_4_boards[47][0] = -1;
		matrix_4_boards[47][1] = -1;

	
		byte[][] matrix = new byte[48][2];
		
		try {
			int firstArg = Integer.parseInt(args[0]);
			PD69200 pd69200 = new PD69200(firstArg);
			
			if(firstArg==0){
				System.out.println("Programming matrix in 4 boards config  (32 logical ports)...");
				pd69200.pse_set_temporary_matrix(matrix_4_boards);
				System.out.println("Moving temporary matrix...");
				pd69200.pse_program_global_matrix();
				System.out.println("Wait while reboot is complete...");
				Thread.sleep(5);
				System.out.println("Saving to memory...");
				pd69200.pse_save_system_settings();
				System.out.println("Matrix updated, reading matrix...");			
				for (int i = 0; i < 48; i++) {
					byte[] buf = pd69200.pse_get_physical_port_number_from_active_matrix((byte)i);
					matrix[i][0] = buf[2];
					matrix[i][1] = buf[3];
					System.out.println("["+i+"] " + pd69200.byteToInt(buf[2]) + " " + pd69200.byteToInt(buf[3]));
				}

				
			}else if(firstArg==1){
				System.out.println("Programming matrix in 3 boards config  (24 logical ports)...");
				pd69200.pse_set_temporary_matrix(matrix_4_boards);
				System.out.println("Moving temporary matrix...");
				pd69200.pse_program_global_matrix();
				System.out.println("Wait while reboot is complete...");
				Thread.sleep(5);
				System.out.println("Saving to memory...");
				pd69200.pse_save_system_settings();
				System.out.println("Matrix updated, reading matrix...");			
				for (int i = 0; i < 48; i++) {
					byte[] buf = pd69200.pse_get_physical_port_number_from_active_matrix((byte)i);
					matrix[i][0] = buf[2];
					matrix[i][1] = buf[3];
					System.out.println("["+i+"] " + pd69200.byteToInt(buf[2]) + " " + pd69200.byteToInt(buf[3]));
				}
			}else {
				System.err.println("Error, wrong PSE ID");
			}
			
			
			
			
			System.out.println(pd69200.pse_save_system_settings());
		} catch (NumberFormatException e) {
			System.err.println("Argument" + args[0] + " must be an integer.");
			System.exit(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

} // end of class
