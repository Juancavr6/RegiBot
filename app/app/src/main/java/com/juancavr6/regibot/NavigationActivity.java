package com.juancavr6.regibot;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.juancavr6.regibot.R;
import com.juancavr6.regibot.ui.fragment.HomeFragment;
import com.juancavr6.regibot.ui.fragment.MoreFragment;
import com.juancavr6.regibot.ui.fragment.ParamsFragment;

public class NavigationActivity extends AppCompatActivity {

    BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        bottom_nav=findViewById(R.id.bottomNav);
        if (savedInstanceState == null) replaceFragment(new HomeFragment());
        bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if( id == R.id.item_home) {
                    replaceFragment(new HomeFragment());
                } else if (id == R.id.item_params) {
                    replaceFragment(new ParamsFragment());
                } else if (id == R.id.item_more) {
                    replaceFragment(new MoreFragment());
                }

                return true;
            }
        });
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }



}