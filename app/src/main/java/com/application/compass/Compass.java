package com.application.compass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener
{

    ImageView img_compass;
    TextView text_view;
    int number;
    private SensorManager mSensorManager;
    private Sensor RotationV;
    private Sensor Accelometer;
    private Sensor Magnetometer;
    float[] rMat=new float[9];
    float[] orientation= new float[9];
    private float[] LastAccelemeter = new float[3];
    private float[] LastMagnetometer = new float[3];
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean LastAccelerometerSet = false;
    private boolean LastMagnetometerSet = false;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  //reference for sensor manager
        img_compass = (ImageView) findViewById(R.id.img_compass);  //reference for image compass
        text_view = (TextView) findViewById(R.id.text_view);  ////reference for text view

        start();

    }

    @Override
    public void onSensorChanged(SensorEvent event)   //Using the sensors phone have it will show the value
    {
        if(event.sensor.getType()== Sensor.TYPE_ROTATION_VECTOR)  //if phone have gyroscope
        {
            SensorManager.getRotationMatrixFromVector(rMat,event.values);
            number = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0]+360)%360);
        }
        if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER)  //if phone have accelerometer
        {
            System.arraycopy(event.values,0, LastAccelemeter, 0,event.values.length);
            LastAccelerometerSet = true;
        }
        else if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD)  //if phone have magnetometer
        {
            System.arraycopy(event.values,0, LastMagnetometer, 0,event.values.length);
            LastMagnetometerSet = true;
        }
        if(LastMagnetometerSet && LastAccelerometerSet)  //if phone have both accelerometer & magnetometer
        {
            SensorManager.getRotationMatrix(rMat,null, LastAccelemeter, LastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            number = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0]+360)%360);
        }
        number =Math.round(number);
        img_compass.setRotation(-number);

        String where = "NO";

        if(number >=350 || number <= 10)
            where = "N";
        if(number <350 && number > 280)
            where = "NW";
        if(number <=280 && number > 260)
            where = "W";
        if(number <=260 && number > 190)
            where = "SW";
        if(number <=190 && number > 170)
            where = "S";
        if(number <=170 && number > 100)
            where = "SE";
        if(number <=100 && number > 80)
            where = "E";
        if(number <=80 && number > 10)
            where = "NE";
        text_view.setText(number +"°" +where);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void start(){ //check if the phone have gyroscope, magnetometer and accelerometer
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)== null)
        {

            if(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)== null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)== null)
            {
                noSensorAlert();
            }

            else
                {
                Accelometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  //Use these two sensor for compass
                Magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                haveSensor = mSensorManager. registerListener(this, Accelometer,SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager. registerListener(this, Magnetometer,SensorManager.SENSOR_DELAY_UI);
            }
        }
        else
            {
            RotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, RotationV,SensorManager.SENSOR_DELAY_UI);
        }

    }

    public void noSensorAlert(){  //This phone have no sensor for compass

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support this compass.")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                });


    }

    public void stop(){  //when the app is stopped
        if(haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this, Accelometer);
            mSensorManager.unregisterListener(this, Magnetometer);
        }
        else if(haveSensor){
            mSensorManager.unregisterListener(this, RotationV);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        start();
    }

}


