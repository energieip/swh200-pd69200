package com.energieip.i2c.test;

public class TestGlobalPower {
	
	private byte[] buf = new byte[15]; // input buffer tab

	public static void main(String[] args) {


		new TestGlobalPower();

	}
	
	public TestGlobalPower() {
			
				buf[0]=3;
				buf[1]=37;
				buf[2]=0;
				buf[3]=0;
				buf[4]=0;
				buf[5]=0;
				buf[6]=0;
				buf[7]=0;
				buf[8]=6;
				buf[9]=76;
				buf[10]=0;
				buf[11]=78;
				buf[12]=78;
				buf[13]=1;
				buf[14]=22;
				
				
				
		
	}

}
