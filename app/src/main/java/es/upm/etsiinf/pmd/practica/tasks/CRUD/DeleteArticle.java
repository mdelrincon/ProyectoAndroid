package es.upm.etsiinf.pmd.practica.tasks.CRUD;

import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.util.connArticle;

public class DeleteArticle implements Callable<String> {

    private int id;
    private String apikey;
    private String auth;

    public DeleteArticle(int id, String apikey, String authorithation) {
        this.id=id;
        this.apikey=apikey;
        this.auth=authorithation;
    }

    @Override
    public String call() throws Exception {
        //cuando le den al boton de crear articulo lo creamos
        String response="";
        try {
            connArticle connArticle = new connArticle();
            response= connArticle.deleteArticle(id,apikey,auth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
