package Model;

import java.util.ArrayList;

/**
 *
 * Created by F. Rallo on 26/03/2020.
 *  * version 1
 */
public final class ListOfImages extends ArrayList<PostImage> {
    public static ArrayList<PostImage> listOfPostImages= new ArrayList<>();
    public ListOfImages(){
        super();
        // add( new PostImage( "Amidala", "La jeune femme forte", R.drawable.amidala ));
    }

    @Override
    public String toString() {
        StringBuilder res;
        res = new StringBuilder("ListOfImages= { ");
        for (PostImage PostImage : this) {  res.append(PostImage);  }
        res.append("listOfCharacters=").append(this);
        res.append(" }");
        return res.toString();
    }
}

