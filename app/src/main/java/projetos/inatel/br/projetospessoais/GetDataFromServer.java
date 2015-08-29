package projetos.inatel.br.projetospessoais;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import projetos.inatel.br.projetospessoais.model.Project;


public class GetDataFromServer extends Service {
    public GetDataFromServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        RefreshThread refreshTread = new RefreshThread();
        refreshTread.run();

        try {
            refreshTread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class RefreshThread extends Thread {

        public void run(){
            String httpResponse = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://ckilee.esy.es/project");
            HttpResponse response = null;
            try {
                response = httpclient.execute(httpget);

                httpResponse = new BasicResponseHandler().handleResponse(response);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ArrayList<Project>>() {
            }.getType();
            List<Project> projectList = gson.fromJson(httpResponse, listType);
            for(Project curProject : projectList){
                Log.i("GetDataFromServer",curProject.toString());
            }
        }
    }
}
