package com.example.azzem.chatty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzem.chatty.Fragments.ChatsFragment;
import com.example.azzem.chatty.Fragments.GroupsFragment;
import com.example.azzem.chatty.Fragments.HomeFragment;
import com.example.azzem.chatty.Fragments.PeopleFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private WifiManager wifiManager;
    private ImageView warningIcon;
    private TextView textViewNoInternetConnexion;
    private ConstraintLayout constraintLayoutConnexionInternet, constraintLayoutConnexionInternet2;
    private BottomNavigationView bottomNav;
    private DatabaseReference refernce;
    private FirebaseUser firebaseUser;
    private Intent i;
    //Typeface myfont;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize controllers
        initViews();
        //-----
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //-----
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Clicks in items to go to fragments --> Method bottomNavigation...
        //Now i pass this listener to bottom Navigation
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //bottomNav.setItemIconTintList(null);
        //The container is empty when the ctivity opened
        //Now to show first fragment when the activity is opened
        //And i pass the first fragment to show...
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ChatsFragment()).commit();
        //myfont = Typeface.createFromAsset(this.getAssets(), "fonts/Quicksand-Light.otf");

        //--------------
        i = getIntent();
        String data = i.getStringExtra("a");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new GroupsFragment());
        fragmentTransaction.commit();
        bottomNav.setSelectedItemId(R.id.nav_groups);
        //--------------

    }

    private void initViews()
    {
        bottomNav = findViewById(R.id.bottom_navigation);
        constraintLayoutConnexionInternet = findViewById(R.id.constraint_layout_connexion);
        constraintLayoutConnexionInternet2 = findViewById(R.id.constraint_layout_Connexion);
        textViewNoInternetConnexion = findViewById(R.id.textView_noInternetConnexion);
        warningIcon = findViewById(R.id.icon_NoInternetConnexion);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item)
                {
                    Fragment selectedFragment = null;
                    //Set a reference to item i want to open
                    switch(item.getItemId())
                    {
                        case R.id.nav_chats:
                            selectedFragment = new ChatsFragment();
                            break;
                        case R.id.nav_groups:
                            selectedFragment = new GroupsFragment();
                            break;
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_people:
                             selectedFragment = new PeopleFragment();
                    }
                    //Pass a fragment twhich i want to show
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,selectedFragment).commit();
                    //true means that i want to select clicked item
                    return true;
                    }
                };


    //----------------CREATE ANE REGISTER BROADCAST RECEIVER---//

    //Register
    //Every time onStart called my receiver check the wifiState and changes.
    @Override
    protected void onStart()
    {
        super.onStart();
        //If the wifi is changed
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //Activate the broadcast receiver in the onStart
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //Unregister the broadcast receiver. When w leave the activity we don't need it.
        unregisterReceiver(wifiStateReceiver);
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //Intent contain information about wifi state.
            //For default value i write WifiManager.WIFI_STATE_UNKNOWN if i don't receive anythings the wifi is unknown
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            //Testing.
            switch(wifiStateExtra)
            {
                case WifiManager.WIFI_STATE_ENABLED:
                    //test if the wifi is enabled when i receive the broadcast.
//                    Snackbar.make(findViewById(R.id.fragment_container), "WIFI IS ON", Snackbar.LENGTH_LONG).show();
//                    Toast.makeText(MainActivity.this, "WIFI IS ON", Snackbar.LENGTH_LONG).show();
                      constraintLayoutConnexionInternet.setVisibility(View.GONE);
                      constraintLayoutConnexionInternet2.setVisibility(View.VISIBLE);
                      int delayMilis = 2700;
                      Handler handler = new Handler();
                      final View v = constraintLayoutConnexionInternet2;
                      handler.postDelayed(new Runnable()
                      {
                          @Override
                          public void run()
                          {
//                              TranslateAnimation animation = new TranslateAnimation(0, -v.getWidth(), 0,0);
//                              animation.setDuration(800);
//                              animation.setFillAfter(true);
//                              v.startAnimation(animation);
                              v.setVisibility(View.GONE);
                          }
                      }, delayMilis);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
//                    Snackbar.make(findViewById(R.id.fragment_container), "WIFI IS OFF", Snackbar.LENGTH_LONG).show();
//                    Toast.makeText(MainActivity.this, "No connexion", Snackbar.LENGTH_LONG).show();
                    if(constraintLayoutConnexionInternet != null)
                    {
                        constraintLayoutConnexionInternet2.setVisibility(View.GONE);
                        constraintLayoutConnexionInternet.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
    //----------

//    private void status(String status)
//    {
//        refernce = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//        //Save data
//        refernce.updateChildren(hashMap);
//    }
//
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        status("online");
//        //Yousra check vedio again in logout
//    }
//
//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        status("offline");
//    }
}

