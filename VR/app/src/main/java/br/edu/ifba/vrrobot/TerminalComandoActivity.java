package br.edu.ifba.vrrobot;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.util.List;

import br.edu.ifba.bd.BD;
import br.edu.ifba.modelos.Configuracao;

public class TerminalComandoActivity extends AppCompatActivity {
    private String URLbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_comandos);

        Configuracao configuracao = new Configuracao();
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
       Shutdown();
    }
    public void RebootButton(View view) throws InterruptedException {
        Reboot();
    }
    public void Shutdown(){

        WebClientHttpConnection command = new WebClientHttpConnection();
        try {
            if (command.status(URLbase).equals("200")) {
               command.get(URLbase + "stop/");
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                dialog = builder.create();
                builder.setMessage("Comando executado com sucesso!")
                        .setTitle("");
                dialog.show();
            }else{
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                dialog = builder.create();
                builder.setMessage("Imposivel executar a ação!")
                        .setTitle("");
                dialog.show();
            }

        } catch (IOException e) {
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            dialog = builder.create();
            builder.setMessage("Imposivel executar a ação!")
                    .setTitle("");
            dialog.show();
            e.printStackTrace();
        }
    }
    public void Reboot(){

        WebClientHttpConnection command = new WebClientHttpConnection();
        try {
            if (command.status(URLbase).equals("200")) {
                command.get(URLbase + "reboot/");
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                dialog = builder.create();
                builder.setMessage("Comando executado com sucesso!")
                        .setTitle("");
                dialog.show();
            }else{

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                dialog = builder.create();
                builder.setMessage("Imposivel executar a ação!")
                        .setTitle("");
                dialog.show();
            }
        } catch (IOException e) {
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            dialog = builder.create();
            builder.setMessage("Imposivel executar a ação!")
                    .setTitle("");
            dialog.show();
            e.printStackTrace();
        }
    }
}
