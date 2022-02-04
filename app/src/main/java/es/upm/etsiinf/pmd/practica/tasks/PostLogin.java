package es.upm.etsiinf.pmd.practica.tasks;

import android.app.Activity;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.util.GetUrl;

public class PostLogin implements Callable<String> {

    private Activity act;
    private JSONObject json;

    public PostLogin(Activity act,JSONObject json) {
        this.act=act;
        this.json=json;
    }

    @Override
    public String call() throws Exception {
        //cuando le den al boton de logear cogemos el usuario y la contraseña para llamar a la api
        String response="";
        try {
            GetUrl post= new GetUrl();
            response=post.postURL(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
