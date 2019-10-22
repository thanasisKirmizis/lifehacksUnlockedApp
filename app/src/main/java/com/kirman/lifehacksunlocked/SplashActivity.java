package com.kirman.lifehacksunlocked;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

    /****** This activity starts loading cards before user hits 'Enter' button ******/

    //Constants
    public static final int DAILY_LIMIT = 5;
    private final int FIRST_BUNCH_LIMIT = 100;
    private final String API_URL_DAILY = "https://www.reddit.com/r/LifeProTips/top.json?t=day&limit="+ String.valueOf(DAILY_LIMIT);
    private final String API_URL_YEARLY = "https://www.reddit.com/r/LifeProTips/top.json?t=year&limit="+ String.valueOf(FIRST_BUNCH_LIMIT);
    public static final String LPT_PREFS = "LPTPrefs";
    public static final String TIPS_NMBR_KEY = "tipsNmbr";
    public static final String DATE_KEY = "date";
    private final long TWO_DAYS_IN_MS = 172800000;
    public static final int MAX_TIPS = 1000;

    //Static Entities
    public static CardEntity[] globalTips = new CardEntity[MAX_TIPS];
    public static int globalNmbrOfTips = 0;

    //UI Elements
    Button mEnterButton;
    TextView mMotto;

    //Just some vars
    static boolean tempFlag = false;
    public static boolean initialized;
    public static boolean twoDaysPassed = false;
    boolean isFirstTime = false;
    int limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //Means that app has enough resources to go on
        initialized = true;

        //Bind UI elements to code
        mEnterButton = findViewById(R.id.enterButton);
        mMotto = findViewById(R.id.motto);

        //Do a fancy animation
        Animation mottoAnimation = AnimationUtils.loadAnimation(this,R.anim.text_float);
        mMotto.startAnimation(mottoAnimation);

        //Retrieve the buffer pointer from Shared Prefs
        SharedPreferences prefs = getSharedPreferences(SplashActivity.LPT_PREFS, MODE_PRIVATE);
        globalNmbrOfTips = prefs.getInt(SplashActivity.TIPS_NMBR_KEY, 0);

        //Retrieve the tips from Shared Prefs
        Gson gson = new Gson();
        for(int i = 0; i< globalNmbrOfTips; i++){

            String json = prefs.getString("tips"+String.valueOf(i), "");
            globalTips[i] = gson.fromJson(json, CardEntity.class);
        }

        //Implementing time logic here
        long prevTime = prefs.getLong(SplashActivity.DATE_KEY, 0);
        long nowTime = System.currentTimeMillis();

        /*** Decide how to load the tips here ***/

        if(globalNmbrOfTips > MAX_TIPS - 6) {
            //If buffer is about to get filled, start over with a new bunch
            globalNmbrOfTips = 0;
            limit = FIRST_BUNCH_LIMIT;
            talkWithController(limit, API_URL_YEARLY, isFirstTime);
        }
        else if(prevTime == 0) {
            //First Time: Load the bunch
            isFirstTime = true;
            limit = FIRST_BUNCH_LIMIT;
            talkWithController(limit, API_URL_DAILY, isFirstTime);
        }
        else if(nowTime - TWO_DAYS_IN_MS > prevTime) {

            twoDaysPassed = true;

            if(isNetworkConnected(SplashActivity.this)){
                //Add 5 more cards
                isFirstTime = false;
                limit = DAILY_LIMIT;
                talkWithController(limit, API_URL_DAILY, isFirstTime);
            } else {
                Toast.makeText(SplashActivity.this, "Connect to the internet to display new tips!", Toast.LENGTH_LONG).show();
                tempFlag = true;
            }

        }
        else {
            //Just load the already existing ones
            tempFlag = true;
        }

        //Enter button functionality
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //tempFlag = true means that all necessary data have been loaded
                if(tempFlag){

                    Intent goToHomeIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(goToHomeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {

                    Toast.makeText(SplashActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //Function to check whether there is internet connection available
    public static boolean isNetworkConnected(Context context) {
        final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {

                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
        }

        return false;
    }

    public void talkWithController(int cLimit, String url, boolean firstTime) {

        //Create new Card Controller
        CardController controller = new CardController(cLimit, url, SplashActivity.this);

        //Let the controller handle all the networking
        InputStream is = getResources().openRawResource(R.raw.first_time);
        controller.redditAPIRequest(firstTime, is);

    }
}
