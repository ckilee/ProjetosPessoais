package projetos.inatel.br.projetospessoais.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.io.IOException;

import projetos.inatel.br.projetospessoais.MainActivity;
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
    private int idColumn;


    public ProjectCursorAdapter(Context context, Cursor c) {
        super(context, R.layout.project_list_item, c, fromProject, toProject, 0);
        this.cursor = c;
        idColumn = cursor.getColumnIndex(ProjectContract.Column.ID);
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
        ProjectDAO projectDAO = MainActivity.getInstance().getProjectDAO();
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        int projectId = cursor.getInt(idColumn);
        int imageFileNameColumnIndex = 0;
        Cursor allImagesCursor = projectDAO.getAllImageAsCursor(Integer.toString(projectId));
        if(allImagesCursor!=null){
            imageFileNameColumnIndex = allImagesCursor.getColumnIndex(ProjectContract.Column.IMAGE);
            allImagesCursor.moveToFirst();
            String fileName = allImagesCursor.getString(imageFileNameColumnIndex);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyCameraApp"+File.separator+fileName);
            Uri fileUri = Uri.fromFile(mediaStorageDir);
            try {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(MediaStore.Images.Media.getBitmap(MainActivity.getInstance().getContentResolver(), fileUri), 300, 300);
                if(thumbnail!=null)
                    imageView.setImageBitmap(thumbnail);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*Integer idCloud = cursor.getInt(idCloudIndex);
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
