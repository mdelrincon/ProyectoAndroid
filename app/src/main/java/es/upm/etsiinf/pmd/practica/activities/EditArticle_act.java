package es.upm.etsiinf.pmd.practica.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.tasks.InsertArticleBBDD;
import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;
import es.upm.etsiinf.pmd.practica.tasks.CRUD.EditArticle;

public class EditArticle_act extends AppCompatActivity implements Serializable {

    private transient Article item;
    private int id;
    private Activity act=null;
    private String auth,apikey;
    private static final int REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_articulo);
        Spinner spinner = (Spinner) findViewById(R.id.sp_ca_categorias);

        //Preparamos el spinner con las categorias para elegir
        //Creamos el array con las posibles categorias cerradas
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorias_array, android.R.layout.simple_spinner_item);
        //especificamos el layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //aplicamos el adaptador
        spinner.setAdapter(adapter);
        this.act=this;


        item = (Article) getIntent().getSerializableExtra("objetoData");
        auth=(String) getIntent().getSerializableExtra("auth");
        apikey=(String) getIntent().getSerializableExtra("apikey");
        id=item.getId();
        /*titulo.setText(item.getTitle());
        subtitulo.setText(item.getSubtitle());
        categoria.setText(item.getCategory());
        resumen.setText(item.getAbstracto());
        cuerpo.setText(item.getBody());
        leyenda.setText(item.getUpdate_date());
        imagen.setImageBitmap(item.getBmp());
        imagen.setImageBitmap(ConvertidorImags.base64StringToImg(item.getThumbnail_image()));*/


        //Cogemos como esta el articulo y lo mostramos
        ((EditText) this.findViewById(R.id.edit_txt_ca_titulo)).setText(item.getTitle());
        ((EditText) this.findViewById(R.id.edit_txt_ca_subtitulo)).setText(item.getSubtitle());
        ((EditText) this.findViewById(R.id.edit_txt_ca_resumen)).setText(item.getAbstracto());
        ((EditText) this.findViewById(R.id.edit_txt_ca_cuerpo)).setText(item.getBody());
        ImageView imagen=findViewById(R.id.img_ca_subida); //conseguimos el image view
        imagen.setImageBitmap(ConvertidorImags.base64StringToImg(item.getThumbnail_image()));
        String categoria_articulo=item.getCategory();
        int num=0;
        if(categoria_articulo == null) {
           num=0;
        }
        else if (categoria_articulo.equals("National") ||categoria_articulo.equals("Nacional")) {
            num=1;
        }
        else if(categoria_articulo.equals("Sports") || categoria_articulo.equals("Deportes")) {
            num=3;
        }
        else if(categoria_articulo.equals("Economía") || categoria_articulo.equals("Economy") || categoria_articulo.equals("Economia")) {
            num=2;
        }
        else if(categoria_articulo.equals("Tecnología") || categoria_articulo.equals("Technology") || categoria_articulo.equals("Tecnologia")) {
            num=4;
        }
        spinner.setSelection(num);

        Button cancelar= findViewById(R.id.btn_ca_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Obtenemos la imagen que carguemos desde el movil
        Button btn_carga_imagen= findViewById(R.id.btn_ca_selecImage);
        btn_carga_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_image(); //llamamos a la funcion load image para cargar la imagen
            }
        });


        ((Button) findViewById(R.id.btn_ca_crear)).setText("Actualizar articulo");
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
                        String authorithation=auth;
                        EditArticle login = new EditArticle(articulo, apikey,authorithation,id);
                        //Preparamos el thread que en este caso nos devuelve un response
                        ExecutorService es = Executors.newSingleThreadExecutor ();
                        String response=es.submit(login).get();
                        JSONObject respuesta= new JSONObject(response);
                        if ( respuesta.isNull("id") ){
                            Toast.makeText(act, "No se ha podido actualizar el articulo", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //en caso de ser editado el requestCode será el id del articulo nuevo que tenemos que meter en la base de datos actualizandolo
                            InsertArticleBBDD insertArticleBBDD= new InsertArticleBBDD(act,id);
                            Thread thread= new Thread(insertArticleBBDD);
                            thread.start();
                            try {
                                thread.join();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent resultIntent=new Intent();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            String fecha=formatter.format(date);
                            resultIntent.putExtra("articulo",fecha);
                            setResult(0,resultIntent);
                            Toast.makeText(act, "Articulo actualizado correctamente", Toast.LENGTH_SHORT).show();
                            setContentView(R.layout.activity_main);
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
        startActivityForResult(intent, REQUEST_CODE);
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream=null;
        if(resultCode==RESULT_OK) {
            try {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(stream); //transformamos en bitmap
                ImageView imagen=findViewById(R.id.img_ca_subida); //conseguimos el image view
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
    //fin funciones que cargan imagenes

}
