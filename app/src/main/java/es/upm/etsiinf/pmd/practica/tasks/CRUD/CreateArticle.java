package es.upm.etsiinf.pmd.practica.tasks.CRUD;

import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.util.connArticle;

public class CreateArticle implements Callable<String> {

    private Article article;
    private String apikey;
    private String auth;

    public CreateArticle(Article articulo, String apikey,String authorithation) {
        this.article=articulo;
        this.apikey=apikey;
        this.auth=authorithation;
    }

    @Override
    public String call() throws Exception {
        //cuando le den al boton de crear articulo lo creamos
        String response="";
        try {
            connArticle connArticle = new connArticle();
            response= connArticle.postArticle(article,apikey,auth);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
