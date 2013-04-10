package com.mit.bluemousev2;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothWorker
{
		
	private static UUID generalUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		
	public static boolean connect(BluetoothSocket sock)
	{
		try
		{
			sock.connect();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			sock = null;
			return false;
		}
	}
	
	public static BluetoothSocket getBluetoothSocket(String pcname)
	{
	    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    mBluetoothAdapter.cancelDiscovery();
	    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
	    // If there are paired devices
	    if (pairedDevices.size() > 0) {
	        // Loop through paired devices
	        for (BluetoothDevice device : pairedDevices) {
	            // Add the name and address to an array adapter to show in a ListView
	            if(device.getName().equalsIgnoreCase((pcname))){
	                try {
	                    return device.createRfcommSocketToServiceRecord(generalUuid);
	                } catch (IOException e) {
	                    return null;
	                }
	            }
	        }
	    }
	    return null;
	}
	
	public static void closeSocket(BluetoothSocket sock)
	{
		try
		{
			sock.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		sock = null;
	}
	
	public static boolean send(String msg, BluetoothSocket sock)
	{
	    try
	    {
			sock.getOutputStream().write(msg.getBytes());
			sock.getOutputStream().flush();
			return true;
		}
	    catch (IOException e)
	    {
			e.printStackTrace();
			return false;
		}
	}
}