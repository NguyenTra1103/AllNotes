package com.example.note_app.example.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.note_app.R;
import com.example.note_app.example.adapter.SoundListAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

public class SoundListFragment extends Fragment implements SoundListAdapter.onItemListClick{

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView sound_list_view;
    private File[] allFiles;
    private SoundListAdapter soundListAdapter;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File filetoPlay;
    private ImageButton player_btn;
    private TextView player_filename;
    private TextView playerHeader;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    public SoundListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sound_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerSheet = view.findViewById(R.id.player_sheet);
        sound_list_view = view.findViewById(R.id.sound_list_view);
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        soundListAdapter = new SoundListAdapter(allFiles, this);
        sound_list_view.setHasFixedSize(true);
        sound_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        sound_list_view.setAdapter(soundListAdapter);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        player_btn = view.findViewById(R.id.player_btn);
        player_filename = view.findViewById(R.id.player_filename);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerSeekbar = view.findViewById(R.id.player_seekbar);
        player_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseSound();
                }else{
                    if(filetoPlay != null) {
                        resumeSound();
                    }
                }
            }
        });
        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                pauseSound();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeSound();
            }
        });

    }

    @Override
    public void onClickListener(File file, int position) {
        if(isPlaying){
            stopSound();
            playSound(filetoPlay);
        }else {
            filetoPlay= file;
            playSound(filetoPlay);
        }
    }
    private void pauseSound(){

        mediaPlayer.pause();
        player_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_player,null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
    private void resumeSound(){
        mediaPlayer.start();
        player_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_pause_btn,null));
        isPlaying = true;
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar,0);
    }

    private void stopSound() {
        player_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_player,null));
        playerHeader.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
    }

    private void playSound(File filetoPlay) {
        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(filetoPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_pause_btn,null));
        player_filename.setText(filetoPlay.getName());
        playerHeader.setText("Playing");
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSound();
                playerHeader.setText("Finish");
            }
        });
        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        stopSound();
    }
}