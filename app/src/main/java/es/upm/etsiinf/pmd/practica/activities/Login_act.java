package es.upm.etsiinf.pmd.practica.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.tasks.CRUD.DeleteArticle;
import es.upm.etsiinf.pmd.practica.tasks.PostLogin;

public class Login_act extends AppCompatActivity implements Serializable {


    private Activity act = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.act = this;
        setContentView(R.layout.login);
        Button btn_login = act.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = ((EditText) (act.findViewById(R.id.txt_username))).getText().toString();
                String password = ((EditText) (act.findViewById(R.id.txt_password))).getText().toString();
                JSONObject json = new JSONObject();
                //creamos el json
                try {
                    String response = null;
                    json.put("username", usuario);
                    json.put("passwd", password);
                    PostLogin login = new PostLogin(act, json);
                    //Preparamos el thread que en este caso nos devuelve un response
                    ExecutorService es = Executors.newSingleThreadExecutor();
                    response = es.submit(login).get();
                    JSONObject respuesta = new JSONObject(response);
                    if (respuesta.isNull("username")) {
                        Toast.makeText(act, "Prueba otra vez, usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    } else {
                        String apikey_response = respuesta.getString("apikey");
                        String authorithathion = respuesta.getString("Authorization");
                        //Casilla recuerdame
                        CheckBox casilla_recuerdame = act.findViewById(R.id.check_recuerdame);
                        //el intent que devuelve los resultados
                        Intent resultIntent = new Intent();
                        if (casilla_recuerdame.isChecked()) {
                            resultIntent.putExtra("apikey",apikey_response);
                            resultIntent.putExtra("auth",authorithathion);
                            resultIntent.putExtra("casilla_recuerdame","true");
                            resultIntent.putExtra("username",usuario);


                        } else {
                            resultIntent.putExtra("apikey",apikey_response);
                            resultIntent.putExtra("auth",authorithathion);
                            resultIntent.putExtra("casilla_recuerdame","false");
                            resultIntent.putExtra("username",usuario);


                        }
                        setResult(0,resultIntent);
                        setContentView(R.layout.activity_main);
                        Toast.makeText(act, "Has iniciado sesión correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

