package es.upm.etsiinf.pmd.practica.modelo;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;

public class Article implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("abstract")
    private String abstracto;
    @SerializedName("body")
    private String body;
    @SerializedName("subtitle")
    private String subtitle;
    @SerializedName("category")
    private String category;
    @SerializedName("title")
    private String title;
    @SerializedName("update_date")
    private String update_date;
    @SerializedName("image_data")
    private String image_data;
    @SerializedName("username")
    private String username;
    @SerializedName("thumbnail_image")
    private String thumbnail_image;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public Article (String title, String subtitle, String category, ImageView image_data, String cuerpo,String abstracto) {
        this.title=title;
        this.subtitle=subtitle;
        this.category=category;
        image_data.buildDrawingCache();
        Bitmap bmap = image_data.getDrawingCache();
        this.image_data=ConvertidorImags.imgToBase64String(bmap);
        this.body=cuerpo;
        this.abstracto=abstracto;
    }

    public Article(@Nullable Integer id, @Nullable String abstracto,@Nullable String body,@Nullable String subtitle,@Nullable String category,
                   @Nullable String title,@Nullable String update_date,@Nullable String image_data,@Nullable String username,@Nullable String thumbnail_image){
            this.id=id;
            this.abstracto=abstracto;
            this.body=body;
            this.subtitle=subtitle;
            this.category=category;
            this.title=title;
            this.update_date=update_date;
            this.image_data=image_data;
            this.username=username;
            this.thumbnail_image=thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }



    private transient Bitmap bmp;

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAbstracto() {
        return abstracto;
    }

    public void setAbstracto(String abstracto) {
        this.abstracto = abstracto;
    }


}
