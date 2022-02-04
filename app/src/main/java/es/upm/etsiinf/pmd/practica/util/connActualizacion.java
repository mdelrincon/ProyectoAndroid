package es.upm.etsiinf.pmd.practica.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class connActualizacion {

    public static String getActualizacion(String fecha) throws IOException {
        //sin autorizacoin
        String url="https://sanger.dia.fi.upm.es/pmd-task/articlesFrom/"+fecha;
        URL website= new URL(url);
        URLConnection connection= website.openConnection();
        BufferedReader in= new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response= new StringBuilder();
        String inputline;

        while((inputline = in.readLine()) != null )
            response.append(inputline);

        in.close();

        return response.toString();
    }

}