package projetos.inatel.br.projetospessoais.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import projetos.inatel.br.projetospessoais.R;

/**
 * Created by ckilee on 23/08/15.
 */
public class ProjectCursorAdapter extends SimpleCursorAdapter{
    public static final String TAG = ProjectCursorAdapter.class.getSimpleName();
    // Campos do banco
    private static String[] fromProject = new String[]{ProjectContract.Column.NAME,
            ProjectContract.Column.DESCRIPTION, ProjectContract.Column.OWNER,
            ProjectContract.Column.CREATION_DATE};

    // Campos da UI
    private static int[] toProject = new int[]{R.id.item_name, R.id.item_description
            , R.id.item_owner,
            R.id.item_date};

    private Cursor cursor;
    //private ImageRetriever ir;
    private int idCloudIndex;
    private int imageFileIndex;

    public ProjectCursorAdapter(Context context, Cursor c) {
        super(context, R.layout.project_list_item, c, fromProject, toProject, 0);
        this.cursor = c;
        /*
        ir = new ImageRetriever(context, ImageRetriever.CacheType.SMALL);
        idCloudIndex = cursor.getColumnIndex(AnuncioContract.Column.ID_CLOUD);
        imageFileIndex = cursor.getColumnIndex(AnuncioContract.Column.IMAGE_FILE);*/
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (cursor.isClosed()) {
            return view;
        }
        /*
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        Integer idCloud = cursor.getInt(idCloudIndex);
        String imagePath = cursor.getString(imageFileIndex);
        Log.d(TAG, "getView " + position + " idCloud: " + idCloud + " imagePath: " +
                imagePath);

        // busca do cache
        Bitmap bitmap = ir.getLoadedImage(idCloud, imageView, imagePath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }*/
        return view;
    }
}
