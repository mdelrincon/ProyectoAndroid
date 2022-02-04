package es.upm.etsiinf.pmd.practica.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.modelo.ArticleAdapter;
import es.upm.etsiinf.pmd.practica.util.BDUtils;
import es.upm.etsiinf.pmd.practica.util.GetUrl;

public class CategoriaEspecificaArticleListThread  implements Runnable {
    private Activity activity;
    private String url;
    private String categoria;
    private String apikey;
    private String auth;

    public CategoriaEspecificaArticleListThread(Activity activity, String url, String categoria, String apikey,String auth ) {
        this.activity=activity; //actividad para mandarle todoo a la pantalla principal
        this.url=url;  //url para descargarnos todoo
        this.categoria=categoria; //categoria especifica que queremos enseñar
        this.apikey=apikey;
        this.auth=auth;
    }



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

        //establecer metodo para que solo muestre los tuyos


        List<Article> array_articulos=null;
        List<Article> lista_articulos= new LinkedList<>();
        try {
            //Realizamos la query a la base de datos
            array_articulos=BDUtils.rellenarListaBD(activity);

            //nos quedamos solo con los que sean  de la categoría específica
            //obtenemos las imagenes a traves de otro thread para que se vayan mostrando las que ya tengamos
            for(Article article : array_articulos) {
                //vemos si quiere ver sus propios articulos
                if (categoria.equals("mios")) {
                    if(article.getUsername().equals(activity.getResources().getString(R.string.username))) {
                        lista_articulos.add(article);
                        DescargaImagenThread descargaImagenThread= new DescargaImagenThread(activity,article);
                        Thread thread= new Thread(descargaImagenThread);
                        thread.start();
                    }
                    else {
                        continue;
                    }
                }
                String categoria_articulo=article.getCategory();
                if(categoria_articulo == null) {
                    categoria_articulo="";
                }
                else if (categoria_articulo.equals("National")) {
                    categoria_articulo="Nacional";
                }
                else if(categoria_articulo.equals("Sports")) {
                    categoria_articulo="Deportes";
                }
                else if(categoria_articulo.equals("Economía") || categoria_articulo.equals("Economy")) {
                    categoria_articulo="Economia";
                }
                else if(categoria_articulo.equals("Tecnología") || categoria_articulo.equals("Technology")) {
                    categoria_articulo="Tecnologia";
                }

                if(categoria_articulo.equals(categoria)) {
                    lista_articulos.add(article);
                    BDUtils.registrarArticuloBD(article, activity);
                    DescargaImagenThread descargaImagenThread= new DescargaImagenThread(activity,article);
                    Thread thread= new Thread(descargaImagenThread);
                    thread.start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        //Actualizacion grafica despues de la descarga
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Obtenemos la lista
                ListView listView= activity.findViewById(R.id.list);
                //Inicializamos el adaptador de los articulos
                ArticleAdapter articleAdapter = new ArticleAdapter(apikey,activity,auth);
                //Le pasamos la lista de articulos que queremos pintar
                articleAdapter.setArticles(lista_articulos);
                //Lo metemos en el listview
                listView.setAdapter(articleAdapter);
                //Se lo mandamos al activity
                activity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }
        });

        //actualizamos ultima noticia si hemos dado a todas
        if(this.categoria.equals("")) {
            SharedPreferences preferences = activity.getSharedPreferences(activity.getResources().getString(R.string.NOMBRE_FICHERO_PREFS_CONEXION), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            //String last_update= preferences.getString(activity.getResources().getString(R.string.LAST_UPDATE_DATE),null);
            editor.putString(activity.getResources().getString(R.string.LAST_UPDATE_DATE), lista_articulos.get(0).getUpdate_date());
            editor.commit();
        }
        return;
    }

}
