package com.kirman.lifehacksunlocked;

import android.app.Activity;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class Fragment2 extends Fragment {

    private CardEntity[] tipsSubset = new CardEntity[SplashActivity.MAX_TIPS];
    private int nmbrOfSubsetTips = 0;

    //UI Elements
    private TextSwitcher mCardContent;
    private TextView mCardID;
    private TextView mCardCategory;
    private TextView mCategory;
    private ImageView mCatIcon1;
    private ImageView mCatIcon2;
    private ImageButton mBookmark;
    private ImageButton mShare;
    private ImageView mNewBadge;
    private TextView mNext;
    private TextView mPrev;

    //Just some vars
    private int counter = 0;
    String filter = "";

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

        filter = getArguments().getString(Fragment3.CHOSEN_FILTER_EXTRA);
        if(filter == null){
            filter = "";
        }

        return inflater.inflate(R.layout.fragment_filtered, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //Base activity to use as context
        final Activity base = Objects.requireNonNull(getActivity());

        if(filter.equals("Bookmarked")) {

            //Filter out the bookmarked tips
            for(int i = 0; i<SplashActivity.globalNmbrOfTips; i++) {

                boolean cond = SplashActivity.globalTips[i].isBookmarked();

                if (cond) {

                    String tip = SplashActivity.globalTips[i].getTip();
                    String cat = SplashActivity.globalTips[i].getCategory();
                    tipsSubset[nmbrOfSubsetTips] = new CardEntity(tip, cat, i+1);
                    tipsSubset[nmbrOfSubsetTips].setBookmarked(true);
                    tipsSubset[nmbrOfSubsetTips].setNewTip(SplashActivity.globalTips[i].isNewTip());
                    nmbrOfSubsetTips++;

                }
            }

        } else {

            //Filter out the tips for the specific category out of all tips
            for(int i = 0; i<SplashActivity.globalNmbrOfTips; i++) {

                String cat = SplashActivity.globalTips[i].getCategory();

                if(cat.equals(filter)) {

                    String tip = SplashActivity.globalTips[i].getTip();
                    tipsSubset[nmbrOfSubsetTips] = new CardEntity(tip, cat, i+1);
                    tipsSubset[nmbrOfSubsetTips].setBookmarked(SplashActivity.globalTips[i].isBookmarked());
                    tipsSubset[nmbrOfSubsetTips].setNewTip(SplashActivity.globalTips[i].isNewTip());
                    nmbrOfSubsetTips++;

                }
            }
        }

        //Set the counter so that on start, the latest tip is displayed
        counter = nmbrOfSubsetTips - 1;

        //Bind UI Elements to Code
        mCardContent = view.findViewById(R.id.cardContent);
        mCardID = view.findViewById(R.id.cardID);
        mCardCategory = view.findViewById(R.id.cardCategory);
        mCategory = view.findViewById(R.id.cat_name_filter);
        mCatIcon1 = view.findViewById(R.id.cat_icon_left_filter);
        mCatIcon2 = view.findViewById(R.id.cat_icon_right_filter);
        mBookmark = view.findViewById(R.id.bookmarkButton);
        mShare = view.findViewById(R.id.shareButton);
        mNewBadge = view.findViewById(R.id.newBadgeFilter);
        mNext = view.findViewById(R.id.nextButtonFilter);
        mPrev = view.findViewById(R.id.prevButtonFilter);

        //Set starting text and text switching animations
        mCardContent.setCurrentText("Hello There!");
        mCardContent.setInAnimation(base, android.R.anim.fade_in);
        mCardContent.setOutAnimation(base, android.R.anim.fade_out);

        //Initialize Category name
        mCategory.setText(filter);

        //Generate Category Icons
        generateCatIcons(filter);

        //If none in this category, print message
        if(nmbrOfSubsetTips == 0) {

            if(filter.equals("Bookmarked")){

                mCardCategory.setText(" ");
                mCardContent.setText("No tips bookmarked yet...");
                mNewBadge.setVisibility(View.INVISIBLE);
            } else {

                mCardCategory.setText(filter);
                mCardContent.setText("No tips for this category right now...");
                mNewBadge.setVisibility(View.INVISIBLE);
            }
            mCardID.setText("#?");

        } else {

            //Initialize Card
            refreshCard(counter);
        }

        //Next button functionality
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nmbrOfSubsetTips!=0){
                    if(counter == nmbrOfSubsetTips - 1) {
                        counter = 0;
                    } else {
                        counter++;
                    }

                    refreshCard(counter);
                }
            }
        });

        //Prev button functionality
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nmbrOfSubsetTips!=0){
                    if(counter == 0) {
                        counter = nmbrOfSubsetTips - 1;
                    } else {
                        counter--;
                    }

                    refreshCard(counter);
                }
            }
        });

        //Bookmark button functionality
        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nmbrOfSubsetTips!=0) {
                    if (tipsSubset[counter].isBookmarked()) {
                        mBookmark.setBackground(ContextCompat.getDrawable(base, R.drawable.ic_bookmark));
                        tipsSubset[counter].setBookmarked(false);
                        SplashActivity.globalTips[tipsSubset[counter].getId()-1].setBookmarked(false);
                    } else {
                        mBookmark.setBackground(ContextCompat.getDrawable(base, R.drawable.ic_bookmark_light_green));
                        tipsSubset[counter].setBookmarked(true);
                        SplashActivity.globalTips[tipsSubset[counter].getId()-1].setBookmarked(true);
                    }
                }
            }
        });

        //Share button functionality
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nmbrOfSubsetTips!=0) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = tipsSubset[counter].getTip() + "\n\nShared by \"Lifehacks Unlocked.\"";
                    String shareSubject = tipsSubset[counter].getCategory();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            }
        });


    }

    //Function to display Icons of chosen category
    public void generateCatIcons(String category) {

        Activity base = Objects.requireNonNull(getActivity());

        switch(category) {

            case "Careers & Work":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_career));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_career));
                break;
            case "Clothing":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_clothes));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_clothes));
                break;
            case "Computers":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_computer));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_computer));
                break;
            case "Electronics":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_electronics));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_electronics));
                break;
            case "Money & Finance":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_finance));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_finance));
                break;
            case "Health & Fitness":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_fitness));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_fitness));
                break;
            case "Food & Drink":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_food));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_food));
                break;
            case "Home & Garden":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_house));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_house));
                break;
            case "Animals & Pets":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_pets));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_pets));
                break;
            case "Productivity":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_productivity));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_productivity));
                break;
            case "Social":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_social));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_social));
                break;
            case "Traveling":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_travel));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_travel));
                break;
            case "School & College":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_education));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_education));
                break;
            case "Arts & Culture":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_art));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_art));
                break;
            case "Miscellaneous":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_misc));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_misc));
                break;
            case "Bookmarked":
                mCatIcon1.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_bookmark));
                mCatIcon2.setImageDrawable(ContextCompat.getDrawable(base, R.drawable.ic_bookmark));
                break;

        }
    }

    public void refreshCard(int counter) {

        //Update all texts
        mCardCategory.setText(tipsSubset[counter].getCategory());
        mCardContent.setText(tipsSubset[counter].getTip());
        mCardID.setText("#"+tipsSubset[counter].getId());

        //Update bookmark icon color
        if(tipsSubset[counter].isBookmarked()) {
            mBookmark.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark_light_green));
        }
        else {
            mBookmark.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark));
        }

        //Update 'New' badge
        if(tipsSubset[counter].isNewTip()) {
            mNewBadge.setVisibility(View.VISIBLE);
        }
        else {
            mNewBadge.setVisibility(View.GONE);
        }
    }
}
