package eu.epitech.fernan_s.msa_m.yourimage.tools;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by quent on 04/02/2017.
 */

public class ImagesTools {
    public static String toBase64(Bitmap b) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
