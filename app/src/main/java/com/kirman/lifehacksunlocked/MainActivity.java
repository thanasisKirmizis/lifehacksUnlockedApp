package com.kirman.lifehacksunlocked;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;

import com.google.gson.Gson;

public class MainActivity extends FragmentActivity {

    //UI Elements
    ConstraintLayout mHeader;
    ConstraintLayout mBody;
    Button mGoBookmarked;
    Button mHome;
    Button mSelectCategory;
    ConstraintLayout mFooter;

    //The arguments bundle to choose category
    Bundle bundle = new Bundle();

    //Static numbering of pages
    public static int GO_TO_HOME = 0;
    public static int CHOOSE_CATEGORY = 1;
    public static int SHOW_FILTERED = 2;

    //Global declaration of categories list fragment
    Fragment3 catFrag = new Fragment3();

    //Number to keep track of which fragment is displayed (Home: 0, Select Category: 1, Bookmarks: 2, Show Filtered: 3)
    int currentlySelected = 0;

    //Key to look for last displayed card SharedPrefs
    public static final String COUNTER_KEY = "counter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Re-initialization of categories list fragment when Activity is re-created
        Fragment3.loadOnce = true;

        //Condition to go back to SplashActivity if RAM has been cleared so to prevent crashes
        if(!SplashActivity.initialized) {
            Intent splashIntent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(splashIntent);
            finish();
        }

        //Bind UI Elements to code
        mHeader = findViewById(R.id.headerLayout);
        mBody = findViewById(R.id.body);
        mGoBookmarked = findViewById(R.id.goBookmarkedButton);
        mHome = findViewById(R.id.goHomeButton);
        mSelectCategory = findViewById(R.id.categoriesButton);
        mFooter = findViewById(R.id.footerLayout);

        //If the app is in split-screen mode, hide top and bottom bars to enhance appearance
        if(isInMultiWindowMode()) {
            mHeader.setVisibility(View.GONE);
            mFooter.setVisibility(View.GONE);
        }

        //Update bottom bar UI
        updateBottomUI("home");

        //Setup an initial fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        //Attach the categories fragment to work as pre-loading (??)
        trans.attach(catFrag);
        //Initialize the Home Fragment to be displayed at first
        Fragment1 homeFrag = new Fragment1();
        trans.replace(R.id.body, homeFrag);
        trans.commit();

        //Go To Bookmarked Button functionality
        mGoBookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //If not already in Home
                if(currentlySelected != 2) {

                    //Update bottom bar UI
                    updateBottomUI("bookmark");

                    //Pass the value "Bookmarked" and start the Bookmarks Fragment
                    changeFragments(SHOW_FILTERED, "Bookmarked", currentlySelected);

                    //Change currently selected pointer
                    currentlySelected = 2;
                }

            }
        });

        //Go To Home Button functionality
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //If not already in Home
                if(currentlySelected != 0) {

                    //Update bottom bar UI
                    updateBottomUI("home");

                    //Start the Home Fragment
                    changeFragments(GO_TO_HOME, "", currentlySelected);

                    //Change currently selected pointer
                    currentlySelected = 0;
                }

            }
        });

        //Show Categories Button functionality
        mSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Update bottom bar UI
                updateBottomUI("category");

                //Start the Select Category Fragment
                changeFragments(CHOOSE_CATEGORY, "", currentlySelected);

                //Change currently selected pointer
                currentlySelected = 1;
            }
        });

    }

    //Used to save bookmarks before closing app
    @Override
    protected void onStop() {
        super.onStop();

        //Save all the tips to Shared Prefs
        SharedPreferences prefs = MainActivity.this.getSharedPreferences(SplashActivity.LPT_PREFS, 0);

        Gson gson = new Gson();
        for(int i = 0; i<SplashActivity.globalNmbrOfTips; i++) {

            String json = gson.toJson(SplashActivity.globalTips[i]);
            prefs.edit().putString("tips"+String.valueOf(i), json).apply();
        }

        //Save the last card that was displayed
        prefs.edit().putInt(COUNTER_KEY, Fragment1.counter).apply();
    }

    //Back button functionality
    @Override
    public void onBackPressed() {

        if(currentlySelected == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Lifehacks Unlocked");
            builder.setMessage("Do you really want to exit?");
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finishAffinity();
                    System.exit(0);
                }
            });
            builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        else if(currentlySelected == 3) {
            currentlySelected = 1;
            super.onBackPressed();
        }
        else {

            //Update bar UI
            updateBottomUI("home");

            //Initialize the Home Fragment to be displayed again
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction trans = fragmentManager.beginTransaction();
            if(currentlySelected == 1){
                trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            else if(currentlySelected == 2){
                trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            Fragment1 homeFrag = new Fragment1();
            trans.replace(R.id.body, homeFrag);
            trans.commit();

            currentlySelected = 0;

        }

    }

    //Updates the fill color of the icons and the selection rectangle
    private void updateBottomUI(String element) {

        //Change the 'Selection' rectangle on the bottom bar
        mHome.setBackground(ContextCompat.getDrawable(MainActivity.this, android.R.color.transparent));
        mSelectCategory.setBackground(ContextCompat.getDrawable(MainActivity.this, android.R.color.transparent));
        mGoBookmarked.setBackground(ContextCompat.getDrawable(MainActivity.this, android.R.color.transparent));

        //Change the fill color of the icons on the bottom bar
        Drawable img = getResources().getDrawable(R.drawable.custom_size_home);
        mHome.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null);
        img = getResources().getDrawable(R.drawable.custom_size_category);
        mSelectCategory.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null);
        img = getResources().getDrawable(R.drawable.custom_size_bookmark);
        mGoBookmarked.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null);

        //Specify the selected one
        if(element.equals("bookmark")){
            mGoBookmarked.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.empty_rect_yellow));
            img = getResources().getDrawable(R.drawable.custom_size_bookmark_filled);
            mGoBookmarked.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null);
        }
        else if(element.equals("home")){
            mHome.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.empty_rect_yellow));
            img = getResources().getDrawable(R.drawable.custom_size_home_filled);
            mHome.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null);
        }
        else if(element.equals("category")){
            mSelectCategory.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.empty_rect_yellow));
            img = getResources().getDrawable(R.drawable.custom_size_category_filled);
            mSelectCategory.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null);
        }

        mHome.setPadding(0, 25, 0, 0);
        mSelectCategory.setPadding(0, 25, 0, 0);
        mGoBookmarked.setPadding(0, 25, 0, 0);


    }

    //Handle the swapping of Fragments inside the 'body' layout
    public void changeFragments(int f, String args, int currSelect) {
        //Prepare Fragment Manager
        bundle.putString(Fragment3.CHOSEN_FILTER_EXTRA, args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();

        //Swap to the Home fragment
        if(f == GO_TO_HOME){

            //If coming from categories tab
            if(currSelect == 1 || currSelect == 3) {
                Fragment1 homeFrag = new Fragment1();
                trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                trans.replace(R.id.body, homeFrag);
            }
            //If coming from bookmarks tab
            else if (currSelect == 2){
                Fragment1 homeFrag = new Fragment1();
                trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                trans.replace(R.id.body, homeFrag);
            }

        }
        //Swap to the Categories fragment
        else if(f == CHOOSE_CATEGORY){

            //If coming from filtered categories
            if(currSelect == 3) {
                //Fragment3 catFrag = new Fragment3();
                trans.replace(R.id.body, catFrag);
            }
            //If coming from anywhere else
            else if(currSelect != 1) {
                //Fragment3 catFrag = new Fragment3();
                trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                trans.replace(R.id.body, catFrag);
            }
        }
        //Swap to the Filtered fragment
        else if (f == SHOW_FILTERED){

            //Showing bookmarks
            if(args.equals("Bookmarked") && currSelect != 2){

                Fragment2 filteredFrag = new Fragment2();
                filteredFrag.setArguments(bundle);
                trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                trans.replace(R.id.body, filteredFrag);
            }
            //Showing Filtered Categories
            else {

                Fragment2 filteredFrag = new Fragment2();
                filteredFrag.setArguments(bundle);
                trans.replace(R.id.body, filteredFrag);
                trans.addToBackStack(null);
                currentlySelected = 3;
            }

        }
        trans.commit();

    }

}
