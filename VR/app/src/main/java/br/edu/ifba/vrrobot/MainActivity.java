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
import org.json.JSONException;
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
    private Sensor mAccelerometer;
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
        if(lst_config.size()>0){
            this.configuracao.setNome(lst_config.get(0).getNome());
            this.configuracao.setEndereco(lst_config.get(0).getEndereco());
            this.configuracao.setPorta_dados(lst_config.get(0).getPorta_dados());
            this.configuracao.setPorta_video(lst_config.get(0).getPorta_video());
            dataURL = configuracao.getEndereco() + ":"+configuracao.getPorta_dados()+"/";
            videoURL = configuracao.getEndereco() + ":" +configuracao.getPorta_video(); //+"/?action=stream";
        }


        //Inicicializa o serviço de sensores
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Inicializar a variavel para captura dos dados do acelerometro
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Determina o tipo de fonte do textview
        Typeface digitalfont = Typeface.createFromAsset(getAssets(),"fonts/DS-DIGIB.TTF");


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

        //Text de exibição dos dados da altitude...#15a0f6
        textviewaltura = (TextView) findViewById(R.id.text_view_altura);
        textviewaltura.setTextColor(Color.parseColor("#4169E1"));
        textviewaltura.setTypeface(digitalfont);
        textviewdescaltitude = (TextView) findViewById(R.id.text_view_desc_alt);
        textviewdescaltitude.setTextColor(Color.parseColor("#4169E1"));


        //Text de exibição dos dados da latitude...#15a0f6
        textviewlatitude = (TextView) findViewById(R.id.text_view_lat);
        textviewlatitude.setTextColor(Color.parseColor("#4169E1"));
        textviewlatitude.setTypeface(digitalfont);
        textviewdesclatitude = (TextView) findViewById(R.id.text_view_desc_lat);
        textviewdesclatitude.setTextColor(Color.parseColor("#4169E1"));

        //Text de exibição dos dados da latitude...#15a0f6
        textviewlongi = (TextView) findViewById(R.id.text_view_longi);
        textviewlongi.setTextColor(Color.parseColor("#4169E1"));
        textviewlongi.setTypeface(digitalfont);
        textviewdesclongi = (TextView) findViewById(R.id.text_view_desc_long);
        textviewdesclongi.setTextColor(Color.parseColor("#4169E1"));



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
        AsyncCommandHttpConnection asyncdados =  new AsyncCommandHttpConnection(videoURL,videoURL+"/?action=stream",dataURL, dataURL + "dados", 0, null, this);
        asyncdados.execute();
        //Captura o tempo que o App iniciou as principais iformações
        this.tmpInit = System.currentTimeMillis();
        JSONObject post_dict = new JSONObject();

        onResume();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void Rotaciona(View v) {
        Animation anim = new RotateAnimation(0, 180, 50, 50);
        anim.setFillAfter(true);
        anim.setDuration(10000);
        img_bussola.startAnimation(anim);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Float x = sensorEvent.values[0];
        Float y = sensorEvent.values[1];
        Float z = sensorEvent.values[2];
        img_frente.setVisibility(ImageView.INVISIBLE);
        img_tras.setVisibility(ImageView.INVISIBLE);
        img_esquerda.setVisibility(ImageView.INVISIBLE);
        img_direita.setVisibility(ImageView.INVISIBLE);

        post_comand = new JSONObject();
        if ((y < 5 && y > -4) && x>0) { // O dispositivo esta de cabeça pra baixo
            //Esta indo para frente
            if (z >= 5.0) {
                img_frente.setVisibility(ImageView.VISIBLE);
                img_tras.setVisibility(ImageView.INVISIBLE);
                //Alimenta o json frente
                command ="1";
                //Esta indo para direita
                if (y < -2) {
                    img_esquerda.setVisibility(ImageView.VISIBLE);
                    img_direita.setVisibility(ImageView.INVISIBLE);
                    command ="3";
                }
                //Esta indo para esquerda
                else if (y > 2) {
                    img_esquerda.setVisibility(ImageView.INVISIBLE);
                    img_direita.setVisibility(ImageView.VISIBLE);
                    command ="2";
                }
            } else if (z <= -3.0) {//Esta indo para tras

                img_frente.setVisibility(ImageView.INVISIBLE);
                img_tras.setVisibility(ImageView.VISIBLE);
                command = "4";
                if (y < -2) {
                    img_esquerda.setVisibility(ImageView.VISIBLE);
                    img_direita.setVisibility(ImageView.INVISIBLE);
                    try {
                        post_comand.put("Direita", "Nao");
                        post_comand.put("Esquerda", "Sim");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (y > 2) {
                    img_esquerda.setVisibility(ImageView.INVISIBLE);
                    img_direita.setVisibility(ImageView.VISIBLE);
                    try {
                        post_comand.put("Direita", "Sim");
                        post_comand.put("Esquerda", "Nao");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }else{
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

      try {
           //Registra o tempo de execução no momento
            long tmpNow = System.currentTimeMillis();
          //Não permite que o mesmo comando seja executado seguidas vezes
          //Não permite que seja feita alguma requisção até que tenha se passado 5s que tudo foi iniciado
          //Evita que a tela fique travada por falta de resposta do vrrobot server
            if(!oldcommand.equals(command) && (System.currentTimeMillis() - this.tmpInit) > 5000) {
                oldcommand = command;
                WebClientHttpConnection comando = new WebClientHttpConnection();
                comando.post(dataURL + "move/" + command, "");
            }
        } catch (IOException e) {
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

    // Mover classe interna para classe externa
    // Video em Background
    /*  private class StreamVideo extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //Cria a barra de progresso
                mDialog = new ProgressDialog(MainActivity.this);
                // Titulo
                mDialog.setTitle("Iniciando o stream de video");
                //Mensagem
                mDialog.setMessage("Carregando...");
                mDialog.setIndeterminate(false);
                // Mostra progresso
                mDialog.show();

            }

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected void onPostExecute(Void args) {

                try {
                    // Start the MediaController
                    MediaController mediacontroller = new MediaController(
                            MainActivity.this);
                    mediacontroller.setAnchorView(videoview);
                    // Get the URL from String VideoURL
                    Uri video = Uri.parse(videoURL);
                    videoview.setMediaController(mediacontroller);
                    videoview.setVideoURI(video);

                    videoview.requestFocus();
                    videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                            mDialog.dismiss();
                            videoview.start();
                        }
                    });
                } catch (Exception e) {
                    mDialog.dismiss();
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

            }

        }


    */

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        @Override
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work

            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
            HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
            //if (DEBUG) Log.d(TAG, "1. Sending http request");
            try {

                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                if (DEBUG)
                    Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if (res.getStatusLine().getStatusCode() == 401) {
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                //if (DEBUG) {
                e.printStackTrace();
                //Log.d(TAG, "Request failed-ClientProtocolException", e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            conexao.setText("Conectado!");
            if (result != null) {
                conexao.setText("Conectado...");
                result.setSkip(1);
                setTitle(R.string.app_name);
            } else {
                conexao.setText("Desconectado");
            }
            mv.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
            mv.showFps(false);
        }
    }

    private void UpdateData() {
       //new AsyncCommandHttpConnection(dataURL, dataURL + "dados", 0, null, this).execute();

    }
    public Boolean getUrlDisponivel(String urlvideo) throws IOException {
        String urlName = null;
        if (urlName == null) {
            urlName = urlvideo;
        }
        WebClientHttpConnection status = new WebClientHttpConnection();
        if (status.status(urlName).equals("200")) {
            return true;
        }
        else return false;
       /* java.net.HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new java.net.URL(urlName.toString());
            urlConnection = (java.net.HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == java.net.HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }*/
    }
    public String htmlPage(){

        String html = "<html>";
        html += "<body bgcolor=\"#000000\">";
        html += "Sem conexao com a internet";
        html += "</body>";
        html += "</html>";

        return html;
    }


}