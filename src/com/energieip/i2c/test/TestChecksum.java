package com.energieip.i2c.test;

public class TestChecksum {
	
	byte[] tab = new byte[15]; // output buffer tab

	public static void main(String[] args) {
		
		new TestChecksum();
	}
	
	public TestChecksum() {
		
		//test();
		
		tab[0] = 82; // request
		tab[1] = 2;
		tab[2] = -1;
		tab[3] = -1;
		tab[4] = -1; // first channel
		tab[5] = -1;
		tab[6] = 78;
		tab[7] = 78;
		tab[8] = 78;
		tab[9] = 78;
		tab[10] = 78;
		tab[11] = 78;
		tab[12] = 78;
		tab[13] = 0;
		tab[14] = 0;

		
		/*
		tab[0] = 3; // request
		tab[1] = 1;
		tab[2] = 0;
		tab[3] = 78;
		tab[4] = 24; // first channel
		tab[5] = 1;
		tab[6] = 89;
		tab[7] = 0;
		tab[8] = 21;
		tab[9] = 3;
		tab[10] = -24;
		tab[11] = 78;
		tab[12] = 78;
		tab[13] = 0;
		tab[14] = 0;
		*/
		
		
		/*
		tab[0] = (byte) 0x82; // request
		tab[1] = (byte) 0x02;
		tab[2] = (byte) 0xff;
		tab[3] = (byte) 0xff;
		tab[4] = (byte) 0xff; // first channel
		tab[5] = (byte) 0xff;
		tab[6] = (byte) 0x78;
		tab[7] = (byte) 0x78;
		tab[8] = (byte) 0x78;
		tab[9] = (byte) 0x78;
		tab[10] = (byte) 0x78;
		tab[11] = (byte) 0x78;
		tab[12] = (byte) 0x78;
		tab[13] = 0;
		tab[14] = 0;
		*/
		
		tab = checksum(tab);
		
		printBuffer(tab);		

	}

	
	/**
	 * This method does the checksum
	 * 
	 * @param tab
	 * @return
	 */
	private byte[] checksum(byte[] tab) {
		short checksum = 0;
		for (int i = 0; i < tab.length - 2; i++) {
			checksum = (short) (checksum + tab[i]);
			if(tab[i]<0){
				System.out.println("negative value, add 256");
				checksum = (short) (checksum + 256);
			}
			System.out.println("i=" + i + " checksum="+checksum);
		}

		System.out.println("checksum=" + checksum);
		
		tab[13] = (byte) ((checksum >> 8) & 0xff);
		tab[14] = (byte) (checksum & 0xff);
		
		
		return tab;
	} // end of checksum
	
	@SuppressWarnings("unused")
	private void printBuffer(byte[] buf2) {
		System.out.println("[DEBUG] **********************");
		for (int j = 0; j < buf2.length; j++) {
			System.out.println("[DEBUG] buf[" + j + "]=" + (buf2[j]));
		}
		System.out.println("[DEBUG] **********************");

	} // end of printBuffer
	
	
	private void test() {
		
		byte i = 0x00;
		
		for (int j = 0; j < 255; j++) {
			System.out.println("[" + j + "] " + i);
			i++;
		}	
		
	}



}
