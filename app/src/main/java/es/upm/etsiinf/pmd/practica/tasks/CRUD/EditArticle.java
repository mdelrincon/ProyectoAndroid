package es.upm.etsiinf.pmd.practica.tasks.CRUD;

import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.util.connArticle;

public class EditArticle implements Callable<String> {

    private Article article;
    private String apikey;
    private String auth;
    private int id;

    public EditArticle(Article articulo, String apikey, String authorithation, int id) {
        this.article=articulo;
        this.apikey=apikey;
        this.auth=authorithation;
        this.id=id;
    }

    @Override
    public String call() throws Exception {
        //cuando le den al boton de crear articulo lo creamos
        String response="";
        try {
            connArticle connArticle = new connArticle();
            response= connArticle.updateArticle(article,apikey,auth,id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}