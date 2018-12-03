package br.edu.ifba.bd;

/**
 * Created by Léo on 06/02/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class BDCore extends SQLiteOpenHelper {
    private static final String NOME_BD = "vrrobot";
    private static final int VERSAO_BD = 1;

    public BDCore(Context ctx) {
        super(ctx, "vrrobot", (CursorFactory) null, 7);
    }

    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("create table configuracao(_id integer primary key autoincrement, nome text, endereco text not null," +
                " porta_dados text not null, porta_video text not null);");
    }

    public void onUpgrade(SQLiteDatabase bd, int arg1, int arg2) {
        bd.execSQL("drop table configuracao;");
        this.onCreate(bd);
    }
}