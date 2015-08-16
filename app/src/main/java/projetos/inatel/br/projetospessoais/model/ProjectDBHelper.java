package projetos.inatel.br.projetospessoais.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ckilee on 16/08/15.
 */
public class ProjectDBHelper extends SQLiteOpenHelper{
    public ProjectDBHelper(Context context) {
        super(context, ProjectContract.DB_NAME, null, ProjectContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL para criar a tabela project
        String CREATE_PROJECT_TABLE =
                "CREATE TABLE " + ProjectContract.PROJECT_TABLE + " ( "
                        + ProjectContract.Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ProjectContract.Column.NAME + " TEXT NOT NULL, "
                        + ProjectContract.Column.DESCRIPTION + " TEXT NOT NULL, "
                        + ProjectContract.Column.OWNER + " TEXT NOT NULL, "
                        + ProjectContract.Column.CREATION_DATE + " TEXT NOT NULL) ";
        db.execSQL(CREATE_PROJECT_TABLE);

        String CREATE_IMAGE_TABLE =
                "CREATE TABLE " + ProjectContract.IMAGE_TABLE + " ( "
                        + ProjectContract.Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ProjectContract.Column.PROJECT_FOREIGN_ID + " INTEGER, "
                        + ProjectContract.Column.IMAGE + " TEXT NOT NULL, "
                        + ProjectContract.Column.DESCRIPTION + " TEXT, "
                        + ProjectContract.Column.CREATION_DATE + " TEXT NOT NULL, "
                        + " FOREIGN KEY ("+ ProjectContract.Column.PROJECT_FOREIGN_ID +") REFERENCES "
                        + ProjectContract.PROJECT_TABLE+" ("+ ProjectContract.Column.ID +") ON DELETE CASCADE )";
        db.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Ainda não precisa fazer update
    }
}