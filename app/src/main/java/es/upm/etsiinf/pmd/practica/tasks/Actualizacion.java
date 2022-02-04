package es.upm.etsiinf.pmd.practica.tasks;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import es.upm.etsiinf.pmd.practica.services.ServicioActualizacionReceiver;

public class Actualizacion {
    public static void schedule(Context ctx) {
        ComponentName serviceComponent = new ComponentName(ctx,
                ServicioActualizacionReceiver.class); //decidimos clase a ejecutar
        JobInfo.Builder jobConf = new JobInfo.Builder(0,serviceComponent); //decidir configuracion
        jobConf.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobConf.setMinimumLatency(30*1000); //cada 30 segundos
        jobConf.setOverrideDeadline(30*1000);
        //jobConf.setPersisted(true);

        //pedimos aceso al servicio de programacion de trabajos
        JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
        jobScheduler.schedule(jobConf.build());



    }

}
