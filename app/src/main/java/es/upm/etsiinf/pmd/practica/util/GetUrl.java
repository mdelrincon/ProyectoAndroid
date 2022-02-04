package es.upm.etsiinf.pmd.practica.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import es.upm.etsiinf.pmd.practica.modelo.Article;

public class GetUrl {

    public static String getURLText(String url) throws Exception {
        try {
        URL website= new URL(url);
        URLConnection connection= website.openConnection();
        BufferedReader in= new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response= new StringBuilder();
        String inputline;

        while((inputline = in.readLine()) != null )
            response.append(inputline);

        in.close();

        return response.toString(); }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postURL(JSONObject json) throws Exception {
        URL website= new URL("https://sanger.dia.fi.upm.es/pmd-task/login");
        HttpURLConnection connection=(HttpURLConnection)website.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
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


    public static Article getArticle (String url, int id) {
        try {
            URL website= new URL(url+id);
            URLConnection connection= website.openConnection();
            BufferedReader in= new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder response= new StringBuilder();
            String inputline;

            while((inputline = in.readLine()) != null )
                response.append(inputline);

            in.close();

            String respuesta = response.toString();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            return gson.fromJson(respuesta, Article.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
