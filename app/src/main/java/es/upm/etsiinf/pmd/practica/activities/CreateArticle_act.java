package es.upm.etsiinf.pmd.practica.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.tasks.CRUD.CreateArticle;
import es.upm.etsiinf.pmd.practica.tasks.InsertArticleBBDD;

public class CreateArticle_act extends AppCompatActivity implements Serializable {


    private Activity act = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cambiamos de vista
        setContentView(R.layout.crear_articulo);
        this.act = this;
        Spinner spinner = (Spinner) findViewById(R.id.sp_ca_categorias);

        //Preparamos el spinner con las categorias para elegir
        //Creamos el array con las posibles categorias cerradas
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(act,
                R.array.categorias_array, android.R.layout.simple_spinner_item);
        //especificamos el layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //aplicamos el adaptador
        spinner.setAdapter(adapter);

        //Obtenemos la imagen que carguemos desde el movil
        Button btn_carga_imagen= findViewById(R.id.btn_ca_selecImage);
        btn_carga_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_image(); //llamamos a la funcion load image para cargar la imagen
            }
        });
        //Si el usuario cancela el articulo volvemos sin hacer nada
        Button cancelar= findViewById(R.id.btn_ca_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);
                finish();
            }
        });
        //Si el usuario crea el articulo, tiene que haber todas las casillas rellenadas
        Button crear_articulo= findViewById(R.id.btn_ca_crear);
        crear_articulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = ((EditText) act.findViewById(R.id.edit_txt_ca_titulo)).getText().toString();
                String subtitulo = ((EditText) act.findViewById(R.id.edit_txt_ca_subtitulo)).getText().toString();
                String resumen = ((EditText) act.findViewById(R.id.edit_txt_ca_resumen)).getText().toString();
                String categoria=spinner.getSelectedItem().toString();
                String cuerpo = ((EditText) act.findViewById(R.id.edit_txt_ca_cuerpo)).getText().toString();
                if (titulo.equals("") || subtitulo.equals("") || resumen.equals("") || cuerpo.equals("") || categoria.equals("Elige categoria") ) {
                    Toast.makeText(act,"Rellena todos los campos obligatorios y elige categoria para crear el articulo",Toast.LENGTH_SHORT).show();
                }
                //si ha rellenado todos los campos obligatorios correctamente
                else {
                    Article articulo= new Article(titulo,subtitulo,categoria,findViewById(R.id.img_ca_subida),cuerpo,resumen);

                    try {
                        String apikey=getIntent().getStringExtra("api");
                        String authorithation=getIntent().getStringExtra("auth");
                        CreateArticle login = new CreateArticle(articulo, apikey,authorithation);
                        //Preparamos el thread que en este caso nos devuelve un response
                        ExecutorService es = Executors.newSingleThreadExecutor ();
                        String response=es.submit(login).get();
                        JSONObject respuesta= new JSONObject(response);
                        //String response = PostArticle.postArticle(articulo,apikey,authorithation);
                        //JSONObject respuesta= new JSONObject(response);
                        if ( respuesta.isNull("id") ){
                            Toast.makeText(act, "No se ha podido crear el articulo", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //obtenemos el nuevo articulo subido al servidor
                            InsertArticleBBDD insertArticleBBDD= new InsertArticleBBDD(act,respuesta.getInt("id"));
                            Thread thread= new Thread(insertArticleBBDD);
                            thread.start();
                            thread.join();
                            setContentView(R.layout.activity_main);
                            Toast.makeText(act, "Articulo creado correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    //funciones que usa un intent para cargar la imagen a subir para el articulo
    private void load_image() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 0);
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;
        if (resultCode == RESULT_OK) {
            try {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(stream); //transformamos en bitmap
                ImageView imagen = findViewById(R.id.img_ca_subida); //conseguimos el image view
                imagen.setImageBitmap(bitmap); //ponemos la foto que cargamos
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
