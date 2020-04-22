package characters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ihmproject.MainActivity;
import com.example.ihmproject.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostImageDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostImageDetailFragment extends Fragment implements IPostImageArguments, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private IPostImageDetailClickListener mCallBack;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostImageDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallBack = (IPostImageDetailClickListener) getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonnageDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static PostImageDetailFragment newInstance(String param1, String param2) {
        PostImageDetailFragment fragment = new PostImageDetailFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_picture, container, false);
        assert getArguments() != null;
        PostImage postImage = MainActivity.listOfPostImages.get(getArguments().getInt(fragmentParam));
        ((ImageView)rootView.findViewById(R.id.imageTaken)).setImageBitmap(postImage.getPicture());
        return rootView;
    }

    @Override
    public void onClick(View v) {
        // if(v.getId() == R.id.BackToList)mCallBack.onReturnButtonClicked(v);
    }
}
