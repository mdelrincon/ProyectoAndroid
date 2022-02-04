package es.upm.etsiinf.pmd.practica.services;

import java.util.concurrent.Callable;

import es.upm.etsiinf.pmd.practica.util.connActualizacion;
import es.upm.etsiinf.pmd.practica.util.connArticle;

public class LlamadaConnActualizacion implements Callable<String> {

    private String last_update_date;

    public LlamadaConnActualizacion(String last_update_date) {
        this.last_update_date=last_update_date;
    }


    @Override
    public String call() throws Exception {
        //cuando le den al boton de crear articulo lo creamos
        String response="";
        try {
            connActualizacion connActualizacion = new connActualizacion();
            response= connActualizacion.getActualizacion(this.last_update_date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
