package com.example.intro.model;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.intro.model.helpers.Location;
import com.example.intro.ui.MainActivity;

import java.io.IOException;

public class AudioPlayer {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    private Activity activity;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private Button btPlay;
    private Button btStop;
    private Button btRecord;

    private Location file;


    public AudioPlayer(Button play, Button stop, Button rec,
                       Location file, Activity activity) {
        preparePlayButton(play);
        prepareStopButton(stop);
        prepareRecButton(rec);
        this.file = file;
        this.activity = activity;
    }


    private void preparePlayButton(Button play) {
        this.btPlay = play;
        btPlay.setOnClickListener(v -> {
            startPlaying();
            Toast.makeText(activity, "Playing",
                    Toast.LENGTH_SHORT).show();
        });
    }


    private void prepareStopButton(Button stop) {
        this.btStop = stop;
        btStop.setOnClickListener(v -> {
            stopRecording();
            Toast.makeText(activity, "Stopped",
                    Toast.LENGTH_SHORT).show();
        });
    }


    private void prepareRecButton(Button rec) {
        this.btRecord = rec;
        btRecord.setOnClickListener(v -> {
            startRecording();
            Toast.makeText(activity, "Started",
                    Toast.LENGTH_SHORT).show();
        });
    }


    private void startRecording() {
        if (recorder == null) prepareRecorder();
        recorder.start();
    }


    private void prepareRecorder() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(file.getFullPath(activity));
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                recorder.prepare();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }


    private void startPlaying() {
        if (player == null) preparePlayer();
        player.start();
    }


    private void preparePlayer() {
        if (player == null) {
            player = new MediaPlayer();
            try {
                player.setDataSource(file.getFullPath(activity));
                player.prepare();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}