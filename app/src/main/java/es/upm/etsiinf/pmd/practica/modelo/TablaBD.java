package es.upm.etsiinf.pmd.practica.modelo;

public class TablaBD {
    private int id;
    private String abstracto;
    private String body;
    private String subtitle;
    private String category;
    private String title;
    private String update_date;
    private String image_data;
    private String username;

    public TablaBD(int id, String abstracto, String body, String subtitle, String category, String title, String update_date, String image_data, String username) {
        this.id = id;
        this.abstracto = abstracto;
        this.body = body;
        this.subtitle = subtitle;
        this.category = category;
        this.title = title;
        this.update_date = update_date;
        this.image_data = image_data;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAbstracto() {
        return abstracto;
    }

    public void setAbstracto(String abstracto) {
        this.abstracto = abstracto;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getImage_data() {
        return image_data;
    }

    public void setImage_data(String image_data) {
        this.image_data = image_data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
