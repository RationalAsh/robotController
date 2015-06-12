package com.example.robotcontroller;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RobotControl extends Activity implements SensorEventListener{
	
	float x, y, sensorX, sensorY;
	String readingX, readingY, serverIPAddress;
	TextView ComDisplay;
	TextView Top, Bottom, Left, Right;
	Button connect, disconnect;
	EditText IP;
	Thread cThread;
	volatile String com;
	Socket socket;
	DataOutputStream os = null;
	DataInputStream is = null;
	SensorManager sm;
	Sensor s;
	boolean asyncTaskDone = false;
	ToggleButton transmitTog;
	PrintWriter out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0){
			s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
		x = y = sensorX = sensorY = 0;
		ComDisplay = (TextView)this.findViewById(R.id.tvCommand);
		Top = (TextView)this.findViewById(R.id.tvTop);
		Bottom = (TextView)this.findViewById(R.id.tvBottom);
		Left = (TextView)this.findViewById(R.id.tvLeft);
		Right = (TextView)this.findViewById(R.id.tvRight);
		IP = (EditText) this.findViewById(R.id.etIPADD);
		transmitTog = (ToggleButton) this.findViewById(R.id.tbTransmit);
		disconnect = (Button) this.findViewById(R.id.bDisconnect);
		disconnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//disconnectServer();
				serverIPAddress = IP.getText().toString();
				new setupNetwork().execute(serverIPAddress, "Android App Checking in!");
			}
		});
		
		
	}
	
	
	
	public void setUpNetwork(String s, String IP){
		try {
            InetAddress serverAddr = InetAddress.getByName(IP);
            Log.d("ClientActivity", "C: Connecting...");
            socket = new Socket(serverAddr, 27015);
            {
                try {
                    Log.d("ClientActivity", "C: Sending command.");
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        // WHERE YOU ISSUE THE COMMANDS
                        out.println(s);
                        Log.d("ClientActivity", "C: Sent.");
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
            }
            //socket.close();
            Log.d("ClientActivity", "C: Closed.");
        } catch (Exception e) {
            Log.e("ClientActivity", "C: Error", e);
        }
	}
	
	private class sendDataTask extends AsyncTask<String, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			asyncTaskDone = false;
		}

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Log.d("AsyncTask", "IP is: "+arg0[0]);
			Log.d("AsyncTask", "SentChar is: "+ arg0[1]);
			//setupConnection(arg0[0]);
			//sendInfo(arg0[1]);
			out.println(arg0[1]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			asyncTaskDone = true;
		}
		
		
		
	}
	
private class setupNetwork extends AsyncTask<String, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			asyncTaskDone = false;
		}

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Log.d("AsyncTask", "IP is: "+arg0[0]);
			Log.d("AsyncTask", "SentChar is: "+ arg0[1]);
			//setupConnection(arg0[0]);
			//sendInfo(arg0[1]);
			setUpNetwork(arg0[1], arg0[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			asyncTaskDone = true;
		}
		
		
		
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sm.unregisterListener(this);
	}




	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
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
		
		if(transmitTog.isChecked()){
			if(asyncTaskDone){
				new sendDataTask().execute(serverIPAddress, com);
			}
			
		}
		
		//readingX = Float.toString(sensorX);
		//readingY = Float.toString(sensorY);
		//ComDisplay.setText(readingX+","+readingY);
	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void revertToNormal() {
		// TODO Auto-generated method stub
		Top.setTextColor(Color.rgb(0, 0, 0));
		Bottom.setTextColor(Color.rgb(0, 0, 0));
		Left.setTextColor(Color.rgb(0, 0, 0));
		Right.setTextColor(Color.rgb(0, 0, 0));
	}

	

}
