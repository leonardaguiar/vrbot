package br.edu.ifba.vrrobot;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;

import br.edu.ifba.bd.BD;
import br.edu.ifba.modelos.Configuracao;

public class ConfiguracaoActivity extends Activity {
    private Configuracao configuracao = new Configuracao();
    private EditText nomeEt;
    private EditText enderelEt;
    private EditText portadadosEt;
    private EditText portavideoEt;
    private EditText delayreqEt;
    private RadioButton rd_somente_vr;
    private CheckBox chk_somente_vr;
    private Button salvarBt;
    private Button editarBt;
    private RadioButton rd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
        /*ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));*/
        this.nomeEt = (EditText)this.findViewById(R.id.edit_nome);
        this.nomeEt.requestFocus();
        this.enderelEt = (EditText)this.findViewById(R.id.edit_endereco);
        this.portadadosEt = (EditText)this.findViewById(R.id.edit_portadados);
        this.portavideoEt = (EditText)this.findViewById(R.id.edit_portavideo);
        this.delayreqEt = (EditText)this.findViewById(R.id.edit_delay);
        this.chk_somente_vr = (CheckBox) this.findViewById(R.id.checkBox_somente_modo_vr);

        BD bd = new BD(this);
        List <Configuracao> lst_config = bd.buscar();
        if(lst_config.size()>0){
            this.configuracao.setId(lst_config.get(0).getId());
            this.configuracao.setNome(lst_config.get(0).getNome());
            this.configuracao.setEndereco(lst_config.get(0).getEndereco());
            this.configuracao.setPorta_dados(lst_config.get(0).getPorta_dados());
            this.configuracao.setPorta_video(lst_config.get(0).getPorta_video());

            this.nomeEt.setText(lst_config.get(0).getNome());
            this.enderelEt.setText(lst_config.get(0).getEndereco());
            this.portadadosEt.setText(lst_config.get(0).getPorta_dados());
            this.portavideoEt.setText(lst_config.get(0).getPorta_video());
            this.delayreqEt.setText(Integer.toString(lst_config.get(0).getDelay_req()));
            this.chk_somente_vr.setChecked(lst_config.get(0).isSomente_vr());
            //this.rd.setActivated(true);

        }

    }
    public void salvar(View view) {
        this.configuracao.setNome(this.nomeEt.getText().toString());
        this.configuracao.setEndereco(this.enderelEt.getText().toString());
        this.configuracao.setPorta_dados(this.portadadosEt.getText().toString());
        this.configuracao.setPorta_video(this.portavideoEt.getText().toString());
        this.configuracao.setDelay_req(Integer.getInteger(this.delayreqEt.getText().toString()));
        this.configuracao.setSomente_vr(this.chk_somente_vr.isChecked());

        BD bd = new BD(this);
        bd.inserir(this.configuracao);
        Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
    }

    public void editarConfiguracao(View view) {
        this.configuracao.setNome(this.nomeEt.getText().toString());
        this.configuracao.setEndereco(this.enderelEt.getText().toString());
        this.configuracao.setPorta_dados(this.portadadosEt.getText().toString());
        this.configuracao.setPorta_video(this.portavideoEt.getText().toString());
        this.configuracao.setPorta_video(this.portavideoEt.getText().toString());
        this.configuracao.setDelay_req(Integer.parseInt(this.delayreqEt.getText().toString()));
        this.configuracao.setSomente_vr(this.chk_somente_vr.isChecked());
        BD bd = new BD(this);
        bd.atualizar(this.configuracao);
        Toast.makeText(this, "Configuração \"" + this.configuracao.getNome() + "\" atuailizado com sucesso.", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.id, menu);
        return true;
    }
}
