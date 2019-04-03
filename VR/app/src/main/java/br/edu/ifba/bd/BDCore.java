package br.edu.ifba.bd;

/**
 * Created by Léo on 06/02/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class BDCore extends SQLiteOpenHelper {
    private static final String NOME_BD = "vrrobot";
    private static final int VERSAO_BD = 1;

    public BDCore(Context ctx) {
        super(ctx, "vrrobot", (CursorFactory) null, 8);
    }

    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("create table configuracao(_id integer primary key autoincrement, nome text, endereco text not null," +
                " porta_dados text not null, porta_video text not null, delay_req INTEGER, somente_vr INTEGER);");
    }

    public void onUpgrade(SQLiteDatabase bd, int arg1, int arg2) {
        bd.execSQL("drop table configuracao;");
        this.onCreate(bd);
    }
    public boolean checkDataBase() {
        SQLiteDatabase db = null;
        try {
            String path = "vrrobot";
            db =
                    SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            db.close();
        } catch (SQLiteException e) {
            // O banco não existe
        }
// Retorna verdadeiro se o banco existir, pois o ponteiro irá existir,
// se não houver referencia é porque o banco não existe
        return db != null;
    }
}