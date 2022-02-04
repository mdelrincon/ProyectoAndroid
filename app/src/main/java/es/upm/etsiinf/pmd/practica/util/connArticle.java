package es.upm.etsiinf.pmd.practica.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import es.upm.etsiinf.pmd.practica.modelo.Article;

public class connArticle {

    public static String postArticle(Article articulo, String apikey,String authorithation) throws Exception {
        //requiere cabecera de autenticacion y no id ya que es nuevo articulo

        //creamos el json con el articulo y sus caracteristicas
        JSONObject json = new JSONObject();
        json.put("title", articulo.getTitle());
        json.put("subtitle", articulo.getSubtitle());
        json.put("body", articulo.getBody());
        json.put("category", articulo.getCategory());
        json.put("abstract", articulo.getAbstracto());
        json.put("image_data", articulo.getImage_data());


        URL website= new URL("https://sanger.dia.fi.upm.es/pmd-task/article");
        HttpURLConnection connection=(HttpURLConnection)website.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        String auth= authorithation+" apikey="+apikey;
        connection.setRequestProperty("Authorization",auth);
        connection.setDoOutput(true);

        OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream());
        wr.write(json.toString());
        wr.close();

        // read the response
        BufferedReader br =null;
        if (connection.getResponseCode()==401){
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        else {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        System.out.println(response.toString());
        return response.toString();

    }

    public static String updateArticle(Article articulo, String apikey,String authorithation,int id) throws Exception {
        //requiere cabecera de autenticacion y no id ya que es nuevo articulo

        //creamos el json con el articulo y sus caracteristicas
        JSONObject json = new JSONObject();
        json.put("title", articulo.getTitle());
        json.put("subtitle", articulo.getSubtitle());
        json.put("body", articulo.getBody());
        json.put("category", articulo.getCategory());
        json.put("abstract", articulo.getAbstracto());
        json.put("image_data", articulo.getImage_data());
        json.put("id",id);

        String URL="https://sanger.dia.fi.upm.es/pmd-task/article";
        URL website= new URL(URL);
        HttpURLConnection connection=(HttpURLConnection)website.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        String auth= authorithation+" apikey="+apikey;
        connection.setRequestProperty("Authorization",auth);
        connection.setDoOutput(true);

        OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream());
        wr.write(json.toString());
        wr.close();

        // read the response
        BufferedReader br =null;
        if (connection.getResponseCode()==401){
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        else {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        System.out.println(response.toString());
        return response.toString();

    }

    public static String deleteArticle(int id,String apikey,String authorithation) throws Exception {
        //metemos en la url el id para borrar ese articulo


        String URL="https://sanger.dia.fi.upm.es/pmd-task/article/"+id;
        URL website= new URL(URL);
        HttpURLConnection connection=(HttpURLConnection)website.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        String auth= authorithation+" apikey="+apikey;
        connection.setRequestProperty("Authorization",auth);
        connection.setDoOutput(true);


        // read the response
        BufferedReader br =null;
        if (connection.getResponseCode()==401){
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        else {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        System.out.println(response.toString());
        return response.toString();

    }


}
