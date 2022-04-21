package com.example.note_app.example.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.note_app.R;
import com.example.note_app.example.fragment.SoundFragment;

public class SoundActivity extends AppCompatActivity {


    public SoundActivity() {
        super(R.layout.activity_sound);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, SoundFragment.class, null)
                    .commit();
        }
    }
}