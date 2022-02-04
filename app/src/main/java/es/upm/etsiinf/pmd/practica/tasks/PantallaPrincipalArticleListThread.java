package es.upm.etsiinf.pmd.practica.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.MainActivity;
import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.modelo.ArticleAdapter;
import es.upm.etsiinf.pmd.practica.util.BDUtils;
import es.upm.etsiinf.pmd.practica.util.ConexionSQLiteHelper;
import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;
import es.upm.etsiinf.pmd.practica.util.GetUrl;

public class PantallaPrincipalArticleListThread implements Runnable {


    private Activity activity;
    private String url;
    private String apikey;
    private String auth;
    private ConexionSQLiteHelper conn;

    public PantallaPrincipalArticleListThread(Activity activity, String url,String apikey,String auth ) {
        this.activity=activity; //actividad para mandarle todoo a la pantalla principal
        this.url=url;  //url para descargarnos todoo
        this.apikey=apikey;
        this.auth=auth;
    }


    //establecer metodo para que solo muestre los tuyos
    @Override
    public void run() {
        //Actualizamos la interfaz grafica antes de descargarnos todoo para que salga el emoticono de en progreso
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar barra_progreso=activity.findViewById(R.id.progressBar);
                barra_progreso.setVisibility(View.VISIBLE);
            }
        });

        //Realizamos la query a la base de datos
        List<Article> lista_articulos=BDUtils.rellenarListaBD(activity);

        List<Article> finalLista_articulos = lista_articulos;
        try {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Obtenemos la lista
                ListView listView= activity.findViewById(R.id.list);
                //Inicializamos el adaptador de los articulos
                ArticleAdapter articleAdapter = new ArticleAdapter(apikey,activity,auth);
                //Le pasamos la lista de articulos que queremos pintar
                articleAdapter.setArticles(finalLista_articulos);
                //Lo metemos en el listview
                listView.setAdapter(articleAdapter);
                //Se lo mandamos al activity
                activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }
        });
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        //Actualizacion grafica despues de conseguir los datos
        for(Article article : finalLista_articulos) {
            DescargaImagenThread descargaImagenThread= new DescargaImagenThread(activity,article);
            Thread thread= new Thread(descargaImagenThread);
            thread.start();
        }


        //actualizamos la fecha del articulo mas nuevo
        SharedPreferences preferences=activity.getSharedPreferences(activity.getResources().getString(R.string.NOMBRE_FICHERO_PREFS_CONEXION), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString(activity.getResources().getString(R.string.LAST_UPDATE_DATE),lista_articulos.get(0).getUpdate_date());
        editor.commit();
        return;

    }

}
