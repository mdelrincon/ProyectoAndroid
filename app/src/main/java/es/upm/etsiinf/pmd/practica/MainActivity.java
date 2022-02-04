package es.upm.etsiinf.pmd.practica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.upm.etsiinf.pmd.practica.activities.CreateArticle_act;
import es.upm.etsiinf.pmd.practica.activities.DetalleArticulo_act;
import es.upm.etsiinf.pmd.practica.activities.Login_act;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.notificaciones.NotificationHandler;
import es.upm.etsiinf.pmd.practica.services.LlamadaConnActualizacion;
import es.upm.etsiinf.pmd.practica.tasks.CategoriaEspecificaArticleListThread;
import es.upm.etsiinf.pmd.practica.tasks.CRUD.CreateArticle;
import es.upm.etsiinf.pmd.practica.tasks.InsertArticleBBDD;
import es.upm.etsiinf.pmd.practica.tasks.PantallaPrincipalArticleListThread;
import es.upm.etsiinf.pmd.practica.tasks.PostLogin;
import es.upm.etsiinf.pmd.practica.tasks.Actualizacion;
import es.upm.etsiinf.pmd.practica.tasks.RellenarTablaPrimeraVezThread;
import es.upm.etsiinf.pmd.practica.util.BDUtils;
import es.upm.etsiinf.pmd.practica.util.ConexionSQLiteHelper;
import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;
import es.upm.etsiinf.pmd.practica.util.GetUrl;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_EDITADO=1,REQUEST_CODE_ELIMINAR=2, REQUEST_CODE_LOGIN=3, REQUEST_CODE_CREATE_ARTICLE=4;

    //{"username": "GR_25",
    //"passwd": "294850"}


    private Activity act=null;
    private static final String NOMBRE_ATR_PREF_USERNAME= "api_token"
            , NOMBRE_FICHERO_PREFS_CONEXION="pref_conexion"
            ,   LAST_UPDATE_DATE = "last_update_date";
    ;
    private String apikey="";
    private String authorithation="";
    private SharedPreferences misPreferencias;

    // Los botones flotantes
    private FloatingActionButton btn_menu, btn_iniciar_sesion, btn_cerrar_sesion, btn_crear_articulo,btn_ver_articulos;

    // These are taken to make visible and invisible along
    // with FABs
    private TextView txt_iniciar_sesion,txt_cerrar_sesion, txt_crear_articulo, txt_ver_articulos;

    // para saber si los botones del floatingbutton estan visibles o no
    private Boolean botones_visibles;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act=this;
        misPreferencias=getSharedPreferences(NOMBRE_FICHERO_PREFS_CONEXION,Context.MODE_PRIVATE);
        Toolbar toolbar= findViewById(R.id.barra_herramientas);
        setSupportActionBar(toolbar);
        //Creamos el servicio de las actualizaciones del servidor
        Actualizacion.schedule(this);
        ConexionSQLiteHelper conn= new ConexionSQLiteHelper(this);
        //Para borrar bases de datos
        //this.deleteDatabase("db_articulos_final");
        File path_db=this.getDatabasePath("db_articulos_def");
        //Obtenemos los articulos y los guardamos en la base de datos
        if(!path_db.exists()) {
            RellenarTablaPrimeraVezThread rellenarTablaPrimeraVezThread = new RellenarTablaPrimeraVezThread(this);
            Thread thread = new Thread(rellenarTablaPrimeraVezThread);
            thread.start();
            try {
                thread.join();
                pantallaprincipal("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            //Recargamos tod0 desde la ultima vez
            String last_update= misPreferencias.getString(act.getResources().getString(R.string.LAST_UPDATE_DATE),null);
            LlamadaConnActualizacion llamadaConnActualizacion= new LlamadaConnActualizacion(last_update);
            ExecutorService es = Executors.newSingleThreadExecutor ();
            String response = "";
            try {
                response=es.submit(llamadaConnActualizacion).get();
                //miramos si hay articulos nuevos
                if(!response.equals("[]")){
                    addLocalTable(response);
                }
            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }
            pantallaprincipal("");
        }

    }

    private void addLocalTable(String response) throws JSONException {
        JSONArray articulos= new JSONArray(response);
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
        }
        //actualizamos la fecha de last_update
        String last_update_date= articulos.getJSONObject(0).getString("update_date");
        SharedPreferences.Editor editor= misPreferencias.edit();
        editor.putString(act.getResources().getString(R.string.LAST_UPDATE_DATE),last_update_date);
        editor.commit();
    }

    //mostramos el menu de la barra de categorias
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    //Acciones tras presionar en el tool bar
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        switch(id) {
            case R.id.Nacional:
                pantallaprincipal("Nacional");
                return true;

            case R.id.Economia:
                pantallaprincipal("Economia");
                return true;

            case R.id.Deportes:
                pantallaprincipal("Deportes");
                return true;

            case R.id.Tecnologia:
                pantallaprincipal("Tecnologia");
                return true;

            case R.id.Todas:
                pantallaprincipal("");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }




    //metodo de pantalla principal general
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pantallaprincipal(String categoria) {
        //botones flotantes y sus textos
        btn_menu= findViewById(R.id.f_btn_menu); //boton padre: menu
        btn_iniciar_sesion= findViewById(R.id.f_btn_login); //boton del logueo
        txt_iniciar_sesion=findViewById(R.id.txt_f_btn_login); //texto del boton de inicar sesion
        btn_cerrar_sesion=findViewById(R.id.f_btn_logout); //boton del logout
        txt_cerrar_sesion=findViewById(R.id.txt_f_btn_logout);
        btn_crear_articulo=findViewById(R.id.f_btn_crear_articulo);
        txt_crear_articulo=findViewById(R.id.txt_f_btn_crear_articulo);
        btn_ver_articulos=findViewById(R.id.f_btn_ver_articulos);
        txt_ver_articulos=findViewById(R.id.txt_f_btn_ver_articulos);

        //establecemos la visibilidad
        btn_iniciar_sesion.setVisibility(View.GONE);
        txt_iniciar_sesion.setVisibility(View.GONE);
        btn_crear_articulo.setVisibility(View.GONE);
        txt_crear_articulo.setVisibility(View.GONE);
        btn_cerrar_sesion.setVisibility(View.GONE);
        txt_cerrar_sesion.setVisibility(View.GONE);
        btn_ver_articulos.setVisibility(View.GONE);
        txt_ver_articulos.setVisibility(View.GONE);

        //los botones no visibles
        botones_visibles=false;

        //miramos si tiene iniciada la sesion o no
        String apikeyShared=misPreferencias.getString(NOMBRE_ATR_PREF_USERNAME,null);

        //establecemos el funcionamiento del boton floatin del menu
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si no estan visibles entonces desplegamos los botones y sus respectivos textos
                String prueba= getApikey();
                if(!botones_visibles) {
                    if ((getApikey() == null || getApikey().equals("")) && (apikeyShared == null || apikeyShared.equals("") ) ) {
                        btn_iniciar_sesion.show();
                        txt_iniciar_sesion.setVisibility(View.VISIBLE);
                    }
                    else {
                        btn_cerrar_sesion.show();
                        txt_cerrar_sesion.setVisibility(View.VISIBLE);
                        btn_crear_articulo.show();
                        txt_crear_articulo.setVisibility(View.VISIBLE);
                        btn_ver_articulos.show();
                        txt_ver_articulos.setVisibility(View.VISIBLE);
                    }
                    botones_visibles=true;
                }
                //si estaban visibles entonces los volvemos a ocultar
                else {
                    if ((apikey == null || apikey.equals("")) && (apikeyShared == null || apikeyShared.equals("") ) ) {
                        btn_iniciar_sesion.hide();
                        txt_iniciar_sesion.setVisibility(View.GONE);
                    }
                    else {
                        btn_cerrar_sesion.hide();
                        txt_cerrar_sesion.setVisibility(View.GONE);
                        btn_crear_articulo.hide();
                        txt_crear_articulo.setVisibility(View.GONE);
                        btn_ver_articulos.hide();
                        txt_ver_articulos.setVisibility(View.GONE);
                    }
                    botones_visibles=false;
                }
            }
        });
        //fin del boton de menu

        //ahora establecemos el funcionamiento del boton de iniciar sesion
        btn_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_iniciar_sesion.hide();
                txt_iniciar_sesion.setVisibility(View.GONE);
                Intent intent = new Intent(act, Login_act.class);
                act.startActivityForResult(intent,REQUEST_CODE_LOGIN);
            }
        });
        //fin del boton de iniciar sesion


        //funcionamiento del boton de logout
        btn_cerrar_sesion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btn_cerrar_sesion.hide();
                txt_cerrar_sesion.setVisibility(View.GONE);
                btn_crear_articulo.hide();
                txt_crear_articulo.setVisibility(View.GONE);
                btn_ver_articulos.hide();
                txt_ver_articulos.setVisibility(View.GONE);
                String apikey_now="";
                apikey="";
                SharedPreferences.Editor editor= misPreferencias.edit();
                editor.putString(NOMBRE_ATR_PREF_USERNAME,apikey_now);
                editor.putString(act.getResources().getString(R.string.TEMPORAL_SESION),apikey_now);
                editor.commit();
                Toast.makeText(act, "Has cerrado sesión correctamente", Toast.LENGTH_SHORT).show();
                pantallaprincipal("");
            }
        });
        //fin del funcionamiento del boton de logout

        //funcionamiento del boton de crear articulo
        btn_crear_articulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, CreateArticle_act.class);
                intent.putExtra("auth",getAuthorithation());
                String apikey_send;
                if (getApikey()==null || getApikey().equals("")) {
                    apikey_send=getApikeyShared();
                }
                else {
                    apikey_send=getApikey();
                }
                intent.putExtra("api",apikey_send);
                act.startActivityForResult(intent,REQUEST_CODE_CREATE_ARTICLE);
            }
        });

        //fin del funcionamiento del boton de crear articulo

        //funcionamiento del boton de ver articulos
        btn_ver_articulos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pantallaprincipal("mios");
            }
        });

        //fin del funcionamiento del boton de ver tus articulos

        //Lista de articulos
        //caso en el que no ha elegido ninguna categoria o ha elegito todas
        if (categoria.equals("")){
            String apiSend="";
            if (getApikey().equals("")) {
                if(!getApikeyShared().equals("")){
                    apiSend=getApikeyShared();
                }
            }
            else {
                apiSend=getApikey();
            }
            PantallaPrincipalArticleListThread lista_articulos= new PantallaPrincipalArticleListThread(this,"https://sanger.dia.fi.upm.es/pmd-task/articles/200/0",apiSend,getAuthorithation());
            Thread thread= new Thread(lista_articulos);
            thread.start();
        }
        //caso en el que ha elegido una categoria concreta
        else {
            String apiSend="";
            if (getApikey().equals("")) {
                if(!getApikeyShared().equals("")){
                    apiSend=getApikeyShared();
                }
            }
            else {
                apiSend=getApikey();
            }
            CategoriaEspecificaArticleListThread lista_articulos= new CategoriaEspecificaArticleListThread(this,"https://sanger.dia.fi.upm.es/pmd-task/articles/2000/0",categoria,apiSend,getAuthorithation());
            Thread thread= new Thread(lista_articulos);
            thread.start();
            //Metemos en las preferencias la fecha de del ultimo articulo subido

        }

        //guardamos ultima fecha de actualizacion para el servicio que revisa si hay actualizaciones
        return;

    }
    //fin de pantalla principal



    //Acciones dependiendo de los intents que hayan terminado
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Si termina el intent del login
        if(requestCode==REQUEST_CODE_LOGIN) {
            if(resultCode==0){
                String apikey_response=data.getStringExtra("apikey");
                String authorithathion=data.getStringExtra("auth");
                String remember=data.getStringExtra("casilla_recuerdame");
                String username=data.getStringExtra("username");
                //opcion de recuerdame
                if (remember.equals("true")) {
                    //guardamos credenciales
                    SharedPreferences.Editor editor = misPreferencias.edit();
                    editor.putString(NOMBRE_ATR_PREF_USERNAME, apikey_response);
                    editor.putString(ACCOUNT_SERVICE, authorithathion);
                    editor.putString(act.getResources().getString(R.string.TEMPORAL_SESION), apikey_response);
                    editor.putString(act.getResources().getString(R.string.nombreUsuario),username);
                    editor.putString(act.getResources().getString(R.string.TEMPORAL_AUTHORITHATHION),authorithathion);
                    editor.commit();
                }
                //sin recordar
                else {
                    SharedPreferences.Editor editor = misPreferencias.edit();
                    editor.putString(act.getResources().getString(R.string.TEMPORAL_SESION), apikey_response);
                    editor.putString(act.getResources().getString(R.string.TEMPORAL_AUTHORITHATHION),authorithathion);
                    editor.putString(act.getResources().getString(R.string.nombreUsuario),username);
                    editor.commit();
                }
                setApikey(misPreferencias.getString(act.getResources().getString(R.string.TEMPORAL_SESION), null));
                setAuthorithation(misPreferencias.getString(act.getResources().getString(R.string.TEMPORAL_AUTHORITHATHION), null));
                pantallaprincipal("");
            }

        }
        //Si termina el intent de editar un articulo
        else if( requestCode==REQUEST_CODE_EDITADO ) {
            if(resultCode==0) {
                SharedPreferences.Editor editor = misPreferencias.edit();
                //ponemos la fecha nueva del articulo para que no salte la notificacion
                String last_update_date= data.getStringExtra("fecha");
                editor.putString(getApplicationContext().getResources().getString(R.string.LAST_UPDATE_DATE),last_update_date);
                pantallaprincipal("");
            }
        }
        //si termina el intent de eliminar y crear un articulo
        else if(requestCode==REQUEST_CODE_ELIMINAR || requestCode==REQUEST_CODE_CREATE_ARTICLE) {
            pantallaprincipal("");
        }


    }
    //fin funciones que cargan imagenes



    //para la apikey y la authoritation
    public void setApikey(String apikey) {
        this.apikey=apikey;
    }

    public String getApikey() {
        return this.apikey;
    }

    public String getApikeyShared(){
        return misPreferencias.getString(NOMBRE_ATR_PREF_USERNAME,null);
    }

    public String getAuthorithation() {
        if(this.authorithation.equals("")) {
             return misPreferencias.getString(ACCOUNT_SERVICE,null);
        }
        else {
            return this.authorithation;
        }
    }


    public void setAuthorithation(String authorithation) {
        this.authorithation = authorithation;
    }



}