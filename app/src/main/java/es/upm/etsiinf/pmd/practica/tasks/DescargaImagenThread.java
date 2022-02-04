package es.upm.etsiinf.pmd.practica.tasks;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.BaseAdapter;
import android.widget.ListView;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.modelo.ArticleAdapter;
import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;

public class DescargaImagenThread implements Runnable {

    private Activity activity;
    private Article article;

    public DescargaImagenThread(Activity activity, Article article) {
        this.activity=activity;
        this.article=article;
    }


    @Override
    public void run() {
        try {
            article.setBmp(ConvertidorImags.base64StringToImg(article.getThumbnail_image()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //obtenemos la lista
                BaseAdapter baseAdapter = (ArticleAdapter) ((ListView)activity.findViewById(R.id.list)).getAdapter();
                if (baseAdapter!=null) {
                    baseAdapter.notifyDataSetChanged(); //actualizamos la lista con la nueva imagen
                }
            }
        });
    }
}
