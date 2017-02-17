package eu.epitech.fernan_s.msa_m.yourimage.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.ByteArrayOutputStream;

/**
 * Created by quent on 04/02/2017.
 */

public class ImagesTools {
    public static String toBase64(Bitmap b) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static byte[] toByteArray(Bitmap b) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void LoadPictures(String extension, GlideUrl gurl, Context _ctx, ImageView imageView) {
        if (extension.equals(".gif") || extension.equals(".gifv"))
            Glide
                    .with(_ctx)
                    .load(gurl)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(imageView);
        else {
            Glide
                    .with(_ctx)
                    .load(gurl)
                    .dontAnimate()
                    .into(imageView);
        }
    }

    public static void LoadPictures(Uri gurl, Context _ctx, ImageView imageView) {
        Glide
                .with(_ctx)
                .load(gurl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(imageView);
    }
}
