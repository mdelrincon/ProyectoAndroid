package es.upm.etsiinf.pmd.practica.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.modelo.Article;
import es.upm.etsiinf.pmd.practica.util.ConvertidorImags;

public class DetalleArticulo_act extends AppCompatActivity implements Serializable {
    private transient Article item;
    private TextView titulo, subtitulo, categoria, leyenda, resumen, cuerpo;
    private ImageView imagen;


        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_detalle_articulo);

            item = (Article) getIntent().getSerializableExtra("objetoData");

            titulo = findViewById(R.id.art_titulo);
            subtitulo = findViewById(R.id.art_subtitulo);
            categoria = findViewById(R.id.art_categoria);
            leyenda = findViewById(R.id.art_leyenda);
            resumen = findViewById(R.id.art_resumen);
            cuerpo = findViewById(R.id.art_cuerpo);
            imagen = findViewById(R.id.art_imagen);

            titulo.setText(item.getTitle());
            subtitulo.setText(item.getSubtitle());
            categoria.setText(item.getCategory());
            resumen.setText(item.getAbstracto());
            cuerpo.setText(item.getBody());
            leyenda.setText(item.getUpdate_date());
            imagen.setImageBitmap(ConvertidorImags.base64StringToImg(item.getThumbnail_image()));


        }

}
