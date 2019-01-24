package com.energieip.i2c.test;

import java.util.function.ToDoubleBiFunction;

public class TestHex {

	public static void main(String[] args) {
		
		/*
		int a=82;
		int b=0;
		int c=-1;
		int d=-50;
		int e=-122;
		int f=-16;
		
		System.out.println(Integer.valueOf(String.valueOf(a), 16));
		System.out.println(Integer.valueOf(String.valueOf(b), 16));
		System.out.println(Integer.valueOf(String.valueOf(c), 16));
		System.out.println(Integer.valueOf(String.valueOf(d), 16));
		System.out.println(Integer.valueOf(String.valueOf(e), 16));
		System.out.println(Integer.valueOf(String.valueOf(f), 16));
		
		System.out.println("\n");
		
	System.out.println(Integer.toHexString(a));
	System.out.println(Integer.toHexString(b));
	System.out.println(Integer.toHexString(c));
	
	System.out.println("\n");
	
	System.out.println(Integer.toHexString(toByte(a)));
	System.out.println(toByte(b));
	System.out.println(Integer.toHexString(toByte(c)));
	System.out.println(toByte(d));
	System.out.println(toByte(e));
	System.out.println(toByte(f));
	*/
		
		byte i = 0x02;
		
		System.out.println(i*(byte)2+(byte)1);
		
	}
	
	public static int toByte(int number) {
	    int tmp = number & 0xff;
	    return (tmp & 0x80) == 0 ? tmp : tmp - 256;
	}

}
