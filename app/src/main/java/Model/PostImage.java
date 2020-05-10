package Model;

import android.graphics.Bitmap;

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
}
