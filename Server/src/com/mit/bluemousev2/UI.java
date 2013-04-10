package com.mit.bluemousev2;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

class UI extends JFrame
{
	JButton btn1;
	JLabel lb1,lb2,lb3;
	BluetoothMob bm;
	boolean running = false;
	UI()
	{
		super("BlueMouse");
		int w = 300;
		setSize(w,w);
		setLayout(null);
		
		Container c = getContentPane();
		bm = new BluetoothMob();
		String computername = "";
		try{
			computername=InetAddress.getLocalHost().getHostName();
		}catch( Exception e)
		{ }
				
		lb1 = new JLabel("BlueMouse");
		lb1.setBounds((w-120)/2,(w-20)/2 - 60,120,20);
		
		lb2 = new JLabel("Server Started");
		lb2.setBounds(10,(w-30)/2,120,30);
		
		lb3 = new JLabel("PC Name: " + computername);
		lb3.setBounds(10,170,250,30);
		
		btn1 = new JButton("About");
		btn1.setBounds(220,220,70,30);
		
		btn1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(null, "<html>Made By:<br>Divyanshu Verma<br>Yugansh Aggarwal</html>", "About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		Font f = lb1.getFont();
		Float s = f.getSize2D();
		s += 8.0f;
		lb1.setFont(f.deriveFont(s));
		
		c.add(lb1);
		c.add(lb2);
		c.add(btn1);
		c.add(lb3);
		
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String arg[])
	{
		new UI();
	}
}