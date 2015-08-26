package projetos.inatel.br.projetospessoais;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import projetos.inatel.br.projetospessoais.R;
import projetos.inatel.br.projetospessoais.model.Image;
import projetos.inatel.br.projetospessoais.model.Project;
import projetos.inatel.br.projetospessoais.model.ProjectDAO;

public class EditProjectActivity extends Activity {
    private int CAMERA_REQUEST = 0;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private int numberOfPictures = 0;
    private ProjectDAO projectDAO;
    private EditText descriptionEditText;
    private EditText nameEditText;
    private LinearLayout picturesTableLayout;
    private ArrayList<ImageViewContainer> imageViewContainerList;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        descriptionEditText = (EditText) super.findViewById(R.id.descriptionEditText);
        nameEditText = (EditText) super.findViewById(R.id.projectNameEditText);
        picturesTableLayout = (LinearLayout) super.findViewById(R.id.pictureLinearLayout);
        imageViewContainerList = new ArrayList<ImageViewContainer>();
        projectDAO = new ProjectDAO(getBaseContext());
        configureButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureButtons() {
        Button btnAddPhoto = (Button) super.findViewById(R.id.addPhotoBtn);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // String filePath = getFilesDir() +"/teste.jpeg";
                /*
                File file = new File( getFilesDir() +"/teste.jpeg");
                Uri output = Uri.fromFile(file);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        };
        btnAddPhoto.setOnClickListener(listener);


        Button btnSave = (Button) super.findViewById(R.id.saveBtn);
        View.OnClickListener listenerBtnSave = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                insertIntoDatabase();
                finish();

            }
        };
        btnSave.setOnClickListener(listenerBtnSave);
    }

    private void insertIntoDatabase(){

        Project project = new Project();
        project.setCreationDate(new Date());
        project.setDescription(descriptionEditText.getText().toString());
        project.setName(nameEditText.getText().toString());
        project.setOwner("Carlos");
        projectDAO.addProject(project);

        for(ImageViewContainer imageViewContainer : imageViewContainerList){
            Image image = new Image();
            image.setDescription(imageViewContainer.getEditText().getText().toString());
            image.setCreationDate(new Date());
            image.setImage(imageViewContainer.getImageName());
            image.setProjectId(project.getId());
            projectDAO.addImage(image);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                //Toast.makeText(this, "Image saved to:\n" +
                //        data.getData(), Toast.LENGTH_LONG).show();

                //String curPath = fileUri.getPath();
                //Bitmap bitmap = BitmapFactory.decodeFile(curPath);
                try {

                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri),300,300);
                    ImageViewContainer imageViewContainer = new ImageViewContainer();
                    ImageView iv = new ImageView(this);
                    iv.setImageBitmap(thumbnail);
                    iv.setAdjustViewBounds(true);
                    iv.setMaxHeight(400);
                    iv.setMaxWidth(500);

                    FileOutputStream out = new FileOutputStream(fileUri.getPath().replace(".jpg",".png"));
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                    File curFile = new File(fileUri.getPath());
                    curFile.delete();


                    EditText et = new EditText(this);
                    et.setHint("Add pictures comments here");

                    picturesTableLayout.addView(iv, numberOfPictures++);
                    picturesTableLayout.addView(et, numberOfPictures++);

                    imageViewContainer.setEditText(et);
                    imageViewContainer.setImageView(iv);
                    imageViewContainer.setImageName(fileUri.getLastPathSegment().replace(".jpg",".png"));
                    imageViewContainerList.add(imageViewContainer);

                } catch (Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private class ImageViewContainer{
        private EditText editText;
        private ImageView imageView;
        private String imageName = "";

        public ImageViewContainer(){

        }

        public ImageViewContainer(EditText editText, ImageView imageView){
            this.editText = editText;
            this.imageView = imageView;
        }


        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public EditText getEditText() {
            return editText;
        }

        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }




    }
}
