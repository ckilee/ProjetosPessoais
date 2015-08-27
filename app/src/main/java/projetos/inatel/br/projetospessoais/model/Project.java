package projetos.inatel.br.projetospessoais.model;

import java.util.Date;

/**
 * Created by ckilee on 16/08/15.
 */
public class Project {
    private int id = -1;
    private String name = "";
    private String description = "";
    private String owner = "";
    private Date creationDate;

    public Project(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Project Name:"+name+" Description:"+description+" Owner:"+owner;
    }
}
