package es.upm.etsiinf.pmd.practica.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.tasks.CRUD.DeleteArticle;

public class DeleteArticle_act extends AppCompatActivity implements Serializable {

    private int id;
    private Activity act=null;
    private String auth,apikey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.act=this;
        auth=(String) getIntent().getSerializableExtra("auth");
        apikey=(String) getIntent().getSerializableExtra("apikey");
        id=(int) getIntent().getSerializableExtra("id");

        try {
            String authorithation=auth;
            DeleteArticle deleteArticle = new DeleteArticle(id, apikey,authorithation);
            //Preparamos el thread que en este caso nos devuelve un response
            ExecutorService es = Executors.newSingleThreadExecutor ();
            String response=es.submit(deleteArticle).get();
            if (response.equals("")) {
                Toast.makeText(act, "Articulo eliminado correctamente", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_main);
                finish();
            } else {
                Toast.makeText(act, "No se ha podido eliminar el articulo", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
