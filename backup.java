package com.example.robotcontroller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RobotControl extends Activity implements SensorEventListener{
	
	float x, y, sensorX, sensorY;
	String readingX, readingY, serverIPAddress;
	TextView ComDisplay;
	TextView Top, Bottom, Left, Right;
	boolean connected = false;
	Button connect, disconnect;
	EditText IP;
	Thread cThread;
	String com;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0){
			Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
		x = y = sensorX = sensorY = 0;
		ComDisplay = (TextView)this.findViewById(R.id.tvCommand);
		Top = (TextView)this.findViewById(R.id.tvTop);
		Bottom = (TextView)this.findViewById(R.id.tvBottom);
		Left = (TextView)this.findViewById(R.id.tvLeft);
		Right = (TextView)this.findViewById(R.id.tvRight);
		IP = (EditText) this.findViewById(R.id.etIPADD);
		connect = (Button) this.findViewById(R.id.bConnectToServer);
		disconnect = (Button) this.findViewById(R.id.bDisconnect);
		disconnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cThread.destroy();
			}
		});
		connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				serverIPAddress = IP.getText().toString();
				connect.setText(serverIPAddress);
				cThread = new Thread(new ClientThread());
				cThread.start();
				
			}
		});
	}

	public class ClientThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
	            InetAddress serverAddr = InetAddress.getByName("192.168.1.2");
	            Log.d("ClientActivity", "C: Connecting...");
	            Socket socket = new Socket(serverAddr, 27015);
	            connected = true;
	            while (connected) {
	                try {
	                    Log.d("ClientActivity", "C: Sending command.");
	                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	                        // WHERE YOU ISSUE THE COMMANDS
	                        out.println(com);
	                        Log.d("ClientActivity", "C: Sent.");
	                } catch (Exception e) {
	                    Log.e("ClientActivity", "S: Error", e);
	                }
	            }
	            socket.close();
	            Log.d("ClientActivity", "C: Closed.");
	        } catch (Exception e) {
	            Log.e("ClientActivity", "C: Error", e);
	            connected = false;
	        }
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent e) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(20);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sensorX = e.values[0];
		sensorY = e.values[1];
		if((sensorY<-2.5)
			&&(sensorX>-2.5)
			&&(sensorX<2.5)){
			ComDisplay.setText("Forward");
			com = "W";
			Top.setTextColor(Color.rgb(255, 0, 0));
			
		}
		else if((sensorY>3.5)
				&&(sensorX>-2.5)
				&&(sensorX<2.5)){
			ComDisplay.setText("Backward");
			com = "S";
			Bottom.setTextColor(Color.rgb(255, 0, 0));
			
		}
		else if((sensorX<2.5)
				&&(sensorX>-2.5)
				&&(sensorY<2.5)
				&&(sensorY)>-2.5){
			ComDisplay.setText("Stop");
			com = "H";
			revertToNormal();
		}
		else if(sensorX<-2.5){
			ComDisplay.setText("Right");
			com = "D";
			Right.setTextColor(Color.rgb(255, 0, 0));
		}
		else if(sensorX>2.5){
			ComDisplay.setText("Left");
			com = "A";
			Left.setTextColor(Color.rgb(255, 0, 0));
		}
		//readingX = Float.toString(sensorX);
		//readingY = Float.toString(sensorY);
		//ComDisplay.setText(readingX+","+readingY);
	}



	private void revertToNormal() {
		// TODO Auto-generated method stub
		Top.setTextColor(Color.rgb(0, 0, 0));
		Bottom.setTextColor(Color.rgb(0, 0, 0));
		Left.setTextColor(Color.rgb(0, 0, 0));
		Right.setTextColor(Color.rgb(0, 0, 0));
	}

	

}

