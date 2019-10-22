package com.kirman.lifehacksunlocked;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import cz.msebera.android.httpclient.Header;

public class CardController {

    private int limit;
    private String url;
    private Context mContext;

    public CardController(int limit, String url, Context mContext) {
        this.limit = limit;
        this.url = url;
        this.mContext = mContext;
    }

    public void redditAPIRequest(boolean isFirstTime, InputStream inputStream) {

        if(isFirstTime){

            //Load a bunch of tips stored locally in JSON
            InputStream is = inputStream;
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            JSONObject response = new JSONObject();

            try {
                response = new JSONObject(writer.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Call the function to fill the cards to be displayed
            fillCards(response);
            //Set the flag to true so that user can enter the Main Activity
            SplashActivity.tempFlag = true;

        } else {

            //Speak with the Reddit API and get Response from there
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    //Call the function to fill the cards to be displayed
                    fillCards(response);
                    //Set the flag to true so that user can enter the Main Activity
                    SplashActivity.tempFlag = true;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.e("LifeProTips", "Fail " + e.toString());
                    Log.d("LifeProTips", "Status code " + statusCode);
                }

            });

        }

    }

    //Parses the JSON response accordingly and extracts tip and category for the given number of cards
    private void fillCards(JSONObject response) {

        for(int i=0; i<limit; i++){

            String title = "";
            String cat = "";

            try {
                title = response.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getString("title");
                title = clearPrefix(title);

                cat = response.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getString("link_flair_text");

                //Keep a clean format for the category string
                cat = cat.replace("&amp;", "&");
                cat = cat.replace("Removed: ", "&");
                cat = cat.replace("Removed:", "&");

                if(cat.equals("null")) {
                    cat = "Miscellaneous";
                }

                if(checkDuplicate(title)){

                    SplashActivity.globalTips[SplashActivity.globalNmbrOfTips] = new CardEntity(title, cat, SplashActivity.globalNmbrOfTips +1);
                    SplashActivity.globalNmbrOfTips++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //After loading all the new tips, store the new value of the buffer pointer and the new tips to Shared Prefs
        saveDataToPrefs();

    }

    private void saveDataToPrefs() {

        SharedPreferences prefs = mContext.getSharedPreferences(SplashActivity.LPT_PREFS, 0);
        //Store number of tips
        prefs.edit().putInt(SplashActivity.TIPS_NMBR_KEY, SplashActivity.globalNmbrOfTips).apply();

        //Store time of last update
        prefs.edit().putLong(SplashActivity.DATE_KEY, System.currentTimeMillis()).apply();

        //Store all the tips
        Gson gson = new Gson();
        for(int i = 0; i<SplashActivity.globalNmbrOfTips; i++) {

            String json = gson.toJson(SplashActivity.globalTips[i]);
            prefs.edit().putString("tips"+String.valueOf(i), json).apply();
        }
    }

    //Checks if a tip already have been loaded
    private boolean checkDuplicate(String title) {

        if((Boolean) title.equals("")){
            return false;
        }

        for(int i = 0; i<SplashActivity.globalNmbrOfTips; i++) {

            if(SplashActivity.globalTips[i].getTip().equals(title)) {
                return false;
            }
        }

        return true;
    }

    //Keeps a clean formatting of the tips to be displayed
    private String clearPrefix(String title) {

        String toRet = title.replace("LPT: ","");
        toRet = toRet.replace("LPT:","");
        toRet = toRet.replace("LPT- ","");
        toRet = toRet.replace("LPT-","");
        toRet = toRet.replace("LPT - ","");
        toRet = toRet.replace("LPT -","");
        toRet = toRet.replace("LPT : ","");
        toRet = toRet.replace("LPT :","");
        toRet = toRet.replace("LPT ","");
        toRet = toRet.replace("[LPT] ", "");
        toRet = toRet.replace("[LPT]", "");
        toRet = toRet.replace("LPT. ", "");
        toRet = toRet.replace("LPT.", "");

        toRet = toRet.replace("&amp;", "&");

        toRet = toRet.substring(0, 1).toUpperCase() + toRet.substring(1);

        return toRet;
    }


}
