package com.gaos.mediaplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.media.ThumbnailUtils;
import android.media.TimedMetaData;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Author:　Created by benjamin
 * DATE :  2017/7/27 17:42
 * versionCode:　v2.2
 */

public class SurfaceViewMediaPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private SurfaceView surfaceView;
    private Button btnPlay;
    private Button btnPause;
    private Button btnReplay;
    private Button btnStop;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;

    String mp4Url = "http://192.168.4.51:8080/benjamin/eason.mp4";
    private boolean isPlaying;
    private static final String TAG = "SurfaceViewMediaPlayerA";
    private ImageView ivLayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_media);

        surfaceView = (SurfaceView) findViewById(R.id.view_surface);
        //设置顶层，避免SurfaceView所占据空间被穿透。
        surfaceView.setZOrderOnTop(true);


        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnReplay = (Button) findViewById(R.id.btn_replay);
        btnStop = (Button) findViewById(R.id.btn_stop);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        surfaceView.getHolder().addCallback(callbcakSurface);

        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnReplay.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);


        new RetriveVideoFrameFromVideoAsyncTask().execute(mp4Url);
//

        ivLayer = (ImageView) findViewById(R.id.iv_layer);
//        ivLayer.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float factor = 852.0f / 480;

//        ViewGroup.LayoutParams params = ivLayer.getLayoutParams();
//        params.width = displayMetrics.widthPixels;
//        params.height = (int) (displayMetrics.widthPixels * 1.0f / factor);
//        ivLayer.setLayoutParams(params);
//
//
//        surfaceView.getHolder().setFixedSize(displayMetrics.widthPixels, (int) (displayMetrics.widthPixels * 1.0f / factor));
//        surfaceView.setVisibility(View.VISIBLE);

        FrameLayout flVideo = (FrameLayout) findViewById(R.id.fl_video);
        ViewGroup.LayoutParams params = flVideo.getLayoutParams();
        params.width = displayMetrics.widthPixels;
        params.height = (int) (displayMetrics.widthPixels * 1.0f / factor);
        flVideo.setLayoutParams(params);

        surfaceView.setVisibility(View.VISIBLE);

    }

    SurfaceHolder.Callback callbcakSurface = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {


        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_play:

                play(0);
                break;

            case R.id.btn_pause:

                pause();

                break;

            case R.id.btn_replay:

                replay();
                break;

            case R.id.btn_stop:
                stop();

                break;

            default:
                break;

        }
    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * @param msec 播放起点
     */
    private void play(final int msec) {

        if (mediaPlayer == null) {

            mediaPlayer = new MediaPlayer();

        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(mp4Url);

            mediaPlayer.setDisplay(surfaceView.getHolder());

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                    if (ivLayer != null) {

                        ivLayer.setVisibility(View.GONE);
                    }

                    mediaPlayer.start();
//                            mediaPlayer.pause();
//                            mediaPlayer.pause();
                    mediaPlayer.seekTo(msec);

                    isPlaying = true;

                    seekBar.setMax(mediaPlayer.getDuration());


                    mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mp, int percent) {
                            float buffer = percent * 1.0f / 100 * mediaPlayer.getDuration();

                            seekBar.setSecondaryProgress((int) buffer);


                        }
                    });
//
//                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    mediaPlayer.pause();
//                                }
//                            },1000);


//                            surfaceView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    mediaPlayer.start();
//                                }
//                            });

                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            while (isPlaying) {

                                final int currentPosition = mediaPlayer.getCurrentPosition();


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        seekBar.setProgress(currentPosition);

                                    }
                                });

                                try {
                                    Thread.sleep(500);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                    btnPlay.setEnabled(false);
                }
            });
//                }
//            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlay.setEnabled(true);
                    isPlaying = false;
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    isPlaying = false;
                    play(0);
                    return false;
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重新开始播放
     */
    private void replay() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            mediaPlayer.seekTo(0);
            Toast.makeText(this, "重新播放", Toast.LENGTH_SHORT).show();
            btnPause.setText("暂停");
            return;
        }

        play(0);
        isPlaying = false;

    }

    private void pause() {

        if (TextUtils.equals("暂停", btnPause.getText().toString().trim())) {

            btnPause.setText("继续");

            mediaPlayer.pause();

            return;
        }

        if (TextUtils.equals("继续", btnPause.getText().toString().trim())) {

            btnPause.setText("暂停");

            mediaPlayer.start();
        }
    }

    private void stop() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btnPlay.setEnabled(true);
            isPlaying = false;
        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();


        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private class RetriveVideoFrameFromVideoAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private Bitmap retriveVideoFrameFromVideo;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                retriveVideoFrameFromVideo = retriveVideoFrameFromVideo(params[0]);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return retriveVideoFrameFromVideo;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {

//                Canvas canvas = new Canvas(bitmap);

//                surfaceView.draw(canvas);

//                surfaceView.getHolder().setFixedSize(bitmap.getWidth(), bitmap.getHeight());

//
//                int width = bitmap.getWidth();
//                int height = bitmap.getHeight();
//
//                DisplayMetrics displayMetrics =
//                        getResources().getDisplayMetrics();
//
//                float scaleFractor = width * 1.0f / displayMetrics.widthPixels;
//
//                float newHeight = height * 1.0f / scaleFractor;


//                surfaceView.getHolder().setFixedSize(displayMetrics.widthPixels, (int) newHeight);


                ivLayer.setImageBitmap(bitmap);


                /**
                 * onPostExecute: width = 852
                 * onPostExecute: height = 480
                 *
                 * 视屏的宽高比必须提前告知。
                 */
//                Log.e(TAG, "onPostExecute: width = " + width);
//                Log.e(TAG, "onPostExecute: height = " + height);


            }
        }
    }
}
