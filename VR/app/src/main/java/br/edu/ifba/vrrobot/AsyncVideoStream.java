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
public class AsyncVideoStream extends AsyncTask<String,String,String> {
    public static final int DID_START = 0;
    public static final int DID_ERROR = 1;
    public static final int DID_SUCCEED = 2;

    private static final int GET = 0;
    private static final int POST = 1;
    private static final int PUT = 2;
    private static final int DELETE = 3;
    private String URLRequest ="";
    private String URLbase = "";
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
    private boolean alert = false;
    private boolean webcreated = false;
    private int alertOnlineHit = 0;
    private int alertHit = 0;
    private AlertDialog.Builder builderConectado;
    private AlertDialog.Builder builderDesconectado;
    private AlertDialog dialogConectado;
    private AlertDialog dialogDesconectado;
    private WebView webview;

    public AsyncVideoStream(String URLbase, String URLRequest, int metodo, String params, Activity context) {
        this.textviewtemp = (TextView) context.findViewById(R.id.text_view_temp);
        this.textviewtumd = (TextView) context.findViewById(R.id.text_view_umd);
        this.textviewfire = (TextView) context.findViewById(R.id.text_view_fire);
        this.textviewdist = (TextView) context.findViewById(R.id.text_view_dist);
        this.URLRequest = URLRequest;
        this.metodo = metodo;
        this.params = params;
        this.imgalert = (ImageView) context.findViewById(R.id.img_alert);
        this.context = context;
        this.URLbase = URLbase;
        this.layoutInflater = context.getLayoutInflater();
        this.builderConectado =  new AlertDialog.Builder(context);
        this.builderDesconectado =  new AlertDialog.Builder(context);
        this.webview = (WebView) context.findViewById(R.id.webView);
    }

    @Override
    protected String doInBackground(String... strings) {
        while (true) {
            try {

                WebClientHttpConnection status = new WebClientHttpConnection();
                if (status.status(URLbase).equals("200")) {
                    //Exibe duas vezes uma Toast para o usuario para informar que esta conectado
                    if((alertOnlineHit < 2) && (metodo == 0 || metodo == 1)) {
                        //Se em algum momento o alertdialog foi exibido será eleiminado
                        if(alert){
                            dialogDesconectado.dismiss();
                            dialogDesconectado.cancel();
                            alert= false;

                        }
                        //incrementada o contador de exibição do Toast
                        alertOnlineHit ++;
                        online = true;
                        //Envia a mesagem para ser exibido o Toast
                        publishProgress("online");

                    }
                    String res = "";
                    //Solicita do VRrobot os metadados
                    if (metodo == 0) {
                        WebClientHttpConnection comando = new WebClientHttpConnection();
                        res = comando.get(URLRequest);
                        publishProgress(res);

                    }else if (metodo == 1 ) {

                    }else if (metodo == 2){
                        WebClientHttpConnection comando = new WebClientHttpConnection();
                        res = comando.status(URLRequest);
                        publishProgress(res);
                        alert = false;
                    }
                    //Dorme por 1 segundo
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    //Envia uma mensagem para ser exibido o AlertDialog com a mensagem que esta desconectado
                    publishProgress("");
                    //Dorme por 1 segundo
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onProgressUpdate(String...values) {
        String res = values[0];
        //Se estiver online exibe o Toast
        if(res.equals("online") && !alert){
        Toast.makeText(context,"Você esta conectado", Toast.LENGTH_SHORT).show();
        }
        //AlertDialog com a mensagem que esta desconectado
        else if (res.isEmpty()){
               if((!alert) &&(metodo == 0 || metodo == 1)) {

                   online = false;
                   alert = true;
                   alertOnlineHit = 0;
                   this.dialogDesconectado = builderDesconectado.create();
                   this.builderDesconectado.setMessage("Você esta desconectado!")
                           .setTitle("");
                  this.dialogDesconectado = this.builderDesconectado.show();
               }else if (!alert && metodo == 2){
                   if(!webcreated) {
                       //Define configurações para o webview
                       webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                       webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                       webview.getSettings().setDomStorageEnabled(true);
                       webview.getSettings().setAppCacheEnabled(true);
                       webview.loadData(htmlPage(),"text/html", "UTF-8");
                       webview.setWebViewClient(new WebViewClient());
                       alert=true;
                   }else {
                       webview.loadData(htmlPage(),"text/html", "UTF-8");
                       webview.setWebViewClient(new WebViewClient());
                       alert=true;
                   }

               }
            //Carrega Toast personalizado
       /* int layout = R.layout.custom_toast;
        ViewGroup viewGroup = (ViewGroup) context.findViewById(R.id.custom_toast_container);
        View view = layoutInflater.inflate(layout, viewGroup);

        TextView tv_texto = (TextView) view.findViewById(R.id.text_toast);
        tv_texto.setText("Sem conexão");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(view);
        toast.show();*/
        }else {//O VRRobot esta conectado
            if(metodo == 0 || metodo == 1) {
                res = res.replaceAll("\"", "");
                res = res.replaceAll("\\n", "");
                String comandos[] = res.split(",");
                if (comandos.length == 4) {
                    textviewdist.setText(comandos[0] + "m");
                    textviewfire.setText(comandos[1] + "i");
                    textviewtemp.setText(comandos[2] + "ºC");
                    textviewtumd.setText(comandos[3] + "%U");
                    int alert = Integer.parseInt(comandos[1]);
                    if (alert > 80)
                        imgalert.setImageResource(R.drawable.ic_action_alert_red);
                    else imgalert.setImageResource(R.drawable.ic_action_alert);
                }
            }else if (metodo == 2){
               //Cria e carrega o video na webview
               if(!webcreated) {
                   //Define configurações para o webview
                   webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                   webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                   webview.getSettings().setDomStorageEnabled(true);
                   webview.getSettings().setAppCacheEnabled(true);
                   webview.loadUrl(this.URLRequest);
                   webview.setWebViewClient(new WebViewClient());
               }else{
                   webview.loadUrl(this.URLRequest);
                   webview.setWebViewClient(new WebViewClient());
               }
            }
        }
    }
    @Override
    protected void onPostExecute(String res) {
        if (res == null){
            Toast.makeText(context, "Você esta desconectado!", Toast.LENGTH_SHORT).show();

         //Carrega Toast personalizado
       /* int layout = R.layout.custom_toast;
        ViewGroup viewGroup = (ViewGroup) context.findViewById(R.id.custom_toast_container);
        View view = layoutInflater.inflate(layout, viewGroup);

        TextView tv_texto = (TextView) view.findViewById(R.id.text_toast);
        tv_texto.setText("Sem conexão");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(view);
        toast.show();*/
    }else {
            dialogDesconectado.dismiss();
            dialogDesconectado.cancel();
            res = res.replaceAll("\"", "");
            res = res.replaceAll("\\n", "");
            String comandos[] = res.split(",");
            if (comandos.length == 4) {
                textviewdist.setText(comandos[0] + "m");
                textviewfire.setText(comandos[1] + "i");
                textviewtemp.setText(comandos[2] + "ºC");
                textviewtumd.setText(comandos[3] + "%U");
                int alert = Integer.parseInt(comandos[1]);
                if (alert > 80)
                     imgalert.setImageResource(R.drawable.ic_action_alert_red);
                else imgalert.setImageResource(R.drawable.ic_action_alert);
            }
        }

        //return;
    }
    private String htmlPage(){

        String html = "<html>";
       // html += "<body bgcolor=\"#000000\">";
        html += "Sem conexao com a internet";
        html += "</body>";
        html += "</html>";

        return html;
    }

    }









