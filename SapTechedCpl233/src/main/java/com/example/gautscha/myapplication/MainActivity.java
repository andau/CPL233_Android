package com.example.gautscha.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    double xValue, yValue, zValue;
    double startTime = 0;
    ArrayList<SensorData> sensorDataList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Prototype functionality - allows network functionality on ui thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


        Button b = (Button) findViewById(R.id.button);
        b.setText("start");
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");
                } else {
                    startTime = (int)System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("stop");
                }

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xValue = event.values[0];
            yValue = event.values[1];
            zValue = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            collectData();
            timerHandler.postDelayed(this, 300);
        }
    };

    private void collectData()
    {
        SensorData sensorData = new SensorData(startTime, xValue, yValue, zValue);
        sendData(sensorData);
    }


    private void sendData(SensorData sensorData) {
        String USERNAME = "iotmmsp1937732348trial";
        String DEVICE_ID = "60f9750e-9ce7-43c3-b747-53b9e8dc5241";
        String MESSAGE_TYPE = "b7b6b10ca55173358ecd";
        String BEARER = "516cef7f3fdad1a2fccac88c6ccc314f";

        try {
            URL url = new URL(String.format("https://%s.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/%s",
                    USERNAME, DEVICE_ID));
            String request = String.format(Locale.US, "{\"messageType\":\"%1$s\",\"messages\":" +
                            "[{\"milliseconds\":%2$.0f,\"xValue\":%3$.3f,\"yValue\":%4$.3f,\"zValue\":%5$.3f}]}",
                    MESSAGE_TYPE, (double) (System.currentTimeMillis() - startTime)/100, xValue, yValue, zValue);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("Authorization", "Bearer " + BEARER);
            connection.setInstanceFollowRedirects(false);

            byte[] bytes = request.getBytes("UTF_8");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(bytes);


            InputStream inputStream;
            int status = connection.getResponseCode();

            TextView tvXValue = (TextView) findViewById(R.id.tvStatus);
            tvXValue.setText("Responsestatus: " + String.valueOf(status));

        } catch (Exception ex) {
            //error handling
            TextView tvXValue = (TextView) findViewById(R.id.tvStatus);
            tvXValue.setText("ERROR " + ex.getMessage());
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Button b = (Button)findViewById(R.id.button);
        b.setText("start");
    }
}