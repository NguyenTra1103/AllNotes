package com.example.note_app.example.fragment;

import static com.example.note_app.R.id.action_soundFragment_to_soundListFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.note_app.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class SoundFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private ImageView listBtn;
    private ShapeableImageView recordBtn;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private  int PERMISSION_CODE = 21;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer record_timer;
    private TextView filenameText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.recordBtn);
        record_timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);

        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_list_btn:
                if (isRecording){

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_soundFragment_to_soundListFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("Cancel",null);
                    alertDialog.setTitle("Sound is still recording");
                    alertDialog.setMessage("Are you sure, you want to stop the recording?");
                    alertDialog.create().show();
                }else{
                    navController.navigate(R.id.action_soundFragment_to_soundListFragment);
                }
                break;
            case R.id.recordBtn:
                if(isRecording){
                    stopRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_stop,null));
                    isRecording = false;
                }else{
                    if(checkPermission()) {
                        startRecording();
                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic, null));
                        isRecording = true;
                    }
                }
                    break;

        }
    }

    private void stopRecording() {
        record_timer.stop();
        filenameText.setText("Recording Stopped, File Saved: " + recordFile);
        mediaRecorder.start();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording(){
        record_timer.setBase(SystemClock.elapsedRealtime());
        record_timer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", new Locale("vi","VN"));
        Date now = new Date(SystemClock.elapsedRealtime());
        recordFile = "Recording_" + formater.format(now)+ ".3gp";
        filenameText.setText("Recording, filename: " + recordFile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }
    private boolean checkPermission() {
       if(ActivityCompat.checkSelfPermission(getContext(),recordPermission)== PackageManager.PERMISSION_GRANTED){
           return true;
       }else {
           ActivityCompat.requestPermissions(getActivity(),new String[]{recordPermission},PERMISSION_CODE);
           return false;
       }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording) {
            stopRecording();
        }
    }
}