package com.mit.bluemousev2;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BlueMouse extends Activity {
	
	Button connect, left, right;
	EditText pcname;
	BluetoothSocket socket;
	boolean isConnected;
	String text;
	View touchpad;
	int prevX=-1, prevY=-1;
	
	int factor = 0;
	boolean isMagnified = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        isConnected = false;
        
        setFrame1();     
        
    }
    
    public void setFrame1()
    {
    	setContentView(R.layout.activity_blue_mouse);
    	
    	connect = (Button) findViewById(R.id.connect);
        pcname = (EditText) findViewById(R.id.pcname);
        
        connect.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				connectToPC();
			}
		});
    }
    
    public void setFrame2()
    {
    	setContentView(R.layout.layout_2);
    	
    	prevX = -1;
    	prevY = -1;
    	touchpad = (View) findViewById(R.id.touchpad);
    	touchpad.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				if(prevX>0 && prevY>0)
				{
					int x = ((int) event.getX()-prevX);
					int y = ((int) event.getY()-prevY);
					if(x>30 || y>30 || x<-30 || y<-30);
					else
						BluetoothWorker.send( x*(factor+1)+","+y*(factor+1),socket);
				}
				
				prevX = (int) event.getX();
				prevY = (int) event.getY();				
				
				return true;
			}
		});
    	
    	left = (Button) findViewById(R.id.left);
    	right = (Button) findViewById(R.id.right);
    	
    	left.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				BluetoothWorker.send( "<",socket);
			}
		});
    	
    	right.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				BluetoothWorker.send( ">",socket);
				
			}
		});
    	
    	Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
    }
    
    public void connectToPC()
    {    	
    	text = pcname.getText().toString();
		
		if(text.equals(""))
			Toast.makeText(getApplicationContext(), "Enter PC Name to connect!", Toast.LENGTH_SHORT).show();
		else
		{
			socket = BluetoothWorker.getBluetoothSocket(text);
			if(socket==null)
			{
				Toast.makeText(getApplicationContext(), "Cannot find PC!", Toast.LENGTH_SHORT).show();
			}
			else
			{
				if(BluetoothWorker.connect(socket))
				{
					isConnected = true;
					setFrame2();
				}
				else
					Toast.makeText(getApplicationContext(), "Unable to connect to PC!", Toast.LENGTH_SHORT).show();
			}
		}
    }
    
    public void disconnectFromPc()
    {    	
    	BluetoothWorker.closeSocket(socket);
    	socket = null;
    	
    	isConnected = false;
    	setFrame1();
    	pcname.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_blue_mouse, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_connect:
            	connectToPC();
                break;
            case R.id.menu_disconnect:
            	disconnectFromPc();
                break;
            case R.id.speed:
            	factor = (factor+1)%3;
            	item.setTitle("Speed x"+(factor+1));
            	Toast.makeText(getApplicationContext(), "Speed: x"+(factor+1), Toast.LENGTH_SHORT).show();
            	break;
            case R.id.magnify:
            	if(isMagnified)
            	{
                	BluetoothWorker.send( "m",socket);
                	item.setTitle("Magnification: OFF");
            		Toast.makeText(getApplicationContext(), "Magnification OFF", Toast.LENGTH_SHORT).show();
            	}
            	else
            	{
                	BluetoothWorker.send( "M",socket);
                	item.setTitle("Magnification: ON");
            		Toast.makeText(getApplicationContext(), "Magnification ON", Toast.LENGTH_SHORT).show();
            	}
            	isMagnified = !isMagnified;
            	break;
        }
        
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_connect).setVisible(!isConnected);
        menu.findItem(R.id.menu_disconnect).setVisible(isConnected);
        menu.findItem(R.id.speed).setVisible(isConnected);
        menu.findItem(R.id.magnify).setVisible(isConnected);
        return true;
    }
}
