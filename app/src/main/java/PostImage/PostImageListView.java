package PostImage;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ihmproject.R;

public class PostImageListView extends RecyclerView.ViewHolder {
    private ImageView imageView;
    void setImageViewPicture(Bitmap picture){
        imageView.setImageBitmap(picture);
    }
    public ImageView getImageView(ImageView imageView){
        return imageView;
    }
    PostImageListView(View view) {
        super(view);
        //textViewTitle = (TextView) view.findViewById(R.id.textview_titley);
        imageView = (ImageView) view.findViewById(R.id.imageTaken);
        // public TextView textViewTitle;
        if(ListOfImages.listOfPostImages.size()>1){
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_picture);
            linearLayout.getLayoutParams().width = (int) (Utils.getScreenWidth(itemView.getContext()) / 2);
        }
        // linearLayout.getLayoutParams().height = (int) (Utils.getScreenWidth(itemView.getContext()) / 2);
    }
}
