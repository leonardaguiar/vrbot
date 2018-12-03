package br.edu.ifba.vrrobot;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashEstabilizaActivity extends Activity implements SensorEventListener {
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private boolean estabilizado = false;
    private boolean threadActive = false;
    private static final int TIMER_RUNTIME = 5000;
    private ProgressBar mProgressBar;
    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_estabiliza);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.setRequestedOrientation(ActivityInfo.FLAG_ENABLE_VR_MODE);
        //Define a exibição da tela como fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Define fonte
        Typeface digitalfont = Typeface.createFromAsset(getAssets(),"fonts/DS-DIGIB.TTF");
        //Text de exibição dos dados do acelerometro
        textViewX = (TextView) findViewById(R.id.text_view_x);
        textViewY = (TextView) findViewById(R.id.text_view_y);
        textViewZ = (TextView) findViewById(R.id.text_view_z);
        textViewX.setTextColor((Color.parseColor("#87CEFA")));
        textViewY.setTextColor(Color.parseColor("#87CEFA"));
        textViewZ.setTextColor(Color.parseColor("#87CEFA"));
        textViewX.setTypeface(digitalfont);
        textViewY.setTypeface(digitalfont);
        textViewZ.setTypeface(digitalfont);




        //Inicicializa o serviço de sensores
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Inicializar a variavel para captura dos dados do acelerometro
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        onResume();
       // Log.d("Executou","");
        final Thread estabilizaThread = new Thread(){
            public void run(){
                threadActive = true;
                int waited = 0;
                try {
                while(threadActive && (waited < TIMER_RUNTIME) ){
                   // Log.d("Executou","");

                        sleep(200);
                        if (estabilizado) {
                           waited += 200;
                           onProgess(waited);
                        } else {
                            waited = 0;
                            onProgess(waited);
                        }
                    }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        onContinue();
                    }


            }
        };
        estabilizaThread.start();


    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Float x = sensorEvent.values[0];
        Float y = sensorEvent.values[1];
        Float z = sensorEvent.values[2];
        int x1 = x.intValue();
        int y1 = y.intValue();
        int z1 = z.intValue();
        //Controla a TextView para informar se o eixo x esta estabilizado
        //Eixo x etabilizado
        if(x.intValue()==9){
            x1= ((x.intValue()*100)/9)/10;
            textViewX.setTextColor((Color.parseColor("#000FFF")));
            textViewX.setText("Pos X: " + x1 + " Estabilizado");
        }else if (x.intValue()>=7 && x.intValue()<=9) {
            x1= ((x.intValue()*100)/9)/10;
            textViewX.setTextColor((Color.parseColor("#00FF00")));
            textViewX.setText("Pos X: " + x1 + " Estabilize");
        }
        else if (x.intValue()<7) {
            x1= ((x.intValue()*100)/9)/10;
            textViewX.setTextColor((Color.parseColor("#F70000")));
            textViewX.setText("Pos X: " + x1 + " Estabilize");
        }
        //Controla a TextView para informar se o eixo y esta estabilizado
        //Eixo y etabilizado
        if(y.intValue()==0){
            y1= (100-(y.intValue()*100)/9)/10;
            textViewY.setTextColor((Color.parseColor("#000FFF")));
            textViewY.setText("Pos Y: " + y1 + " Estabilizado");
        }else if (y.intValue()<=4 && y.intValue()>0) {
            y1= (100-(y.intValue()*100)/9)/10;
            textViewY.setTextColor((Color.parseColor("#00FF00")));
            textViewY.setText("Pos Y: " + y1 + " Estabilize");
        } else if (y.intValue()<0 && y.intValue()>=-4) {
            y1= (((y.intValue()*100)/9))/10;
            textViewY.setTextColor((Color.parseColor("#00FF00")));
            textViewY.setText("Pos Y: " + y1 + " Estabilize");
        }else if (y.intValue()>4) {
            y1= (100-(y.intValue()*100)/9)/10;
            textViewY.setTextColor((Color.parseColor("#F70000")));
            textViewY.setText("Pos Y: " + y1 + " Estabilize");
        }else if (y.intValue()<-4) {
            y1= (((y.intValue()*100)/9))/10;
            textViewY.setTextColor((Color.parseColor("#F70000")));
            textViewY.setText("Pos Y: " + y1 + " Estabilize");
        }
        //Controla a TextView para informar se o eixo z esta estabilizado
        //Eixo z etabilizado
        if(z.intValue()==0){
            z1= (100-(z.intValue()*100)/9)/10;
            textViewZ.setTextColor((Color.parseColor("#000FFF")));
            textViewZ.setText("Pos Z: " + z1 + " Estabilizado");
        }else if (z.intValue()<=4 && z.intValue()>0) {
            z1= (100-(z.intValue()*100)/9)/10;
            textViewZ.setTextColor((Color.parseColor("#00FF00")));
            textViewZ.setText("Pos Z: " + z1 + " Estabilize");
        } else if (z.intValue()<0 && z.intValue()>=-4) {
            z1= ((z.intValue()*100)/9)/10;
            textViewZ.setTextColor((Color.parseColor("#00FF00")));
            textViewZ.setText("Pos Z: " + z1 + " Estabilize");
        }else if (z.intValue()>4) {
            z1= (100-(z.intValue()*100)/9)/10;
            textViewZ.setTextColor((Color.parseColor("#F70000")));
            textViewZ.setText("Pos Z: " + z1 + " Estabilize");
        }else if (z.intValue()<-4) {
            z1= ((z.intValue()*100)/9)/10;
            textViewZ.setTextColor((Color.parseColor("#F70000")));
            textViewZ.setText("Pos Z: " + z1 + " Estabilize");
        }
        //Se os eixos x,y,z estiverem alinhados define true para iniciar a barra de carregamento
        if (x.intValue() == 9 && y.intValue() == 0 && z.intValue() == 0) {
            this.estabilizado = true;
        }else
            estabilizado = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onProgess(final int time){
        final int progress = mProgressBar.getMax() * time / TIMER_RUNTIME;
        mProgressBar.setProgress(progress);
    }
    public void onContinue(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
