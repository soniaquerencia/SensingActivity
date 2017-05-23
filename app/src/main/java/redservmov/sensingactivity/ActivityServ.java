package redservmov.sensingactivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ActivityServ extends AppCompatActivity implements SensorEventListener {
    Sensor mLight;
    SensorManager managerSensor;
    TextView txtValue;


    private String server_ip;
    private int server_port;
    private String server_protocol;
    private Socket socket;

    private static DatagramSocket datagramSocket;
    public InetAddress server_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv);

        new Thread(new ClientThread("")).start();

        server_ip="10.0.2.2";
        server_port=5000;
        server_protocol="tcp";


        managerSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = managerSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        managerSensor.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        txtValue = (TextView) findViewById(R.id.txtValue);


    }

    protected void onResume(){
        super.onResume();
        managerSensor.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause(){
        super.onPause();
        managerSensor.unregisterListener(this);
    }



    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {

        txtValue.setText("X: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2]);

        String message =  "X: " + String.valueOf(event.values[0]) + " Y : " + String.valueOf(event.values[1]) + " Z: " + String.valueOf(event.values[2]);

        new Thread(new ClientThread(message)).start();
    }


    class ClientThread implements Runnable {
        String mess;

        ClientThread (String message){
            mess = message;
        }

        public void run() {

            try {
                server_addr = InetAddress.getByName(server_ip);

                if(server_protocol.equalsIgnoreCase("tcp")) {
                    socket = new Socket(server_addr, server_port);
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println(mess);
                    socket.close();
                }else{
                    DatagramPacket p;
                    p = new DatagramPacket(mess.getBytes(), mess.length(), server_addr , server_port);
                    datagramSocket= new DatagramSocket();
                    datagramSocket.send(p);
                    datagramSocket.close();
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onClickStop (View view){


        Intent i = new Intent(this, SensingActivity.class );


        startActivity(i);
        finish();
    }






}
