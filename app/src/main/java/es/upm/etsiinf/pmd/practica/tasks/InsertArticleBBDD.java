package es.upm.etsiinf.pmd.practica.tasks;

import android.app.Activity;

import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.util.BDUtils;
import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;
import es.upm.etsiinf.pmd.practica.util.GetUrl;

public class InsertArticleBBDD implements Runnable {


    private Activity act;
    private int id;
    public InsertArticleBBDD(Activity act,int id) {
        this.act=act;
        this.id=id;
    }

    @Override
    public void run() {
        try {
            Article nuevo_article= GetUrl.getArticle(act.getResources().getString(R.string.URL_BASE_GET_ARTICULO),id);
            if(nuevo_article.getThumbnail_image()==null || nuevo_article.getThumbnail_image().equals("")) {
                nuevo_article.setThumbnail_image(nuevo_article.getImage_data());
            }
            BDUtils.registrarArticuloNot(nuevo_article,act);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
