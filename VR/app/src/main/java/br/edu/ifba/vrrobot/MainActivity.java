package br.edu.ifba.vrrobot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import br.edu.ifba.bd.BD;
import br.edu.ifba.modelos.Configuracao;


public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {

    private ProgressDialog mDialog;
    private VideoView videoview;
    private ImageView btnPlayPause;
    private Configuracao configuracao;
    private String videoURL = "http://192.168.1.150:8080/?action=stream";
    private String dataURL = "http://192.168.1.150:5000/";
    private SensorManager mSensorManager;
    private SensorManager mSensorManager2;
    private Sensor mAccelerometer;
    private Sensor mLight;
    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;
    private TextView conexao;
    private TextView textviewTemp;
    private TextView textviewdesctemp;
    private TextView textviewumidade;
    private TextView textviewdescumd;
    private TextView textviewdist;
    private TextView textviewfire;
    private TextView textviewaltura;
    private TextView textviewdescaltitude;
    private TextView textviewlatitude;
    private TextView textviewdesclatitude;
    private TextView textviewlongi;
    private TextView textviewdesclongi;
    private TextView textviewinfo;
    private TextView textviewcout;
    private TextView textviewdescsaturaox;
    private TextView textviewdetectmq2;
    private ImageView img_alert;
    private ImageView img_esquerda;
    private ImageView img_direita;
    private ImageView img_frente;
    private ImageView img_tras;
    private ImageView img_bussola;
    private JSONObject post_comand;
    private JSONObject get_comand;
    private Button btnRotate;
    private int controtate = 0;
    private WebView wb;
    private MjpegView mv;
    private static final boolean DEBUG = false;
    private static final String TAG = "MJPEG";
    private String command = "";
    private String oldcommand = "";
    private int count_estabilizador = 0;
    private long tmpInit = 0;
    private long time_exit = 0;
    AsyncReqHttpConnection asyncdados;
    AsyncComandoHttpConnection asyncmove;
    //Flag sinaliza se é possivle fazer a leitura do aceleroemtro para navegação
    boolean flag_block = false;


    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Remove as restrições
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Define a orientação da tela para landscape (Deitado)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Define a exibição da tela como fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Carrega o Imageview
        //videoview = (VideoView) findViewById(R.id.videoView);

        // mv = (MjpegView) findViewById(R.id.mv);
//        if (mv != null) {
//            mv.setResolution(640, 480);
//        }

        //Carrega dados
        configuracao = new Configuracao();
        BD bd = new BD(this);
        List<Configuracao> lst_config = bd.buscar();
        if (lst_config.size() > 0) {
            this.configuracao.setNome(lst_config.get(0).getNome());
            this.configuracao.setEndereco(lst_config.get(0).getEndereco());
            this.configuracao.setPorta_dados(lst_config.get(0).getPorta_dados());
            this.configuracao.setPorta_video(lst_config.get(0).getPorta_video());
            this.configuracao.setSomente_vr(lst_config.get(0).isSomente_vr());
            this.configuracao.setDelay_req(lst_config.get(0).getDelay_req());
            dataURL = configuracao.getEndereco() + ":" + configuracao.getPorta_dados() + "/";
            videoURL = configuracao.getEndereco() + ":" + configuracao.getPorta_video(); //+"/?action=stream";
        }


        //Inicicializa o serviço de sensores
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager2 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Inicializar a variavel para captura dos dados do acelerometro
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //Determina o tipo de fonte do textview
        Typeface digitalfont = Typeface.createFromAsset(getAssets(), "fonts/DS-DIGIB.TTF");

        //Text de exibição dos dados de temperatura #15a0f6 ou #87CEFA
        textviewTemp = (TextView) findViewById(R.id.text_view_temp);
        textviewTemp.setTextColor(Color.parseColor("#4169E1"));
        textviewTemp.setTypeface(digitalfont);
        textviewdesctemp = (TextView) findViewById(R.id.text_view_desc_temp);
        textviewdesctemp.setTextColor(Color.parseColor("#4169E1"));


        //Text de exibição dos dados de umidade #15a0f6
        textviewumidade = (TextView) findViewById(R.id.text_view_umd);
        textviewumidade.setTextColor(Color.parseColor("#4169E1"));
        textviewumidade.setTypeface(digitalfont);

        textviewdescumd = (TextView) findViewById(R.id.text_view_desc_umd);
        textviewdescumd.setTextColor(Color.parseColor("#4169E1"));


        //Text de exibição dos dados de distancia #15a0f6
        textviewdist = (TextView) findViewById(R.id.text_view_dist);
        textviewdist.setTextColor(Color.parseColor("#4169E1"));
        textviewdist.setTypeface(digitalfont);

        //Text de exibição dos dados de alerta saturação..#15a0f6
        textviewfire = (TextView) findViewById(R.id.text_view_fire);
        textviewfire.setTextColor(Color.parseColor("#4169E1"));
        textviewfire.setTypeface(digitalfont);
        textviewdescsaturaox = (TextView) findViewById(R.id.text_desc_fire);
        textviewdescsaturaox.setTextColor(Color.parseColor("#4169E1"));
        textviewdescsaturaox.setTypeface(digitalfont);
        textviewdetectmq2 = (TextView) findViewById(R.id.text_view_detect_mq2);
        textviewdetectmq2.setTextColor(Color.parseColor("#4169E1"));
        textviewdetectmq2.setTypeface(digitalfont);

        //Text de exibição dos dados da altitude...#15a0f6
        textviewaltura = (TextView) findViewById(R.id.text_view_altura);
        textviewaltura.setTextColor(Color.parseColor("#4169E1"));
        textviewaltura.setTypeface(digitalfont);
        textviewaltura.setVisibility(View.INVISIBLE);
        textviewdescaltitude = (TextView) findViewById(R.id.text_view_desc_alt);
        textviewdescaltitude.setTextColor(Color.parseColor("#4169E1"));
        textviewdescaltitude.setVisibility(View.INVISIBLE);



        //Text de exibição dos dados da latitude...#15a0f6
        textviewlatitude = (TextView) findViewById(R.id.text_view_lat);
        textviewlatitude.setTextColor(Color.parseColor("#4169E1"));
        textviewlatitude.setTypeface(digitalfont);
        textviewlatitude.setVisibility(View.INVISIBLE);
        textviewdesclatitude = (TextView) findViewById(R.id.text_view_desc_lat);
        textviewdesclatitude.setTextColor(Color.parseColor("#4169E1"));
        textviewdesclatitude.setVisibility(View.INVISIBLE);

        //Text de exibição dos dados da latitude...#15a0f6
        textviewlongi = (TextView) findViewById(R.id.text_view_longi);
        textviewlongi.setTextColor(Color.parseColor("#4169E1"));
        textviewlongi.setTypeface(digitalfont);
        textviewlongi.setVisibility(View.INVISIBLE);
        textviewdesclongi = (TextView) findViewById(R.id.text_view_desc_long);
        textviewdesclongi.setTextColor(Color.parseColor("#4169E1"));
        textviewdesclongi.setVisibility(View.INVISIBLE);

        //Text de exibição de informações adicionais
        textviewinfo = (TextView) findViewById(R.id.text_view_info);
        textviewinfo.setTextColor(Color.parseColor("#4169E1"));
        //textviewinfo.setTypeface(digitalfont);
       //Text de exibição do contador de saida
        textviewcout = (TextView) findViewById(R.id.text_view_count);
        textviewcout.setTextColor(Color.parseColor("#4169E1"));
        textviewcout.setTypeface(digitalfont);

        //Imagens para cada comando
        img_direita = (ImageView) findViewById(R.id.imgv_direita);
        img_esquerda = (ImageView) findViewById(R.id.imgv_esquerda);
        img_frente = (ImageView) findViewById(R.id.imgv_frente);
        img_tras = (ImageView) findViewById(R.id.imgv_tras);
        img_frente.setVisibility(ImageView.INVISIBLE);
        img_tras.setVisibility(ImageView.INVISIBLE);
        img_esquerda.setVisibility(ImageView.INVISIBLE);
        img_direita.setVisibility(ImageView.INVISIBLE);

        //Imagem alerta fumaça, fogo...
        img_alert = (ImageView) findViewById(R.id.img_alert);

        //Carrega o botão de conexão
        btnRotate = (Button) findViewById(R.id.button);
        btnRotate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Animation anim = new RotateAnimation(0,180,50,50);
//                anim.setFillAfter(true);
//                anim.setDuration(10000);
//                img_bussola.startAnimation(anim);
//                final RotateAnimation rotateAnim = new RotateAnimation(0.0f, 20,
//                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//
//                rotateAnim.setDuration(0);
//                rotateAnim.setFillAfter(true);
//                img_bussola.startAnimation(rotateAnim);
                textviewTemp.setText(controtate + "°C");
                controtate += 1;

                img_bussola.setRotation(controtate);


            }
        });

        //Recebe informações para alimentar os metadados a cada 1 segundo
        asyncdados = new AsyncReqHttpConnection(videoURL, videoURL + "/?action=stream", dataURL, dataURL + "dados", 0, null, this);
        asyncdados.Delay(this.configuracao.getDelay_req());
        asyncdados.execute();
        //Envia comandos para movimentação do robo
        asyncmove = new AsyncComandoHttpConnection(dataURL,this, "");
        asyncmove.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //Captura o tempo que o App iniciou as principais informações
        this.tmpInit = System.currentTimeMillis();
        JSONObject post_dict = new JSONObject();

        onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        asyncdados.Exit(true);
        asyncmove.Exit(true);
        this.finish();
    }
    @Override
    public void onClick(View view) {
        //new StreamVideo().execute();
        conexao.setText("Conectando");
        //new DoRead().execute(videoURL);
        wb.loadUrl(videoURL);

       /* mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Por favor espere...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        try{
            if(!videoview.isPlaying()) {
                Uri uri = Uri.parse(videoURL);
                videoview.setVideoURI(uri);
                videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        btnPlayPause.setImageResource(R.drawable.ic_play);
                    }
                });
            }else{
                videoview.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play);
            }
        }catch (Exception e){

        }
        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mDialog.dismiss();
                mp.setLooping(true);
                videoview.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager2.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mSensorManager2.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(this.configuracao.isSomente_vr()){
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT && this.configuracao.isSomente_vr()) {
            Float x = sensorEvent.values[0];
            if (x > 12) {
                command = "0";
                textviewinfo.setText(x.toString());
                this.flag_block = true;
                if (time_exit == 0)
                    time_exit = System.currentTimeMillis();
                else if (System.currentTimeMillis() - this.time_exit > 10000) {
                    this.asyncdados.Exit(true);
                    this.finish();
                }

                textviewinfo.setText("Saindo da aplicação...");
                long text_time = System.currentTimeMillis() - this.time_exit;
                text_time = -1*(text_time / 1000 - 10);
                textviewcout.setText(Long.toString(text_time));

            }//reseta o contador para sair da aplicação
            else {
                textviewinfo.setText("");
                textviewcout.setText("");
                this.time_exit = 0;
                this.flag_block = false;
            }
        }
        }
        if(!this.flag_block && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Float x = sensorEvent.values[0];
            Float y = sensorEvent.values[1];
            Float z = sensorEvent.values[2];

            img_frente.setVisibility(ImageView.INVISIBLE);
            img_tras.setVisibility(ImageView.INVISIBLE);
            img_esquerda.setVisibility(ImageView.INVISIBLE);
            img_direita.setVisibility(ImageView.INVISIBLE);
            command = "0";
            post_comand = new JSONObject();
            if ((y < 5 && y > -4) && x > 0) { // O dispositivo esta de cabeça pra baixo
                //Esta indo para frente
                if (z >= 5.0) {
                    textviewinfo.setText("");
                    textviewcout.setText("");
                    this.time_exit = 0;
                    img_frente.setVisibility(ImageView.VISIBLE);
                    img_tras.setVisibility(ImageView.INVISIBLE);

                    command = "1";
                    //Esta indo para direita
                    if (y < -2) {
                        this.time_exit = 0;
                        img_esquerda.setVisibility(ImageView.VISIBLE);
                        img_direita.setVisibility(ImageView.INVISIBLE);
                        command = "3";
                    }
                    //Esta indo para esquerda
                    else if (y > 2) {
                        this.time_exit = 0;
                        img_esquerda.setVisibility(ImageView.INVISIBLE);
                        img_direita.setVisibility(ImageView.VISIBLE);
                        command = "2";
                    }
                } else if (z <= -3.0) {//Esta indo para tras
                    textviewinfo.setText("");
                    textviewcout.setText("");
                    this.time_exit = 0;
                    img_frente.setVisibility(ImageView.INVISIBLE);
                    img_tras.setVisibility(ImageView.VISIBLE);
                    command = "4";
                    if (y < -2) {
                        img_esquerda.setVisibility(ImageView.VISIBLE);
                        img_direita.setVisibility(ImageView.INVISIBLE);

                    }
                    if (y > 2) {
                        img_esquerda.setVisibility(ImageView.INVISIBLE);
                        img_direita.setVisibility(ImageView.VISIBLE);
                      }

                }
                //Esta indo para direita
                else if (y < -2) {
                    textviewinfo.setText("");
                    textviewcout.setText("");
                    this.time_exit = 0;
                    img_esquerda.setVisibility(ImageView.VISIBLE);
                    img_direita.setVisibility(ImageView.INVISIBLE);
                    command = "3";
                }
                //Esta indo para esquerda
                else if (y > 2) {
                    textviewinfo.setText("");
                    textviewcout.setText("");
                    this.time_exit = 0;
                    img_esquerda.setVisibility(ImageView.INVISIBLE);
                    img_direita.setVisibility(ImageView.VISIBLE);
                    command = "2";
                }else {
                    textviewinfo.setText("");
                    textviewcout.setText("");
                    this.time_exit = 0;
                    command = "0";
                }

                //Esta usando a camera, olhando para esquerda e direita
          /*  else {
                try {
                    post_comand.put("Frente", "Nao");
                    post_comand.put("Tras", "Nao");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                img_frente.setVisibility(ImageView.INVISIBLE);
                img_tras.setVisibility(ImageView.INVISIBLE);

                if (y < -1.4) {
                    img_esquerda.setVisibility(ImageView.VISIBLE);
                    img_direita.setVisibility(ImageView.INVISIBLE);
                    try {
                        post_comand.put("Direita", "Nao");
                        post_comand.put("Esquerda", "Sim");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (y > 1.4) {
                    img_esquerda.setVisibility(ImageView.INVISIBLE);
                    img_direita.setVisibility(ImageView.VISIBLE);
                    try {
                        post_comand.put("Direita", "Sim");
                        post_comand.put("Esquerda", "Nao");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }*/
            }
        }

        try {

            //Não permite que o mesmo comando seja executado seguidas vezes
            //Não permite que seja feita alguma requisção até que tenha se passado 5s que tudo foi iniciado
            //AsyncTask Evita que a tela fique travada por falta de resposta do serviço web
            if ((System.currentTimeMillis() - this.tmpInit) > 5000) {
                //oldcommand = command;
                asyncmove.Comando(command);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Error quando nao houver imagem
    public void setImageError() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                conexao.setText("Desconectado");
                return;
            }
        });
    }
    public String htmlPage() {

        String html = "<html>";
        html += "<body bgcolor=\"#000000\">";
        html += "Sem conexao com a internet";
        html += "</body>";
        html += "</html>";

        return html;
    }


}