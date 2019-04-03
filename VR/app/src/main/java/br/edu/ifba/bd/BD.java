package br.edu.ifba.bd;

/**
 * Created by Léo on 06/02/2018.
 */

import android.database.sqlite.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifba.modelos.Configuracao;

public class BD {
    private SQLiteDatabase bd;

    public BD(Context context) {
        BDCore auxBd = new BDCore(context);
        /*if(!auxBd.checkDataBase()) {
           auxBd.onCreate(bd);
        }*/
        this.bd = auxBd.getWritableDatabase();
    }

    public void inserir(Configuracao configuracao) {
        ContentValues valores = new ContentValues();
        valores.put("nome", configuracao.getNome());
        valores.put("endereco", configuracao.getEndereco());
        valores.put("porta_dados", configuracao.getPorta_dados());
        valores.put("porta_video", configuracao.getPorta_video());
        valores.put("delay_req", configuracao.getDelay_req());
        valores.put("somente_vr", configuracao.isSomente_vr());
        this.bd.insert("configuracao", (String)null, valores);
    }

    public void atualizar(Configuracao configuracao) {
        ContentValues valores = new ContentValues();
        valores.put("nome", configuracao.getNome());
        valores.put("endereco", configuracao.getEndereco());
        valores.put("porta_dados", configuracao.getPorta_dados());
        valores.put("porta_video", configuracao.getPorta_video());
        valores.put("delay_req", configuracao.getDelay_req());
        valores.put("somente_vr", configuracao.isSomente_vr());
        this.bd.update("configuracao", valores, "_id = ?", new String[]{"" + configuracao.getId()});
    }

    public void deletar(Configuracao configuracao) {
        this.bd.delete("configuracao", "_id = " + configuracao.getId(), (String[])null);
    }

    public List<Configuracao> buscar() {
        ArrayList list = new ArrayList();
        String[] colunas = new String[]{"_id", "nome", "endereco","porta_dados","porta_video","delay_req","somente_vr"};
        Cursor cursor = this.bd.query("configuracao", colunas, (String)null, (String[])null, (String)null, (String)null,(String)null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Configuracao u = new Configuracao();
                u.setId((int) cursor.getLong(0));
                u.setNome(cursor.getString(1));
                u.setEndereco(cursor.getString(2));
                u.setPorta_dados(cursor.getString(3));
                u.setPorta_video(cursor.getString(4));
                u.setDelay_req(cursor.getInt(5));
                int somente_vr = (cursor.getInt(6));
                if(somente_vr == 1)
                    u.setSomente_vr(true);
                else
                    u.setSomente_vr(false);
                list.add(u);
            } while(cursor.moveToNext());
        }

        return list;
    }
}