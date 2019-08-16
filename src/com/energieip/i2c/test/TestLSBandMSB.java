package com.energieip.i2c.test;

public class TestLSBandMSB {

	public static void main(String[] args) {
		new TestLSBandMSB();

	}
	
	public TestLSBandMSB() {
		
		byte b = (byte) 0x66;
		
		System.out.println(b);
		System.out.println(byteToInt(b));
		System.out.println(byteToMSB(b));
		System.out.println(byteToLSB(b));
		
	}
	
	// LSB is the right part
	private int byteToLSB(byte b) {
		byte result = (byte) (b >>4);
		return byteToInt(result);
	}
	
	// MSB is the left part
	private int byteToMSB(byte b) {
		byte result = (byte) ((b & 0xFF) >> 4);
		return byteToInt(result);
	}

	private int byteToInt(byte b) {
		int a=0;
		a = b;
		if(a<0){
			a=a+256;
		}
		return a;
	}


}
