package projetos.inatel.br.projetospessoais;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                //insertIntoDatabase();
                new SendToWeb().execute();
                finish();

            }
        };
        btnSave.setOnClickListener(listenerBtnSave);
    }

    /*
    private void sendDataToWeb(){
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://ckilee.esy.es/project");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("name",nameEditText.getText().toString() ));
            nameValuePairs.add(new BasicNameValuePair("description", descriptionEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("owner", "Carlos"));
            nameValuePairs.add(new BasicNameValuePair("creation_date", String.valueOf((new Date()).getTime())));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }*/

    private void insertIntoDatabase(String projectId){
        int projectIdInteger = Integer.parseInt(projectId);
        Project project = new Project();
        project.setCreationDate(Long.toString(new Date().getTime()));
        project.setDescription(descriptionEditText.getText().toString());
        project.setName(nameEditText.getText().toString());
        project.setOwner("Carlos");
        project.setId(projectIdInteger);
        projectDAO.addProject(project);

        for(ImageViewContainer imageViewContainer : imageViewContainerList){
            Image image = new Image();
            image.setDescription(imageViewContainer.getEditText().getText().toString());
            image.setCreationDate(Long.toString(new Date().getTime()));
            image.setImage(imageViewContainer.getImageName());
            image.setProjectId(projectIdInteger);
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
                    imageViewContainer.setImageName(fileUri.getLastPathSegment().replace(".jpg", ".png"));
                    imageViewContainer.setCompletePath(fileUri.getPath().replace(".jpg",".png"));
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

        private String completePath = "";

        public ImageViewContainer(){

        }

        public ImageViewContainer(EditText editText, ImageView imageView){
            this.editText = editText;
            this.imageView = imageView;
        }

        public String getCompletePath() {
            return completePath;
        }

        public void setCompletePath(String completePath) {
            this.completePath = completePath;
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

    private class SendToWeb extends AsyncTask<String, Void, String>{
        private String curProjectId = "";
        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://ckilee.esy.es/project");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name",nameEditText.getText().toString() ));
                nameValuePairs.add(new BasicNameValuePair("description", descriptionEditText.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("owner", "Carlos"));
                nameValuePairs.add(new BasicNameValuePair("creationDate", Long.toString(new Date().getTime())));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                curProjectId = new BasicResponseHandler().handleResponse(response);
                curProjectId = curProjectId.replace("\"","");

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            for(ImageViewContainer imageViewContainer : imageViewContainerList) {
                // Create a new HttpClient and Post Header
                httpclient = new DefaultHttpClient();
                httppost = new HttpPost("http://ckilee.esy.es/image");
                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("projectId", curProjectId));
                    nameValuePairs.add(new BasicNameValuePair("image", imageViewContainer.getImageName()));
                    nameValuePairs.add(new BasicNameValuePair("creationDate", Long.toString(new Date().getTime())));
                    nameValuePairs.add(new BasicNameValuePair("description", imageViewContainer.getEditText().getText().toString()));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }

                uploadFile(imageViewContainer.getCompletePath());
            }

            return curProjectId;
        }

        @Override
        protected void onPostExecute(String projectId) {
            insertIntoDatabase(projectId);
        }

        public int uploadFile(String sourceFileUri) {
            String upLoadServerUri = "http://ckilee.esy.es/UploadToServer.php";

            String fileName = sourceFileUri;

            int serverResponseCode = 0;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {

                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });

                return 0;

            }
            else
            {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();


                    if(serverResponseCode == 200){

                        runOnUiThread(new Runnable() {
                            public void run() {
                                /*
                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                        +" http://www.androidexample.com/media/uploads/"
                                        +uploadFileName;*/

                                Toast.makeText(EditProjectActivity.this, "File Upload Complete.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {


                    ex.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EditProjectActivity.this, "MalformedURLException",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EditProjectActivity.this, "Got Exception : see logcat ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    /*Log.e("Upload file to server Exception", "Exception : "
                            + e.getMessage(), e);*/
                }

                return serverResponseCode;

            } // End else block
        }
    }
}
