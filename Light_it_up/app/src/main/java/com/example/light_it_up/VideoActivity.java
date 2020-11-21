package com.example.light_it_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
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

        //동영상 저장을 위해 추가한 부분
        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

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
                    mediaRecorder.setOutputFile("/sdcard/report.mp4");
                    //mediaRecorder.setOutputFile("android.resource://com.example.light_it_up/drawable/title.mp4");
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


                                        Uri file = Uri.fromFile(new File("/sdcard/report.mp4"));
                                        StorageReference videosRef = storageRef.child("videos/" + file.getLastPathSegment());
                                        UploadTask uploadTask = videosRef.putFile(file);


                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle unsuccessful uploads
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                // ...
                                            }
                                        });

                                        SmsManager smsManager = SmsManager.getDefault();
                                        String sendTo = getString(R.string.phone_number);
                                        String myVideoMessage = "https://firebasestorage.googleapis.com/v0/b/lightitup-c4c0d.appspot.com/o/videos%2Freport.mp4?alt=media&token=7ffad23f-5dcd-4970-823b-f0c1ef2f3983";
                                        smsManager.sendTextMessage(sendTo, null, myVideoMessage, null, null);
                                        // 긴급 동영상 메세지 신고 전송 기능 구현

                                        finish();
                                    }
                                }, 5000);
                            } catch (IOException e) {
                                Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }, 500);
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