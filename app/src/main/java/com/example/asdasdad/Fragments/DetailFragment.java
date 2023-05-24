package com.example.asdasdad.Fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asdasdad.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
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


    ImageView detailImage;
    TextView detailName, detailIngredients, detailInstructions;
    TextView detailPreparation, detailDifficulty;
    FloatingActionButton deleteButton;
    String key = "";
    String imageUrl = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_detail,container,false);

        detailName = viewF.findViewById(R.id.detaile_name);
        detailIngredients = viewF.findViewById(R.id.detail_ingredients);
        detailInstructions = viewF.findViewById(R.id.detail_instructions);
        detailPreparation = viewF.findViewById(R.id.detail_preparation_time);
        detailDifficulty = viewF.findViewById(R.id.detail_difficulty_level);
        detailImage = viewF.findViewById(R.id.detail_image);

        deleteButton = viewF.findViewById(R.id.deleteButton);



        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Log.d("result" , "yes");
                Navigation.findNavController(viewF).navigate(R.id.action_detailFragment_to_show_all_recipes_fregment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);



        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                detailName.setText(result.getString("recipeName"));
                detailIngredients.setText(result.getString("recipeIngredients"));
                detailInstructions.setText(result.getString("recipeInstructions"));
                detailPreparation.setText(result.getString("recipePreparationTime"));
                detailDifficulty.setText(result.getString("recipeDifficulty"));
                key = result.getString("key");
                imageUrl = result.getString("recipeImage");
                Glide.with(getContext()).load(result.getString("recipeImage")).into(detailImage);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("recipe");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(viewF).navigate(R.id.action_detailFragment_to_show_all_recipes_fregment);
                    }
                });
            }
        });



        return viewF;
    }
}