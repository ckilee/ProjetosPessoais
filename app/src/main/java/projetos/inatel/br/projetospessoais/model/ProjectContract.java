package projetos.inatel.br.projetospessoais.model;

import android.provider.BaseColumns;

/**
 * Created by ckilee on 16/08/15.
 */
public class ProjectContract {
    // Constantes do banco
    public static final String DB_NAME = "project.db";
    public static final int DB_VERSION = 1;
    public static final String PROJECT_TABLE = "PROJECT";
    public static final String IMAGE_TABLE = "IMAGE";

    public static final String DEFAULT_SORT = Column.CREATION_DATE + " DESC";
    public static final String[] PROJECT_COLUMN_NAMES = {
            Column.ID, Column.NAME, Column.DESCRIPTION, Column.OWNER, Column.CREATION_DATE};

    public static final String[] IMAGE_COLUMN_NAMES = {
            Column.ID, Column.PROJECT_FOREIGN_ID, Column.IMAGE, Column.DESCRIPTION, Column.CREATION_DATE};

    public class Column {
        //common
        public static final String ID = BaseColumns._ID;
        public static final String DESCRIPTION = "DESCRIPTION";

        //Project table
        public static final String NAME = "NAME";
        public static final String OWNER = "OWNER";
        public static final String CREATION_DATE = "CREATION_DATE";

        //Image table
        public static final String PROJECT_FOREIGN_ID = "PROJECTID";
        public static final String IMAGE = "IMAGE";

    }

}
