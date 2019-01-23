package com.energieip.i2c.test;

public class TestGlobalPower {
	
	private byte[] buf = new byte[15]; // input buffer tab

	public static void main(String[] args) {


		new TestGlobalPower();

	}
	
	public TestGlobalPower() {
			
				buf[0]=3;
				buf[1]=9;
				buf[2]=0;
				buf[3]=2;
				buf[4]=0;
				buf[5]=2;
				buf[6]=6;
				buf[7]=74;
				buf[8]=6;
				buf[9]=76;
				buf[10]=15;
				buf[11]=78;
				buf[12]=78;
				buf[13]=1;
				buf[14]=93;

				int power = ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
				
				System.out.println(power);
				
		
	}

}
