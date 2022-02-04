package es.upm.etsiinf.pmd.practica.util;

import androidx.annotation.RequiresApi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ConvertidorImags {

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public static android.graphics.Bitmap base64StringToImg(String input)
    {
        try {
            byte[] decodedBytes = Base64.decode(input, Base64.NO_WRAP);
            Bitmap result = BitmapFactory.decodeByteArray(decodedBytes, 0,
                    decodedBytes.length);
            return result;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String imgToBase64String(Bitmap image)
    {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
        int quality = 100;
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }


}
