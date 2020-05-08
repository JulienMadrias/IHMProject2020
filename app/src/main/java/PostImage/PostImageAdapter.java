package PostImage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihmproject.R;

import java.util.ArrayList;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageListView> {
    private ArrayList<PostImage> horizontalList;
    private Context context;
    public PostImageAdapter(ArrayList<PostImage> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostImageListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_picture, parent, false);
        return new PostImageListView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostImageListView holder, int position) {
        PostImage postImage = horizontalList.get(position);
        holder.setImageViewPicture(postImage.getPicture());
    }
    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

}
