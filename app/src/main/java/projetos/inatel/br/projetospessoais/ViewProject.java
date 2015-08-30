package projetos.inatel.br.projetospessoais;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import projetos.inatel.br.projetospessoais.model.ImageCursorAdapter;
import projetos.inatel.br.projetospessoais.model.Project;
import projetos.inatel.br.projetospessoais.model.ProjectDAO;


public class ViewProject extends Activity {
    private int projectId;
    private Project project;
    private ProjectDAO projectDAO;
    private ImageCursorAdapter novoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
        projectDAO = new ProjectDAO(this.getBaseContext());
        projectId = this.getIntent().getIntExtra("projectId",-1);
        project = projectDAO.getProject(projectId);
        configureProjectInformation();
        configureListView();

    }

    private void configureProjectInformation() {
        TextView projectNameTV = (TextView)super.findViewById(R.id.project_name_view);
        projectNameTV.setText(project.getName());

        TextView descriptionTV = (TextView)super.findViewById(R.id.description_tv);
        descriptionTV.setText(project.getDescription());

    }

    private void configureListView() {
        ListView lstProject = (ListView) super.findViewById(R.id.imagesListView);
        Cursor cursor = projectDAO.getAllImageAsCursor(Integer.toString(projectId));
        novoAdapter = new ImageCursorAdapter(this, cursor);

        // configura o ViewBinder do adaptador do ListView
        //novoAdapter.setViewBinder(new ProjectViewBinder());
        lstProject.setAdapter(novoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_project, menu);
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
}
