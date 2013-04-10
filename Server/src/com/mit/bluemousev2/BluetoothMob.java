package com.mit.bluemousev2;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothMob
{
	protected String UUID = new UUID("0000110100001000800000805F9B34FB",false).toString();
    protected int discoveryMode = DiscoveryAgent.GIAC;

    public static InputStream in;
    public static OutputStream out;
    private static boolean ServerRun = false;

    protected int endToken = 255;

	Robot r;
    public BluetoothMob()
	{
		startserver();
		try{
		r = new Robot();
		}catch( AWTException ae)
		{
			ae.printStackTrace();
		}

        try {
            LocalDevice device = LocalDevice.getLocalDevice();
			int dis = device.getDiscoverable();
			if(dis!=DiscoveryAgent.GIAC)
				device.setDiscoverable(DiscoveryAgent.GIAC);

            String url = "btspp://localhost:" + UUID + ";name=PCServerCOMM";

            System.out.println("Create server by uri: " + url);
            StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(url);

            serverLoop(notifier);

        } catch (Throwable e) {
			e.printStackTrace();
        }
	}
	
	public void startserver()
	{
		ServerRun = true;
	}
	
	private void serverLoop(final StreamConnectionNotifier notifier) {
        Thread handler = new Thread() {

            @Override
            public void run() {
                try {
                    while (ServerRun) { // infinite loop to accept connections.

                        System.out.println("Waiting for connection...");
                        handleConnection(notifier.acceptAndOpen());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        };
        handler.start();
    }

    private void handleConnection(StreamConnection conn) throws IOException {
        out = conn.openOutputStream();
        in = conn.openInputStream();
        startReadThread(in);
        System.out.println("Connection found...");
        conn.close();
    }
	
	public void startReadThread(final InputStream in)
	{
		Thread reader = new Thread(){
		
			@Override
			public void run()
			{
				byte s[] = new byte[8];
				System.out.println("Trying to read...");
				try
				{
					while(true && ServerRun)
					{
						int ro = in.read(s);
						StringBuilder sb = new StringBuilder();
						for(int i=0; i<ro; i++)
						{
							System.out.print((char)s[i]);
							if((char)s[i]=='<')
							{
								r.mousePress(InputEvent.BUTTON1_MASK);
								r.mouseRelease(InputEvent.BUTTON1_MASK);
							}
							else if((char)s[i]=='>')
							{
								r.mousePress(InputEvent.BUTTON3_MASK);
								r.mouseRelease(InputEvent.BUTTON3_MASK);
							}
							else if((char)s[i]=='M')
							{
								//start
								Runtime.getRuntime().exec("cmd /k magnify.exe");
							}
							else if((char)s[i]=='m')
							{
								//stop
								Runtime.getRuntime().exec("cmd /k taskkill /IM Magnify.exe");
							}
							else
								sb.append((char)s[i]);
						}
						String str[] = sb.toString().split(",");
						
						Point a = MouseInfo.getPointerInfo().getLocation();
						try{
						r.mouseMove( a.x+Integer.parseInt(str[0]), a.y+Integer.parseInt(str[1]) );
						}catch(Exception e)
						{	}
						
						System.out.println();
						if(ro<0)
							break;
					}
				}
				catch(Throwable e)
				{
					System.out.println(e);
				}
				finally
				{
					if(in != null)
					{
						try
						{
							System.out.println("Closing connection");
							in.close();
						}
						catch(IOException e)
						{
							System.out.println("IOException occured");
						}
					}
				}
			}
		};
		reader.start();
	}
    
    public static void StopServer()
    {
        ServerRun = false;
    }
}
