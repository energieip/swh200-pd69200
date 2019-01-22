package com.energieip.i2c;

import java.io.IOException;
import java.util.Arrays;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.util.Console;

public class I2CRead {

	private static I2CBus bus;
	private static I2CBus i2c;
	private static I2CDevice device;
   
	// PD69200 I2C address
    public static final int PD69200_ADDR = 0x3C;

    

    /**
     * Program Main Entry Point
     *
     * @param args
     * @throws InterruptedException
     * @throws PlatformAlreadyAssignedException
     * @throws IOException
     * @throws UnsupportedBusNumberException
     */
    public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException, IOException, UnsupportedBusNumberException {

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "I2C Read");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // fetch all available busses
        
            int[] ids = I2CFactory.getBusIds();
            console.println("Found follow I2C busses: " + Arrays.toString(ids));


				bus = I2CFactory.getInstance( I2CBus.BUS_1);
				console.println("Working with I2C bus " + bus.getBusNumber());
         

        // get the I2C bus to communicate on
        //I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
		i2c = I2CFactory.getInstance(bus.getBusNumber());

        // create an I2C device for an individual device on the bus that you want to communicate with
        // in this example we will use the default address for the TSL2561 chip which is 0x39.
        device = i2c.getDevice(PD69200_ADDR);
        
        byte[] tab = new byte[15];
        
        // get systel status
        tab[0] = 0x02; // request
        tab[1] = 0x00; // echo
        tab[2] = 0x07;
        tab[3] = 0x3D;
        tab[4] = 0x4E;
        tab[5] = 0x4E;
        tab[6] = 0x4E;
        tab[7] = 0x4E;
        tab[8] = 0x4E;
        tab[9] = 0x4E;
        tab[10] = 0x4E;
        tab[11] = 0x4E;
        tab[12] = 0x4E;
        tab[13] = 0x00;
        tab[14] = 0x00;
        
        tab = checksum(tab);
        
        device.write(tab);
        
        //byte[] buf = new byte[15];
        byte[] buf = new byte[15];
        int i=1;
        while(true){
        	int res = device.read(buf, 0, 1);
        	if(buf[0]!=0){
        		int pos = device.read(buf, 1, 14);
        		//System.out.println("go :" + i + " pos:" + pos);
        		break;
        	}
        	//System.out.println("res=" + res + " buf=" + "buf["+res+"]=" + buf[res] + " buf[0]="+buf[0]);
        } // while
        
        for (int j = 0; j < buf.length; j++) {
			System.out.println("buf["+j+"]=" + (buf[j]));
		}
        
        
        //System.out.println("buf=" + buf + " res=" + res);
        
        /*
        for (int i = 0; i < buf.length; i++) {
        	System.out.println("buf["+i+"]=" + buf[i]);
		}
        */
        
        /*
        // next, lets perform am I2C READ operation to the TSL2561 chip
        // we will read the 'ID' register from the chip to get its part number and silicon revision number
        console.println("... reading ID register from TSL2561");
        int response = device.read(TSL2561_REG_ID);
        console.println("TSL2561 ID = " + String.format("0x%02x", response) + " (should be 0x50)");

        // next we want to start taking light measurements, so we need to power up the sensor
        console.println("... powering up TSL2561");
        device.write(TSL2561_REG_CONTROL, TSL2561_POWER_UP);

        // wait while the chip collects data
        Thread.sleep(500);

        // now we will perform our first I2C READ operation to retrieve raw integration
        // results from DATA_0 and DATA_1 registers
        console.println("... reading DATA registers from TSL2561");
        int data0 = device.read(TSL2561_REG_DATA_0);
        int data1 = device.read(TSL2561_REG_DATA_1);

        // print raw integration results from DATA_0 and DATA_1 registers
        console.println("TSL2561 DATA 0 = " + String.format("0x%02x", data0));
        console.println("TSL2561 DATA 1 = " + String.format("0x%02x", data1));

        // before we exit, lets not forget to power down light sensor
        console.println("... powering down TSL2561");
        device.write(TSL2561_REG_CONTROL, TSL2561_POWER_DOWN);
        */
    }



	private static byte[] checksum(byte[] tab) {
		
		int checksum = 0;
		
		for (int i = 0; i < tab.length-2; i++) {
			
				System.out.println("i=" + i + " checksum=" + checksum);
				checksum = checksum + tab[i];
		}
		
		short checksum_short = (short) checksum;
		
		System.out.println("checksum short=" + checksum_short);
		
		tab[14] = (byte)(checksum_short & 0xff);
		tab[13] = (byte)((checksum_short >> 8) & 0xff);
		
		return tab;
		
	}
}
