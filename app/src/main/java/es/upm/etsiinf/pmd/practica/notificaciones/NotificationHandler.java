package es.upm.etsiinf.pmd.practica.notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.RequiresApi;

import es.upm.etsiinf.pmd.practica.MainActivity;
import es.upm.etsiinf.pmd.practica.R;

public class NotificationHandler extends ContextWrapper {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHandler(Context base) {
        super(base);
        createChannels(); //Creamos los canales
    }

    NotificationManager manager;


    public static final String highchannelID = "1";
    public static final String highchannelName = "HIGH CHANNEL";

    //para las agrupaciones
    public static final String groupSummary = "GRUPO";
    public static final int groupID = 111;

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return manager;
    }

    //Creacion de canales
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        //Creamos canales
        NotificationChannel canalA = new NotificationChannel(highchannelID, highchannelName, NotificationManager.IMPORTANCE_HIGH);

        //Configuramos canales (opcional)
        canalA.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        canalA.setShowBadge(true);


        //Crear canal de notificaciones en el manager
        NotificationManager notificacion = getManager();
        notificacion.createNotificationChannel(canalA);
    }

    //metodo que llamaremos
    public Notification.Builder createNotification(String titulo, Bitmap image, boolean prioridad) {


        //Comprobacion de version
        if (Build.VERSION.SDK_INT >= 26) {
            //prioridad alta
            if (image==null){
                return createNotificationChannels(titulo, highchannelID);

            }
            else {
                return createNotificationChannels(titulo, image, highchannelID);
            }
        }
        else {
            return createNotificationWithoutChannels(titulo,image);
        }
    }

    //cuando image no existe
    public Notification.Builder createNotification(String title, boolean prioridad) {
        //Comprobacion de version
        if (Build.VERSION.SDK_INT >= 26) {
            //prioridad alta
            return createNotificationChannels(title, highchannelID);
        }
        else {
            return createNotificationWithoutChannels(title);
        }
    }

    private Notification.Builder createNotificationWithoutChannels(String title) {
        return new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_crear_article)
                .setContentTitle(title);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder createNotificationChannels(String title, String highchannelID) {
        // Creamos el intent que va a lanzar el NewActivity
        Intent intent = new Intent(this, MainActivity.class);
        // NEW TASK Y CLEAR TASK para evitar volver a la aplication si no estuviera abierta
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creamos el pendingintent
        PendingIntent pit = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Icon icon = Icon.createWithResource(this, R.drawable.ic_launcher_background);
        Notification.Action action = new Notification.Action.Builder (icon, "VER", pit).build();

        return new Notification.Builder(getApplicationContext(),highchannelID)
                    .setSmallIcon(R.drawable.ic_crear_article)
                    .setContentTitle(getApplicationContext().getResources().getString(R.string.titulo_notificaciones))
                    .setContentIntent(pit) // Añadimos el pendingIntent a la notificación
                    .setActions(action) // Añadimos la action creada
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle().bigText(title))// Estilo con texto grande
                    .setGroup(groupSummary);

    }

    //metodo para cuando tenemos canales
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder createNotificationChannels (String title, Bitmap image, String highchannelID) {
        // Creamos el intent que va a lanzar el NewActivity
        Intent intent = new Intent(this, MainActivity.class);
        // NEW TASK Y CLEAR TASK para evitar volver a la aplication si no estuviera abierta
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creamos el pendingintent
        PendingIntent pit = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Icon icon = Icon.createWithResource(this, R.drawable.ic_launcher_background);
        Notification.Action action = new Notification.Action.Builder (icon, "VER", pit).build();

        return new Notification.Builder(getApplicationContext(),highchannelID)
                .setSmallIcon(R.drawable.ic_crear_article)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.titulo_notificaciones))
                .setContentIntent(pit) // Añadimos el pendingIntent a la notificación
                .setActions(action) // Añadimos la action creada
                .setContentText(title)
                .setLargeIcon(image)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText(title))// Estilo con texto grande
                .setGroup(groupSummary);
    }

    //metodo para crear notificaciones sin canales por version menor a 26
    private Notification.Builder createNotificationWithoutChannels (String titulo, Bitmap mensaje) {
        return new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(titulo)
                .setLargeIcon(mensaje);
    }

    //metodo para los grupos
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createGroup (boolean prioridad) {
        String canal = highchannelID;
        Notification group = new Notification.Builder(this,canal)
                .setGroupSummary(true)
                .setGroup(groupSummary)
                .setSmallIcon(R.drawable.ic_launcher_foreground).build();
        getManager().notify(groupID,group);
    }


}