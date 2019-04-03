package br.edu.ifba.vrrobot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.zip.Inflater;

import br.edu.ifba.modelos.Configuracao;

import static java.lang.Thread.sleep;

/**
 * Created by Léo on 28/01/2018.
 */
public class AsyncReqHttpConnection extends AsyncTask<String,String,String> {
    private String URLRequest ="";
    private String URLbase = "";
    private String URLbasevideo = "";
    private String URLvideo = "";
    private int metodo = -1;
    private String params = null;
    private Activity context;
    private TextView textviewtemp;
    private TextView textviewtumd;
    private TextView textviewdist;
    private TextView textviewfire;
    private ImageView imgalert;
    private LayoutInflater layoutInflater;
    private boolean online = false;
    private boolean streamonline = false;
    private boolean alert = false;
    private boolean webcreated = false;
    private boolean streamoffline = false;
    private boolean streamloaded = false;
    private int alertOnlineHit = 0;
    private int alertHit = 0;
    private AlertDialog.Builder builderConectado;
    private AlertDialog.Builder builderDesconectado;
    private AlertDialog dialogConectado;
    private AlertDialog dialogDesconectado;
    private WebView webview;
    private WebView webview2;
    private boolean exit = false;
    private int delay = 1000;

    public AsyncReqHttpConnection(String URLbasevideo, String URLvideo,
                                  String URLbase, String URLRequest, int metodo, String params, Activity context) {
        this.textviewtemp = (TextView) context.findViewById(R.id.text_view_temp);
        this.textviewtumd = (TextView) context.findViewById(R.id.text_view_umd);
        this.textviewfire = (TextView) context.findViewById(R.id.text_view_fire);
        this.textviewdist = (TextView) context.findViewById(R.id.text_view_dist);
        this.URLRequest = URLRequest;
        this.URLbase = URLbase;
        this.URLbasevideo = URLbasevideo;
        this.URLvideo = URLvideo;
        this.metodo = metodo;
        this.params = params;
        this.imgalert = (ImageView) context.findViewById(R.id.img_alert);
        this.context = context;
        this.layoutInflater = context.getLayoutInflater();
        this.builderConectado =  new AlertDialog.Builder(context);
        this.builderDesconectado =  new AlertDialog.Builder(context);
        this.webview = (WebView) context.findViewById(R.id.webView);

    }

    @Override
    protected String doInBackground(String... strings) {
        while (!exit) {
            try {
                WebClientHttpConnection status = new WebClientHttpConnection();
                online = false;
                streamonline = false;
                if (status.status(URLbase).equals("200")) {
                    //Exibe duas vezes uma Toast para o usuario para informar que esta conectado
                    if((alertOnlineHit < 2) && (metodo == 0)) {
                        //Se em algum momento o alertdialog foi exibido será eleiminado
                        if(alert){
                            dialogDesconectado.dismiss();
                            dialogDesconectado.cancel();
                            alert= false;
                        }
                        //incrementada o contador de exibição do Toast
                        alertOnlineHit ++;
                        //Envia a mesagem para ser exibido o Toast
                        publishProgress("online");
                    }
                    online = true;
                    String res = "";
                    //Solicita do VRrobot os metadados
                    if (metodo == 0) {
                        WebClientHttpConnection comando = new WebClientHttpConnection();
                        res = comando.get(URLRequest);
                        publishProgress(res);

                    }else if (metodo == 1 ) {
                        WebClientHttpConnection comando = new WebClientHttpConnection();
                        res = comando.post(URLRequest,null);
                    }else if (metodo == 2){

                    }

                }

                streamonline = false;
                if(status.status(URLbasevideo).equals("200")){
                     streamonline = true;
                     publishProgress("stream");

                }
                //Dorme por 1 segundo
                Thread.sleep(this.delay);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    //Envia uma mensagem para ser exibido o AlertDialog com a mensagem que esta desconectado
                    publishProgress("");
                    //Dorme por 1 segundo
                    Thread.sleep(this.delay);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "exit";
    }

    @Override
    protected void onProgressUpdate(String...values) {
        String res = values[0];
        //Se estiver online exibe o Toast
        if(res.equals("online") && !alert){
        Toast.makeText(context,"Você está conectado!", Toast.LENGTH_SHORT).show();
        }
        //AlertDialog com a mensagem que esta desconectado
        else if (res.isEmpty()){
               if((!online) && (!alert)) {

                   online = false;
                   alert = true;
                   alertOnlineHit = 0;
                   this.dialogDesconectado = builderDesconectado.create();
                   this.builderDesconectado.setMessage("Você esta desconectado!")
                           .setTitle("");
                  this.dialogDesconectado = this.builderDesconectado.show();
               }else if (!streamonline && !streamoffline){
                   if(!webcreated) {
                       //Define configurações para o webview
                       webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                       webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                       webview.getSettings().setDomStorageEnabled(true);
                       webview.getSettings().setAppCacheEnabled(true);
                       webview.loadData(htmlPage(),"text/html", "UTF-8");
                       webview.setWebViewClient(new WebViewClient());

                       streamoffline = true;
                       streamonline = false;
                   }else {
                       webview.loadData(htmlPage(),"text/html", "UTF-8");
                       webview.setWebViewClient(new WebViewClient());

                       streamoffline = true;
                       streamonline = false;
                   }

               }

        }else {//O VRRobot esta conectado
            if(!res.equals("stream")) {
                res = res.replaceAll("\"", "");
                res = res.replaceAll("\\n", "");
                String comandos[] = res.split(",");
                if (comandos.length == 4) {
                    textviewdist.setText(comandos[0] + "m");
                    textviewfire.setText(comandos[1] + "i");
                    textviewtemp.setText(comandos[2] + "ºC");
                    textviewtumd.setText(comandos[3] + "%U");
                    int alert = Integer.parseInt(comandos[1]);
                    if (alert > 235)
                        imgalert.setImageResource(R.drawable.ic_action_alert_red);
                    else imgalert.setImageResource(R.drawable.ic_action_alert);
                }else
                {
                    textviewdist.setText(comandos[0]);

                }
            }else if (res.equals("stream") && !streamloaded ){
               //Cria e carrega o video na webview
               if(!webcreated) {
                   //Define configurações para o webview
                   webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                   webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                   webview.getSettings().setDomStorageEnabled(true);
                   webview.getSettings().setAppCacheEnabled(true);
                   webview.loadUrl(this.URLvideo);
                   webview.setWebViewClient(new WebViewClient());

                   streamloaded = true;
                   streamoffline = false;
               }else{
                   webview.loadUrl(this.URLvideo);
                   webview.setWebViewClient(new WebViewClient());

                   streamloaded = true;
                   streamoffline = false;
               }
            }
        }
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
    private String htmlPage(){

        String html = "<html>";
        html += "<body bgcolor=\"#B0C4DE\">";
        html += "O Stream de video não pode ser exbido, o serviço esta offline!";
        html += "</body>";
        html += "</html>";

        return html;
    }
    public void Exit(boolean exit){
        this.exit = exit;
    }

    public void Delay(int delay){
        this.delay = delay;
    }
}
