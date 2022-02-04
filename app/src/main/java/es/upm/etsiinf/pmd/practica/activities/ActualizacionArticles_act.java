package es.upm.etsiinf.pmd.practica.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.notificaciones.NotificationHandler;
import es.upm.etsiinf.pmd.practica.services.LlamadaConnActualizacion;
import es.upm.etsiinf.pmd.practica.tasks.Actualizacion;
import es.upm.etsiinf.pmd.practica.util.BDUtils;
import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;
import es.upm.etsiinf.pmd.practica.util.connActualizacion;

public class ActualizacionArticles_act  extends Service {


    //para las notificaciones
    NotificationHandler handler;
    int contador;

   @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
       /* //Realizamos la busqueda de nuevos articulos
        //a partir de la fecha last_update
       SharedPreferences preferences=getApplicationContext().getSharedPreferences(getApplicationContext().getResources().getString(R.string.NOMBRE_FICHERO_PREFS_CONEXION), Context.MODE_PRIVATE);
        String response = "";

       handler = new NotificationHandler(this);

        try {
            String last_update= preferences.getString(getApplicationContext().getResources().getString(R.string.LAST_UPDATE_DATE),null);
            LlamadaConnActualizacion llamadaConnActualizacion= new LlamadaConnActualizacion(last_update);
            ExecutorService es = Executors.newSingleThreadExecutor ();
            response=es.submit(llamadaConnActualizacion).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //si no hay actualizacion no hacemos nada
        if (response.equals("[]")) {
            return ;
        }
        //si hay
        else {
            //dar funcionamiento a la notificacion
            try {
                JSONArray respuesta= new JSONArray(response);
                //actualizamos la fecha de last_update
                //rellenamos la base de datos con el nuevo articulo
                addBDandSendNotification(respuesta,true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }*/
       return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Realizamos la busqueda de nuevos articulos
        //cuando le den al boton de crear articulo lo creamos
        SharedPreferences preferences=getApplicationContext().getSharedPreferences(getApplicationContext().getResources().getString(R.string.NOMBRE_FICHERO_PREFS_CONEXION), Context.MODE_PRIVATE);
        String response = "";
        handler = new NotificationHandler(this);
        try {

            String last_update= preferences.getString(getApplicationContext().getResources().getString(R.string.LAST_UPDATE_DATE),null);
            LlamadaConnActualizacion llamadaConnActualizacion= new LlamadaConnActualizacion(last_update);
            ExecutorService es = Executors.newSingleThreadExecutor ();
            response=es.submit(llamadaConnActualizacion).get();
            //response = connActualizacion.getActualizacion(last_update);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //si no hay actualizacion no hacemos nada
        if (response.equals("[]")) {
            return 0;
        }
        //si hay
        else {
            //dar funcionamiento a la notificacion
            try {
                JSONArray respuesta= new JSONArray(response);
                //actualizamos la fecha de last_update
                //rellenamos la base de datos con el nuevo articulo y enviamos notificacion
                addBDandSendNotification(respuesta,true);
                return 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return 0;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addBDandSendNotification (JSONArray articulos, boolean prioridad) throws JSONException {
       //recorremos todos los articulos nuevos
        handler.createGroup(true);
        for (int i=0;i<articulos.length();i++) {
            JSONObject articulo=articulos.getJSONObject(i);
            Article article = null;
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String tostring= articulo.toString();
            try {
                article = gson.fromJson(tostring, Article.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            BDUtils bdUtils= new BDUtils();
            bdUtils.registrarArticuloNot(article, getApplicationContext());
            //por cada articulo una notificacion
            String imagen=articulo.getString("thumbnail_image");
            ConvertidorImags convertidorImags= new ConvertidorImags();
            Bitmap image=convertidorImags.base64StringToImg(imagen);
            Notification.Builder res;
            String titulo=articulo.getString("title")+":"+articulo.getString("subtitle");
            res = handler.createNotification(titulo, image, prioridad);
            handler.getManager().notify(contador++,res.build());
        }
        //actualizamos la fecha de last_update
        String last_update_date= articulos.getJSONObject(0).getString("update_date");
        SharedPreferences preferences=getApplicationContext().getSharedPreferences(getApplicationContext().getResources().getString(R.string.NOMBRE_FICHERO_PREFS_CONEXION), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString(getApplicationContext().getResources().getString(R.string.LAST_UPDATE_DATE),last_update_date);
        editor.commit();
    }



}
