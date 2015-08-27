package projetos.inatel.br.projetospessoais.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by ckilee on 16/08/15.
 */
public class ProjectDAO extends ProjectDBHelper{
    public static final String TAG = ProjectDAO.class.getSimpleName();
    public ProjectDAO(Context context) {
        super(context);
    }

    public void addProject(Project project) {
        Log.d(TAG, "addProject " + project.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        // Cria o ContentValues para adicionar: "column"/value
        ContentValues values = new ContentValues();
        if(project.getId()>-1)
            values.put(ProjectContract.Column.ID, project.getId());
        values.put(ProjectContract.Column.NAME, project.getName());
        values.put(ProjectContract.Column.DESCRIPTION, project.getDescription());
        values.put(ProjectContract.Column.OWNER, project.getOwner());
        values.put(ProjectContract.Column.CREATION_DATE, project.getCreationDate().getTime());


        // faz o insert
        project.setId((int) db.insert(ProjectContract.PROJECT_TABLE, null, values));
        db.close();
    }

    public void addImage(Image image) {
        Log.d(TAG, "addImage " + image.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        // Cria o ContentValues para adicionar: "column"/value
        ContentValues values = new ContentValues();
        values.put(ProjectContract.Column.PROJECT_FOREIGN_ID, image.getProjectId());
        values.put(ProjectContract.Column.IMAGE, image.getImage());
        values.put(ProjectContract.Column.DESCRIPTION, image.getDescription());
        values.put(ProjectContract.Column.CREATION_DATE, image.getCreationDate().getTime());


        // faz o insert
        db.insert(ProjectContract.IMAGE_TABLE, null, values);
        db.close();
    }

    public int getNextId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ProjectContract.PROJECT_TABLE, new String[]{
                BaseColumns._ID}, null, null, null, null, null);
        if(cursor==null){
            return 0;
        }
        int numberOfIds = cursor.getCount();
        return numberOfIds;

    }

    //consulta todos - retorna um cursor
    public Cursor getAllProjectAsCursor() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ProjectContract.PROJECT_TABLE, ProjectContract.PROJECT_COLUMN_NAMES, null, null, null, null,
                null);
        return cursor;
    }

    //get all images by project id
    public Cursor getAllImageAsCursor(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ProjectContract.IMAGE_TABLE, ProjectContract.IMAGE_COLUMN_NAMES, ProjectContract.Column.PROJECT_FOREIGN_ID+"=?", new String[]{id}, null, null,
                null);
        return cursor;
    }
}
