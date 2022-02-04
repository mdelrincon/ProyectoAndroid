package es.upm.etsiinf.pmd.practica.modelo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.util.LinkedList;
import java.util.List;

import es.upm.etsiinf.pmd.practica.MainActivity;
import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.activities.DeleteArticle_act;
import es.upm.etsiinf.pmd.practica.activities.EditArticle_act;
import es.upm.etsiinf.pmd.practica.activities.DetalleArticulo_act;
import es.upm.etsiinf.pmd.practica.tasks.InsertArticleBBDD;
import es.upm.etsiinf.pmd.practica.util.BDUtils;

public class ArticleAdapter extends BaseAdapter {

    transient List<Article> articulos= new LinkedList<>(); //lista de articulos recibidos
    String apikey="";
    private Activity context;
    private String auth;

    public ArticleAdapter(String apikey, Activity context, String auth) {
        if (apikey==null) {
            apikey="";
        }
        this.apikey=apikey;
        this.auth=auth;
        this.context=context;
    }

    //para ir actualizando el listview
    public void setArticles(List<Article> list) {
        articulos.clear();
        articulos.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return articulos.size();
    }

    @Override
    public Object getItem(int i) {
        return articulos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //devuelve la vista del elemento i
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null) {
            convertView=inflater.inflate(R.layout.article_layout,null); //obtenemos el list view
        }
        //Modificamos el list view añadiendo los titulos, categorias, resumenes..
        ((TextView)convertView.findViewById(R.id.lbl_categoria)).setText(articulos.get(position).getCategory());
        ((TextView)convertView.findViewById(R.id.lbl_titulo)).setText(articulos.get(position).getTitle());
        ((TextView)convertView.findViewById(R.id.lbl_resumen)).setText(articulos.get(position).getAbstracto());
        Drawable back= new BitmapDrawable(articulos.get(position).getBmp());
        //((LinearLayout)convertView.findViewById(R.id.linearLayout)).setBackground(back);
        //((CardView)convertView.findViewById(R.id.cardView)).setBackground(back);

        if(articulos.get(position).getBmp() != null){
            ((ImageView)convertView.findViewById(R.id.imageView)).setBackground(new BitmapDrawable(articulos.get(position).getBmp()));
        }
        else {
            ((ImageView)convertView.findViewById(R.id.imageView)).setBackgroundColor(context.getResources().getColor(R.color.background_defecto));
        }


        //miramos si esta logueado para ver si puede editar o borrar un articulo
        SharedPreferences preferences=context.getSharedPreferences(context.getResources().getString(R.string.NOMBRE_FICHERO_PREFS_CONEXION),Context.MODE_PRIVATE);
        String username= preferences.getString(context.getResources().getString(R.string.nombreUsuario),null);
        if(apikey.equals("") || !articulos.get(position).getUsername().equals(username)) {
            ((Button)convertView.findViewById(R.id.btn_editar)).setVisibility(View.INVISIBLE);
            ((Button)convertView.findViewById(R.id.btn_borrar)).setVisibility(View.INVISIBLE);
        }
        else {
            ((Button)convertView.findViewById(R.id.btn_editar)).setVisibility(View.VISIBLE);
            ((Button)convertView.findViewById(R.id.btn_borrar)).setVisibility(View.VISIBLE);
        }


        //cuando pulsas en alguna noticia
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( context, DetalleArticulo_act.class);
                intent.putExtra("objetoData",   articulos.get(position));
                context.startActivity(intent);
            }
        });



        //boton de editar
        Button btn_editar = convertView.findViewById(R.id.btn_editar);
        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditArticle_act.class);
                Article article=articulos.get(position);
                intent.putExtra("objetoData",article);
                intent.putExtra("apikey",apikey);
                intent.putExtra("auth",auth);
                context.startActivityForResult(intent,1);
            }
        });
        //fin de boton de editar

        //boton de eliminar
        Button btn_borrar = convertView.findViewById(R.id.btn_borrar);
        btn_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mostrarDialogo(position);
            }
        });

        return convertView;
    }

    private void mostrarDialogo(int position) {
        //creamos el cuadro de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //titulo y mensaje del dialogo
        builder.setMessage(R.string.mensaje_dialogo);
        builder.setTitle(R.string.titulo_dialogo);
        //botones del cuadro de dialogo
        //boton de eliminar
        builder.setPositiveButton(R.string.eliminar_articulo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //confirma la eliminacion del articulo
                Intent intent = new Intent(context, DeleteArticle_act.class);
                Article article=articulos.get(position);
                intent.putExtra("id",article.getId());
                intent.putExtra("apikey",apikey);
                intent.putExtra("auth",auth);
                BDUtils bdUtils= new BDUtils();
                int retorno=bdUtils.borrarArticulo(context,article.getId());
                if(retorno==-1) {
                    System.out.println("No se ha podido eliminar de la bbdd local");
                }
                context.startActivityForResult(intent,2);

            }
        });
        //boton de cancelar
        builder.setNegativeButton(R.string.cancelar_eliminacion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context,"No se ha eliminado el articulo",Toast.LENGTH_SHORT).show();
            }
        });
        //mostramos el dialogo
        builder.show();
    }

}
