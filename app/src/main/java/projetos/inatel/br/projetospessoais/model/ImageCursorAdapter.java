package projetos.inatel.br.projetospessoais.model;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.io.IOException;

import projetos.inatel.br.projetospessoais.EditProjectActivity;
import projetos.inatel.br.projetospessoais.MainActivity;
import projetos.inatel.br.projetospessoais.R;

/**
 * Created by ckilee on 23/08/15.
 */
public class ImageCursorAdapter extends SimpleCursorAdapter{
    private int projectId = -1 ;
    public static final String TAG = ImageCursorAdapter.class.getSimpleName();
    // Campos do banco
    private static String[] fromProject = new String[]{ProjectContract.Column.DESCRIPTION};

    // Campos da UI
    private static int[] toProject = new int[]{R.id.item_description};

    private Cursor cursor;
    //private ImageRetriever ir;
    private int imageColumn;


    public ImageCursorAdapter(Context context, Cursor c) {
        super(context, R.layout.image_list_item, c, fromProject, toProject, 0);
        this.cursor = c;
        imageColumn = cursor.getColumnIndex(ProjectContract.Column.IMAGE);
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
        String fileName = cursor.getString(imageColumn);

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyCameraApp"+File.separator+fileName);
            Uri fileUri = Uri.fromFile(mediaStorageDir);
            try {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(MediaStore.Images.Media.getBitmap(MainActivity.getInstance().getContentResolver(), fileUri), 500, 500);
                if(thumbnail!=null)
                    imageView.setImageBitmap(thumbnail);
            } catch (IOException e) {
                e.printStackTrace();
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
