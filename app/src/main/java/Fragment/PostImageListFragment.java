package Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihmproject.MainActivity;
import com.example.ihmproject.R;

import java.util.ArrayList;

import Interface.IPostImageClickListener;
import PostImage.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostImageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostImageListFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private IPostImageClickListener mCallBack;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    private RecyclerView recyclerView;
    private TextView picturesCountShower;
    public PostImageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallBack = (IPostImageClickListener) getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonnageList.
     */
    // TODO: Rename and change types and number of parameters
    public static PostImageListFragment newInstance(String param1, String param2) {
        PostImageListFragment fragment = new PostImageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public void addNewPostImage(Bitmap bitmap){
        ListOfImages.listOfPostImages.add(new PostImage(bitmap));
        resetPostImageList();
    }
    private void resetPostImageList(){
        recyclerView=(RecyclerView) rootView.findViewById(R.id.imagesPostList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rootView.findViewById(R.id.imagesPostList);
        final PostImageAdapter postImageAdatper = new PostImageAdapter(ListOfImages.listOfPostImages,getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postImageAdatper);
        mCallBack.incrementImageTotal();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_image_list, container, false);
        Log.d("jiv", "onCreateView: picture"+picturesCountShower);
        resetPostImageList();
/*
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
 */
        /*
        new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String item = (String) mListView.getItemAtPosition(position);
                Toast.makeText(rootView.getContext(),"Vous avez sélectionné : " + item,Toast.LENGTH_SHORT).show();
            }
        }
         */
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCallBack.onPostImageClicked(position);
    }
}
