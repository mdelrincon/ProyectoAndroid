package es.upm.etsiinf.pmd.practica.tasks;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.util.BDUtils;
import es.upm.etsiinf.pmd.practica.util.GetUrl;

public class RellenarTablaPrimeraVezThread implements Runnable {

    private String url="https://sanger.dia.fi.upm.es/pmd-task/articles/200/0";
    private Activity act;

    public RellenarTablaPrimeraVezThread(Activity act) {
        this.act=act;
    }


    @Override
    public void run() {
        //Realizamos la query a la base de datos
        Article[] array_articulos=getArticulos(url);
        List<Article> lista_articulos=new ArrayList<Article>();
        try {

            //obtenemos las imagenes a traves de otro thread para que se vayan mostrando las que ya tengamos

            for(Article article : array_articulos) {
                //lo añadimos a la lista de articulos
                lista_articulos.add(article);

                //lo añadimos a la base de datos
                BDUtils.registrarArticuloBD(article, act);
               /* DescargaImagenThread descargaImagenThread= new DescargaImagenThread(activity,article);
                Thread thread= new Thread(descargaImagenThread);
                thread.start();*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }


    public Article[] getArticulos(String url) {
        Article[] article = null;
        try {

            String res = GetUrl.getURLText(url);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            article = gson.fromJson(res, Article[].class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }

}
