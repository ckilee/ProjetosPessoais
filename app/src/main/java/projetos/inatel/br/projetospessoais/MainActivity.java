package projetos.inatel.br.projetospessoais;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import projetos.inatel.br.projetospessoais.model.ProjectCursorAdapter;
import projetos.inatel.br.projetospessoais.model.ProjectDAO;


public class MainActivity extends Activity {
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
    }

    private void startEditProjectActivity(){
        Intent intent = new Intent(this,EditProjectActivity.class);
        startActivity(intent);
    }
}
