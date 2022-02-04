package es.upm.etsiinf.pmd.practica.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import es.upm.etsiinf.pmd.practica.modelo.Article;

public class BDUtils {
    //Constante campo tabla
    public static final String TABLA_ARTICULOS="articulos";
    public static final String CAMPO_ID="id";
    public static final String CAMPO_ABSTRACTO="abstract";
    public static final String CAMPO_BODY="body";
    public static final String CAMPO_SUBTITLE="subtitle";
    public static final String CAMPO_CATEGORY="category";
    public static final String CAMPO_TITLE="title";
    public static final String CAMPO_UPDATE_DATE="update_date";
    public static final String CAMPO_IMAGE_DATA="image_data";
    public static final String CAMPO_USERNAME="username";
    public static final String CAMPO_THUMBNAIL_IMAGE="thumbnail_image";
    private static final int Version=1;



    //Constante creacion de tabla
    public static final  String CREAR_TABLA_ARTICULO="CREATE TABLE "+ TABLA_ARTICULOS
            + " ("+CAMPO_ID+" INTEGER UNIQUE, "
            + CAMPO_ABSTRACTO + " TEXT, "
            + CAMPO_BODY + " TEXT, "
            + CAMPO_SUBTITLE + " TEXT, "
            + CAMPO_CATEGORY + " TEXT, "
            + CAMPO_TITLE + " TEXT, "
            + CAMPO_UPDATE_DATE+ " TEXT, "
            + CAMPO_IMAGE_DATA+ " TEXT, "
            +CAMPO_USERNAME+ " TEXT, "
            +CAMPO_THUMBNAIL_IMAGE+ " TEXT)";

    public static void registrarArticuloBD(Article article, Activity act) {
       ConexionSQLiteHelper conn= new ConexionSQLiteHelper(act);
        SQLiteDatabase db=conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAMPO_ID, article.getId());
        if(article.getAbstracto()!=null){values.put(CAMPO_ABSTRACTO, article.getAbstracto());}
        if(article.getBody()!=null){values.put(CAMPO_BODY, article.getBody());}
        if(article.getSubtitle()!=null){values.put(CAMPO_SUBTITLE, article.getSubtitle());}
        if(article.getCategory()!=null){values.put(CAMPO_CATEGORY, article.getCategory());}
        if(article.getTitle()!=null){values.put(CAMPO_TITLE, article.getTitle());}
        if(article.getUpdate_date()!=null){ values.put(CAMPO_UPDATE_DATE, article.getUpdate_date());}
        if(article.getThumbnail_image()!=null){values.put(CAMPO_IMAGE_DATA, article.getThumbnail_image());}
        if(article.getUsername()!=null){values.put(CAMPO_USERNAME, article.getUsername());}
        if(article.getThumbnail_image()!=null){values.put(CAMPO_THUMBNAIL_IMAGE, article.getThumbnail_image());}
        long res= db.insert(TABLA_ARTICULOS,CAMPO_ID, values);
        db.close();

    }

    public static void registrarArticuloNot(Article article, Context act) {
        ConexionSQLiteHelper conn= new ConexionSQLiteHelper(act);
        SQLiteDatabase db=conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        int id= article.getId();
        values.put(CAMPO_ID, article.getId());
        if(article.getAbstracto()!=null){values.put(CAMPO_ABSTRACTO, article.getAbstracto());}
        if(article.getBody()!=null){values.put(CAMPO_BODY, article.getBody());}
        if(article.getSubtitle()!=null){values.put(CAMPO_SUBTITLE, article.getSubtitle());}
        if(article.getCategory()!=null){values.put(CAMPO_CATEGORY, article.getCategory());}
        if(article.getTitle()!=null){values.put(CAMPO_TITLE, article.getTitle());}
        if(article.getUpdate_date()!=null){ values.put(CAMPO_UPDATE_DATE, article.getUpdate_date());}
        if(article.getThumbnail_image()!=null){values.put(CAMPO_IMAGE_DATA, article.getThumbnail_image());}
        if(article.getUsername()!=null){values.put(CAMPO_USERNAME, article.getUsername());}
        if(article.getThumbnail_image()!=null){values.put(CAMPO_THUMBNAIL_IMAGE, article.getThumbnail_image());}
        if(existeId(id,act)){
            db.update(TABLA_ARTICULOS,values,"id = ?", new String[]{Integer.toString(id)});
        }
        else {
           db.insert(TABLA_ARTICULOS,CAMPO_ID, values);
        }
        db.close();

    }

    private static boolean existeId(int id, Context act) {
        ConexionSQLiteHelper conn= new ConexionSQLiteHelper(act);
        SQLiteDatabase db=conn.getReadableDatabase();
        String query= "Select * from articulos " + "where id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    public static List<Article> rellenarListaBD(Activity act) {
        List<Article> res=new LinkedList<Article>();
        ConexionSQLiteHelper conn= new ConexionSQLiteHelper(act);
        SQLiteDatabase db=conn.getReadableDatabase();
        String query="Select * from articulos order by update_date desc limit 5000";
        Cursor cursor = db.rawQuery(query,null);
        //Cursor cursor = db.query(TABLA_ARTICULOS, campos, null, null, null, null, null);
        cursor.moveToFirst();
        do {

                res.add(res.size(), new Article((Integer) cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9)));
                cursor.moveToNext();

        }while(!cursor.isAfterLast());
        cursor.close();
        db.close();
        return res;
    }

    public static int borrarArticulo(Activity act, int id) {
        ConexionSQLiteHelper conn= new ConexionSQLiteHelper(act);
        SQLiteDatabase db=conn.getWritableDatabase();
        db.delete(TABLA_ARTICULOS,"id = ?",new String[]{Integer.toString(id)});
        if(existeId(id,act)) {
            db.close();
            return -1;
        }
        else {
            db.close();
            return 0;
        }

    }

    public static void updateArticle(Activity act, Article articulo) {
        ConexionSQLiteHelper conn= new ConexionSQLiteHelper(act);
        SQLiteDatabase db=conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAMPO_ID, articulo.getId());
        int id= articulo.getId();
        db.update(TABLA_ARTICULOS,values,"id = ?", new String[]{Integer.toString(id)});
        db.close();
        return;
    }

}
