package es.upm.etsiinf.pmd.practica.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.upm.etsiinf.pmd.practica.R;
import es.upm.etsiinf.pmd.practica.activities.ActualizacionArticles_act;
import es.upm.etsiinf.pmd.practica.tasks.Actualizacion;
import es.upm.etsiinf.pmd.practica.util.connActualizacion;

public class ServicioActualizacionReceiver extends JobService {




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Intent servicio=new Intent(getApplicationContext(), ActualizacionArticles_act.class);
        getApplicationContext().startService(servicio);
        Actualizacion.schedule(getApplicationContext());
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }


}
