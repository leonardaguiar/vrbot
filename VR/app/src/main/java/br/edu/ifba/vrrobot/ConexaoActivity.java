package br.edu.ifba.vrrobot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import br.edu.ifba.bd.BD;
import br.edu.ifba.modelos.Configuracao;

public class ConexaoActivity extends Activity {

    private Button tentar_novamenteBT;
    private String URLbase;
    private String URLping;
    private Activity context;
    private TextView textViewStatus;
    private ImageView img_conexao;
    private ProgressBar progresbar;
    StatusAsync asyncstatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexao);
        textViewStatus = (TextView) findViewById(R.id.text_view_conexao_status);
        tentar_novamenteBT = (Button) findViewById(R.id.btn_tentar_novamente);
        img_conexao = (ImageView) findViewById(R.id.img_conexao);
        progresbar = (ProgressBar) findViewById(R.id.progressBar2);
        //Carrega dados
        Configuracao configuracao = new Configuracao();
        String dataURL = "";
        String videoURL = "";
        BD bd = new BD(this);
        List<Configuracao> lst_config = bd.buscar();
        if(lst_config.size()>0){
            configuracao.setNome(lst_config.get(0).getNome());
            configuracao.setEndereco(lst_config.get(0).getEndereco());
            configuracao.setPorta_dados(lst_config.get(0).getPorta_dados());
            configuracao.setPorta_video(lst_config.get(0).getPorta_video());
            URLbase = configuracao.getEndereco() + ":"+configuracao.getPorta_dados()+"/";
            URLping = configuracao.getEndereco();
            videoURL = configuracao.getEndereco() + ":" +configuracao.getPorta_video(); //+"/?action=stream";
        }

       Status();
    }
    public void TentarNovamente(View view) throws InterruptedException {
        textViewStatus.setText("Iniciando Conexão...");
        Status();
    }
    private void Status(){
        tentar_novamenteBT.setVisibility(View.INVISIBLE);
        img_conexao.setVisibility(View.INVISIBLE);
        progresbar.setVisibility(View.VISIBLE);
        asyncstatus =  new StatusAsync();
        asyncstatus.execute();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        asyncstatus.cancel(true);
        this.finish();
    }
    public void Conectar() throws InterruptedException {

        Intent intent = new Intent(this, SplashEstabilizaActivity.class);
        startActivity(intent);
        this.finish();
    }

    private class StatusAsync extends AsyncTask<String,String,String>{

        public void StatusAsync(){

        }
        @Override
        protected String doInBackground(String... strings) {
            boolean stop = false;
            int tentativas = 0;
            InetAddress in;
            in = null;
            //Verifica se o host existe na rede
            try {
                //Limpa as informações da url para realizar o ping
                 in = InetAddress.getByName(URLping.replaceAll("http://", ""));
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Aguarda 10 segundos até a resposta do host
            try {
                if (in.isReachable(10000));
                else //Caso não responda para o processamento
                    return "false";

            } catch (IOException e) {
                return "false";
            }
            while (!stop) {
                tentativas++;
                if(tentativas==11) {
                    stop = true;
                    return "false";
                }else {
                    try {

                        WebClientHttpConnection status = new WebClientHttpConnection();

                        if (status.status(URLbase).equals("200")) {
                           return "true";
                        }
                        //Dorme por 1 segundo
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "false";
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.equals("false")) {
                textViewStatus.setText("Falha ao conectar!");
                img_conexao.setImageResource(R.drawable.falha_conexao);
                img_conexao.setVisibility(View.VISIBLE);
                progresbar.setVisibility(View.INVISIBLE);
                tentar_novamenteBT.setVisibility(View.VISIBLE);
            }else{
                textViewStatus.setText("Conectado!");
                img_conexao.setImageResource(R.drawable.conectado);
                img_conexao.setVisibility(View.VISIBLE);
                progresbar.setVisibility(View.INVISIBLE);
                //tentar_novamenteBT.setVisibility(View.VISIBLE);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Conectar();
                    } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
