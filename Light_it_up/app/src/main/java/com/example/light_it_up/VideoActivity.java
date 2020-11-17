package com.example.light_it_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean recording = false;
    private ArrayList<String> urlString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        SmsManager smsManager = SmsManager.getDefault();
        String sendTo = "01090856697";
        String myMessage = "help me";
        smsManager.sendTextMessage(sendTo, null, myMessage, null, null);
        Toast.makeText(getBaseContext(), "메세지 신고 완료", Toast.LENGTH_SHORT).show();

        camera = Camera.open();
        camera.setDisplayOrientation(90);
        surfaceView = (SurfaceView) findViewById(R.id.surface1);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(VideoActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaRecorder = new MediaRecorder();
                    urlString = new ArrayList<String>();
                    camera.unlock();
                    mediaRecorder.setCamera(camera);
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
                    mediaRecorder.setOrientationHint(90);
                    mediaRecorder.setOutputFile("/sdcard/1.mp4");
                    mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                recording = true;
                                new Handler().postDelayed(new Runnable() {// 5 초 후에 녹화 종료
                                    @Override
                                    public void run() {
                                        mediaRecorder.stop();
                                        mediaRecorder.release();
                                        camera.lock();
                                        onBackPressed();
                                        finish();
                                    }
                                }, 5000);
                            } catch (IOException e) {
                                Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }, 300);
                } catch (Exception e) {
                    Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    mediaRecorder.release();
                }
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    private void refreshCamera(Camera camera) {
        if (surfaceHolder.getSurface() == null) return;
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCamera(camera);
    }

    private void setCamera(Camera cam) {
        camera = cam;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }
}