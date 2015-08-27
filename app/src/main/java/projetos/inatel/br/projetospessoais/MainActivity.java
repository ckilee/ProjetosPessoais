package projetos.inatel.br.projetospessoais;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import projetos.inatel.br.projetospessoais.model.ProjectCursorAdapter;
import projetos.inatel.br.projetospessoais.model.ProjectDAO;


public class MainActivity extends Activity {
    private static MainActivity instance;
    public static MainActivity getInstance(){
        return instance;
    }
    public MainActivity(){
        instance = this;
    }

    private ProjectDAO projectDAO;
    private ProjectCursorAdapter novoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        projectDAO = new ProjectDAO(getApplicationContext());
        /*SQLiteDatabase db = projectDAO.getWritableDatabase();
        Intent intent = new Intent(this,EditProjectActivity.class);
        startActivity(intent);*/

        configureButtons();
        configureListView();


    }

    private void configureListView() {
        ListView lstProject = (ListView) super.findViewById(R.id.listProject);
        Cursor cursor = projectDAO.getAllProjectAsCursor();
        novoAdapter = new ProjectCursorAdapter(this, cursor);

        // configura o ViewBinder do adaptador do ListView
        novoAdapter.setViewBinder(new ProjectViewBinder());
        lstProject.setAdapter(novoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    class ProjectViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            switch (view.getId()) {
                case R.id.item_date:
                    long timestamp = cursor.getLong(columnIndex);
                    CharSequence data = DateUtils.getRelativeTimeSpanString(timestamp);
                    ((TextView) view).setText(data);
                    return true;
            }
            return false;
        }

    }

    private void configureButtons() {
        Button btnAddProject = (Button) super.findViewById(R.id.btn_add_project);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startEditProjectActivity();
            }
        };
        btnAddProject.setOnClickListener(listener);

        Button btnRefresh = (Button) super.findViewById(R.id.btn_refresh);
        View.OnClickListener listenerRefresh = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                refreshWithWebService();
            }
        };
        btnRefresh.setOnClickListener(listenerRefresh);
    }

    private void refreshWithWebService(){

    }

    private void startEditProjectActivity(){
        Intent intent = new Intent(this,EditProjectActivity.class);
        startActivity(intent);
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }



    private class SendToWeb extends AsyncTask<String, Void, String> {
        private String curProjectId = "";

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://ckilee.esy.es/project");

            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httpget);

                curProjectId = new BasicResponseHandler().handleResponse(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }


            curProjectId = curProjectId.replace("\"", "");
            return curProjectId;
        }
    }

}
