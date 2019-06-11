package com.example.azzem.chatty;

import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.azzem.chatty.Adapter.IntroViewPagerAdapter;
import com.example.azzem.chatty.Model.ScreenItem;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Toolbar toolbar;
    Button btn_next, btn_skip;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //When the activity is about to be launch we need to check if its opened before or not
        if(restorePrefData()){
            Intent start_intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(start_intent);
            finish();
        }

        setContentView(R.layout.activity_intro);
        //hide the action bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();


        //init views
        tabIndicator = findViewById(R.id.tab_indicator);
        btnGetStarted = findViewById(R.id.btn_get_started);
        btn_next = findViewById(R.id.btn_next);
        btn_skip = findViewById(R.id.btn_skip);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);


        //fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Hey Ya! \n It's time to be social","The easiest and most fun way of instant messaging...Connect with your contacts...Anytime and anywhere.",R.drawable.first));
        mList.add(new ScreenItem("Do real time messaging ","Fun with Friends and family ... Meet new people and more...",R.drawable.second));
        mList.add(new ScreenItem("Free !","Use app name for free and without ads .",R.drawable.ig3));

        //setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //setup tablayout with viewpager (right,center,left disparaitre (disappear)
        tabIndicator.setupWithViewPager(screenPager);

        // next button click listener

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if(position < mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if(position == mList.size()-1){ //when we reach to the last screen
                    //TODO : show the GETSTARTED Button and hide the indicator and the next button.

                    loaddLastScreen();

                }
            }
        });

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });


        //tablayout add change listener

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == mList.size()-1){
                    //le bouton getstarted sera visible sans cliquer sur NEXT just by scrolling horizontal
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open main/start activity
                Intent startIntent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(startIntent);
                //also we need to save a boolean value to storage so next time when the user run the app
                //we could now that he is already checked the intro screen activity
                //I'm going to use shared preference to the process
                savePrefData();
                finish();
            }
        });

    }

    private boolean restorePrefData() {
        //les SharedPreferencesAPI permettent de lire et d'écrire des paires clé-valeur.
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        //Si la valeur de la clé n'existe pas, alors retourne la deuxième valeur param - Dans ce cas, null
        //getBoolean ---> récupération
        boolean isIntroActivityOpenedBefore = preferences.getBoolean("isIntroOpened", false);
        return  isIntroActivityOpenedBefore;
    }

    private void savePrefData() {
        //les SharedPreferencesAPI permettent de lire et d'écrire des paires clé-valeur.
        //Créer des préférences partagées---
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //---
        //Définir la valeur de préférence
        //Stockage des données sous forme de paire KEY / VALUE
        editor.putBoolean("isIntroOpened",true);     //Saving boolean - true/false
        // Save the changes in SharedPreferences
        editor.commit(); // // commit changes
    }

    //show the GETSTARTED Button and hide the indicator and the next button.
    private void loaddLastScreen() {
        btn_next.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btn_skip.setVisibility(View.INVISIBLE);
        //TODO / ADD an animation to the GETSTARTED Button
        //lets create the button animation
        //setup animation
        btnGetStarted.setAnimation(btnAnim);
    }
}
