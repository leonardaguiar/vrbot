package br.edu.ifba.vrrobot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import br.edu.ifba.bd.BD;
import br.edu.ifba.modelos.Configuracao;

public class TerminalComandoActivity extends Activity {
    Configuracao configuracao = new Configuracao();
    private String URLbase = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terminal_comando_activity);
        BD bd = new BD(this);
        List<Configuracao> lst_config = bd.buscar();
        if(lst_config.size()>0){
            configuracao.setNome(lst_config.get(0).getNome());
            configuracao.setEndereco(lst_config.get(0).getEndereco());
            configuracao.setPorta_dados(lst_config.get(0).getPorta_dados());
            configuracao.setPorta_video(lst_config.get(0).getPorta_video());
            URLbase = configuracao.getEndereco() + ":"+configuracao.getPorta_dados()+"/";
        }

    }

    public void ShutdownButton(View view) throws InterruptedException {
        StatusAsync asyncstatus;
        asyncstatus =  new StatusAsync(0,this);
        asyncstatus.execute();
    }

    public void RebootButton(View view) throws InterruptedException {
        StatusAsync asyncstatus;
        asyncstatus =  new StatusAsync(1,this);
        asyncstatus.execute();
    }



    public void Shutdown() {

    }

    public void Reboot() {


    }
    private class StatusAsync extends AsyncTask<String,String,String> {
        private int tipo_comando = -1;
        private Context context;
        public StatusAsync(int tipo_comando, Context context){
           this.tipo_comando = tipo_comando;
           this.context = context;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                WebClientHttpConnection command = new WebClientHttpConnection();
               if(this.tipo_comando == 0) {
                   if (command.status(URLbase).equals("200")) {
                       command.get(URLbase + "stop");
                       return "true";
                   } else {
                       return "false";
                   }
               }else if (this.tipo_comando == 1){
                   if (command.status(URLbase).equals("200")) {
                       command.get(URLbase + "reboot");
                       return "true";

                   } else {
                       return "false";
                   }
               }

            } catch (IOException e) {
                e.printStackTrace();
                return "false";
            }
            return "false";
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.equals("true")) {
                if(this.tipo_comando == 0)
                    Toast.makeText(context, "Comando shutdown enviado!", Toast.LENGTH_SHORT).show();
                else if(this.tipo_comando == 1)
                    Toast.makeText(context, "Comando reboot enviado!", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context, "Impossivel executar a ação!", Toast.LENGTH_SHORT).show();

        }
    }
}
