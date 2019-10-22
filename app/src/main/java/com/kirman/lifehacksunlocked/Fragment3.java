package com.kirman.lifehacksunlocked;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment3 extends Fragment {

    public static final String CHOSEN_FILTER_EXTRA = "CHOSEN CATEGORY";
    public static boolean loadOnce = true;

    private List<CategoriesEntity> catList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoriesAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Condition to go back to SplashActivity if RAM has been cleared so to prevent crashes
        if(!SplashActivity.initialized) {
            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
            startActivity(splashIntent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //Bind UI Element to code
        recyclerView = view.findViewById(R.id.categories_recycler);

        //Base activity to use as context
        Activity base = Objects.requireNonNull(getActivity());

        //Set the RecyclerView adapter
        mAdapter = new CategoriesAdapter(catList, base);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(base.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(base, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        //RecyclerView item functionality
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(base.getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                CategoriesEntity cat = catList.get(position);

                //'Return' to the base activity and tell it to replace this fragment with the next one
                ((MainActivity) getActivity()).changeFragments(MainActivity.SHOW_FILTERED, cat.getTitle(), 1);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if(loadOnce){
            prepareCategoriesData();
            mAdapter.notifyDataSetChanged();
            loadOnce = false;
        }

    }

    //Function to make all the categories to be displayed on the list
    public void prepareCategoriesData() {

        CategoriesEntity cat = new CategoriesEntity("Careers & Work", R.drawable.ic_career);
        catList.add(cat);

        cat = new CategoriesEntity("Clothing", R.drawable.ic_clothes);
        catList.add(cat);

        cat = new CategoriesEntity("Computers", R.drawable.ic_computer);
        catList.add(cat);

        cat = new CategoriesEntity("Electronics", R.drawable.ic_electronics);
        catList.add(cat);

        cat = new CategoriesEntity("Money & Finance", R.drawable.ic_finance);
        catList.add(cat);

        cat = new CategoriesEntity("Health & Fitness", R.drawable.ic_fitness);
        catList.add(cat);

        cat = new CategoriesEntity("Food & Drink", R.drawable.ic_food);
        catList.add(cat);

        cat = new CategoriesEntity("Home & Garden", R.drawable.ic_house);
        catList.add(cat);

        cat = new CategoriesEntity("Animals & Pets", R.drawable.ic_pets);
        catList.add(cat);

        cat = new CategoriesEntity("Productivity", R.drawable.ic_productivity);
        catList.add(cat);

        cat = new CategoriesEntity("Social", R.drawable.ic_social);
        catList.add(cat);

        cat = new CategoriesEntity("Traveling", R.drawable.ic_travel);
        catList.add(cat);

        cat = new CategoriesEntity("School & College", R.drawable.ic_education);
        catList.add(cat);

        cat = new CategoriesEntity("Arts & Culture", R.drawable.ic_art);
        catList.add(cat);

        cat = new CategoriesEntity("Miscellaneous", R.drawable.ic_misc);
        catList.add(cat);

    }
}
