package br.edu.ifba.vrrobot;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import br.edu.ifba.bd.BD;
import br.edu.ifba.modelos.Configuracao;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VRActivity extends Activity {
    Configuracao configuracao = new Configuracao();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vr_activity);
        BD bd = new BD(this);
        List <Configuracao> configuracoes = bd.buscar();
        if(configuracoes.size()==0) {
            configuracao.setNome("Padrão");
            configuracao.setEndereco("http://192.168.1.150");
            configuracao.setPorta_dados("5000");
            configuracao.setPorta_video("8080");
            bd.inserir(this.configuracao);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Chain together various setter methods to set the dialog characteristics


// 3. Get the AlertDialog from create()
        AlertDialog alert = builder.create();
        builder.setMessage("Você esta desconectado!")
                .setTitle("");
        alert = builder.show();
        alert.dismiss();


    }
    public void Conectar(View view) throws InterruptedException {

        Intent intent = new Intent(this, SplashEstabilizaActivity.class);
        startActivity(intent);
        //this.finish();
    }

    public void Configuracao(View view){

        Intent intent = new Intent(this, ConfiguracaoActivity.class);
        startActivity(intent);
    }

}
