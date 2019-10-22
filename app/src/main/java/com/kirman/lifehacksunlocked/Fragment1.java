package com.kirman.lifehacksunlocked;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class Fragment1 extends Fragment {

    //UI Elements
    private TextSwitcher mCardContent;
    private TextView mCardID;
    private TextView mCardCategory;
    private ImageButton mBookmark;
    private ImageButton mShare;
    private ImageView mNewBadge;
    private TextView mNext;
    private TextView mPrev;
    private CardView mCardView;

    //Just some vars
    public static int counter = 0;
    private static boolean doOnStart = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Condition to go back to SplashActivity if RAM has been cleared so to prevent crashes
        if(!SplashActivity.initialized) {
            Intent splashIntent = new Intent(getActivity(), SplashActivity.class);
            startActivity(splashIntent);
            getActivity().finish();
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Tag the 5 latest tips as new
        for(int i = 0; i<SplashActivity.globalNmbrOfTips; i++) {

            //Clear the previous new
            SplashActivity.globalTips[i].setNewTip(false);

            if(i >= SplashActivity.globalNmbrOfTips - SplashActivity.DAILY_LIMIT) {
                SplashActivity.globalTips[i].setNewTip(true);
            }
        }

        if(doOnStart) {

            //Set the counter accordingly
            if(SplashActivity.twoDaysPassed){

                //If two days have passed, on start, the New tips are displayed
                counter = SplashActivity.globalNmbrOfTips - SplashActivity.DAILY_LIMIT ;
                SplashActivity.twoDaysPassed = false;
            } else {

                //Else, retrieve the counter from Shared Prefs to show last displayed card
                SharedPreferences prefs = getActivity().getSharedPreferences(SplashActivity.LPT_PREFS, Context.MODE_PRIVATE);
                counter = prefs.getInt(MainActivity.COUNTER_KEY, SplashActivity.globalNmbrOfTips - SplashActivity.DAILY_LIMIT);
            }

            doOnStart = false;
        }



        //Bind UI Elements to Code
        mCardContent = view.findViewById(R.id.cardContent);
        mCardID = view.findViewById(R.id.cardID);
        mCardCategory = view.findViewById(R.id.cardCategory);
        mBookmark = view.findViewById(R.id.bookmarkButton);
        mShare = view.findViewById(R.id.shareButton);
        mNewBadge = view.findViewById(R.id.newBadgeHome);
        mNext = view.findViewById(R.id.nextButtonHome);
        mPrev = view.findViewById(R.id.prevButtonHome);
        mCardView = view.findViewById(R.id.cardView);

        //Set starting text and text switching animations
        mCardContent.setCurrentText("Hello There!");
        mCardContent.setInAnimation(getActivity(), android.R.anim.fade_in);
        mCardContent.setOutAnimation(getActivity(), android.R.anim.fade_out);

        //Initialize card
        refreshCard(counter);

        //Next button functionality
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(counter == SplashActivity.globalNmbrOfTips - 1) {
                    counter = 0;
                } else {
                    counter++;
                }

                refreshCard(counter);
            }
        });

        //Prev button functionality
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(counter == 0) {
                    counter = SplashActivity.globalNmbrOfTips - 1;
                }
                else {
                    counter--;
                }

                refreshCard(counter);
            }
        });

        //Bookmark button functionality
        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SplashActivity.globalTips[counter].isBookmarked()){
                    mBookmark.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark));
                    SplashActivity.globalTips[counter].setBookmarked(false);
                } else {
                    mBookmark.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark_light_green));
                    SplashActivity.globalTips[counter].setBookmarked(true);
                }

            }
        });

        //Share button functionality
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = SplashActivity.globalTips[counter].getTip() + "\n\nShared by \"Lifehacks Unlocked.\"";
                String shareSubject = SplashActivity.globalTips[counter].getCategory();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

    }

    public void refreshCard(int counter) {

        //Update all texts
        mCardCategory.setText(SplashActivity.globalTips[counter].getCategory());
        mCardContent.setText(SplashActivity.globalTips[counter].getTip());
        mCardID.setText("#"+SplashActivity.globalTips[counter].getId());

        //Update bookmark icon color
        if(SplashActivity.globalTips[counter].isBookmarked()) {
            mBookmark.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark_light_green));
        }
        else {
            mBookmark.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark));
        }

        //Update 'New' badge
        if(SplashActivity.globalTips[counter].isNewTip()) {
            mNewBadge.setVisibility(View.VISIBLE);
        }
        else {
            mNewBadge.setVisibility(View.INVISIBLE);
        }
    }

}
