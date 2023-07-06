package com.example.asdasdad.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.bumptech.glide.Glide;
import com.example.asdasdad.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailApi#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailApi extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailApi() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailApi.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailApi newInstance(String param1, String param2) {
        DetailApi fragment = new DetailApi();
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
    String key = "";
    String imageUrl = "";
    FloatingActionMenu apiMenuButton;
    FloatingActionButton apiFavoriteButton;
    Boolean currentRecipeIsFavorite;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_detail_api,container,false);

        detailName = viewF.findViewById(R.id.detaile_api_name);
        detailIngredients = viewF.findViewById(R.id.detail_api_ingredients);
        detailInstructions = viewF.findViewById(R.id.detail_api_instructions);
        detailImage = viewF.findViewById(R.id.detail_api_image);
        apiMenuButton = viewF.findViewById(R.id.apiMenuButton);
        apiFavoriteButton =viewF.findViewById(R.id.apiFavoriteButton);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Log.d("result" , "yes");
                Navigation.findNavController(viewF).navigate(R.id.action_detailApi_to_show_api_recipeFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);



        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                detailName.setText(result.getString("recipeName"));
                detailIngredients.setText(result.getString("recipeIngredients"));
                detailInstructions.setText(result.getString("recipeInstructions"));
                key = result.getString("key");
                imageUrl = result.getString("recipeImage");
                Glide.with(getContext()).load(result.getString("recipeImage")).into(detailImage);

                currentRecipeIsFavorite = isFavorite(detailName.getText().toString(),sharedPref);
                if(currentRecipeIsFavorite){
                    apiFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_favorite_24));
                }
            }
        });

        apiFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiMenuButton.close(true);
                SharedPreferences.Editor editor = sharedPref.edit();
                Map<String, ?> allEntries = sharedPref.getAll();

                if (currentRecipeIsFavorite){
                    currentRecipeIsFavorite = false;
                    apiFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_favorite_border_red_24));

                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        entry.getValue();
                        try {
                            JSONObject jsonObj = new JSONObject(entry.getValue().toString());
                            if (jsonObj.getString("recipeName").equals(detailName.getText().toString())){
                                editor.remove(entry.getKey());
                                editor.apply();
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                else {
                    currentRecipeIsFavorite = true;
                    apiFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_favorite_24));

                    int allEntriesSize = allEntries.size() + 1;

                    String jasonString = "{\"recipeName\":" + "\"" + detailName.getText().toString() + "\"" +
                            ",\"recipeImage\":" + "\"" + imageUrl + "\"" +
                            ",\"recipeIngredients\":" + "\"" + detailIngredients.getText().toString() + "\"" +
                            ",\"recipeInstructions\":" + "\"" + detailInstructions.getText().toString() + "\"" +
                            ",\"recipeDifficulty\":" + "\""  + "\"" +
                            ",\"recipePreparationTime\":" + "\"" +  "\"" + "}";

                    editor.putString(Integer.toString(allEntriesSize), jasonString);
                    editor.apply();
                }
            }
        });


        return viewF;

    }

    public boolean isFavorite(String recipeName, @NonNull SharedPreferences sharedPref){
        Map<String, ?> allEntries = sharedPref.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            entry.getValue();
            try {
                JSONObject jsonObj = new JSONObject(entry.getValue().toString());
                if (jsonObj.getString("recipeName").equals(recipeName)){
                    return true;
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}