package Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 *  * created by F. Rallo on 26/02/2020.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class PostImage implements Serializable {
    private Bitmap picture;
    /**
     * constructeur normal
     * @param picture of diploma
     */
    public PostImage(Bitmap picture) {
        this.picture = picture;
    }

    // ---------------------- accesseurs -------------------------
    public Bitmap getPicture() {
        return picture;
    }

    public String toString() {
        return "Image{" +
                "name='" + picture + '\''+
                '}';
    }
    public static String encode(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public static Bitmap decode(String string){
        byte[] imageAsBytes = Base64.decode(string,Base64.DEFAULT);//encoded.getBytes());
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
