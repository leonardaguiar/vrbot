package br.edu.ifba.vrrobot;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Léo on 28/01/2018.
 */
public class AsyncComandoHttpConnection extends AsyncTask<String,String,String> {
    private String URLbase = "";
    private String command ="";
    private String params = null;
    private Activity context;
    private boolean exit = false;
    private int delay = 1000;

    public AsyncComandoHttpConnection(String URLbase,Activity context, String comando) {
        this.URLbase = URLbase;
        this.context = context;

    }

    @Override
    protected String doInBackground(String... strings) {
        while (!exit) {
            try {
                if(!command.equals("")) {
                    WebClientHttpConnection comando = new WebClientHttpConnection();
                    comando.post(URLbase + "move/" + command, "");
                    command="";
                }/*else{
                    WebClientHttpConnection comando = new WebClientHttpConnection();
                    comando.post(URLbase + "move/" + "0", "");

                }*/

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "exit";
    }

    @Override
    protected void onPostExecute(String res) {
       if(res.equals("exit")) {
           WebClientHttpConnection comando = new WebClientHttpConnection();
           try {
               comando.post(URLbase + "move/" + "0", "");
           } catch (IOException e) {
               e.printStackTrace();
           }
           return;
       }
    }
    public void Exit(boolean exit){
        this.exit = exit;
    }

    public void Delay(int delay){
        this.delay = delay;
    }

    public void Comando(String command){
        this.command = command;
    }
}
