package com.ahofama.nextclass;

import   androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.ahofama.nextclass.databinding.DasboardActivityBinding;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity{

    DasboardActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DasboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.explore:
                    replaceFragment(new ExploreFragment());
                    break;
                case R.id. course:
                    replaceFragment(new MycourseFragment());
                    break;
                case R.id.wishlist:
                    replaceFragment(new WishlistFragment());
                    break;


            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment );
        fragmentTransaction.commit();

    }
}