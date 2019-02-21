package com.mygod_control.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mygod_control.Config;
import com.mygod_control.R;
import com.mygod_control.message.MessageManager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private static final String TAG = MainActivity.class.getName();
    private static final int FORWARD = 0x0000;
    private static final int BACK = 0x0001;
    private static final int LEFT = 0x0010;
    private static final int RIGHT = 0x0011;
    private String path = "http://47.105.182.128:32828/live/test.flv";
    private Uri uri;
    private Timer timer;
    @BindView(R.id.btn_forward)
    Button btnForward;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_left)
    Button btnLeft;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.btn_take)
    Button btnTake;
    @BindView(R.id.btn_temp)
    Button btnTemp;
    @BindView(R.id.btn_location)
    Button btnLocation;
    @BindView(R.id.btn_horn)
    Button btnHorn;
    @BindView(R.id.btn_reboot)
    Button btnReboot;
    @BindView(R.id.vv_screen)
    VideoView vvScreen;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    @BindView(R.id.tv_download)
    TextView tvDownload;
    @BindView(R.id.tv_load)
    TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnForward.setOnTouchListener(this);
        btnBack.setOnTouchListener(this);
        btnLeft.setOnTouchListener(this);
        btnRight.setOnTouchListener(this);

        btnTake.setOnClickListener(this);
        btnTemp.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        btnHorn.setOnClickListener(this);
        btnReboot.setOnClickListener(this);

        uri = Uri.parse(path);
        vvScreen.setVideoURI(uri);
        vvScreen.setMediaController(new MediaController(this));
        vvScreen.requestFocus();
        vvScreen.setOnInfoListener(this);
        vvScreen.setOnBufferingUpdateListener(this);
        vvScreen.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onButtonDown(v);
        }
        if ((event.getAction() == MotionEvent.ACTION_UP)) {
            onButtonUp(v);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take:
                MessageManager.getInstance().sendMessage(Config.CMD_TAKE_PIC);
                break;
            case R.id.btn_temp:
                MessageManager.getInstance().sendMessage(Config.CMD_GET_TEMP);
                break;
            case R.id.btn_location:
                MessageManager.getInstance().sendMessage(Config.CMD_GET_LOCATION);
                break;
            case R.id.btn_horn:
                MessageManager.getInstance().sendMessage(Config.CMD_HORN);
                break;
            case R.id.btn_reboot:
                MessageManager.getInstance().sendMessage(Config.CMD_REBOOT);
                break;
            default:
                break;
        }
    }

    private void onButtonUp(View view) {
        stopCountDown();
    }

    private void onButtonDown(View view) {
        switch (view.getId()) {
            case R.id.btn_forward:
                startCountDown(FORWARD);
                break;
            case R.id.btn_back:
                startCountDown(BACK);
                break;
            case R.id.btn_left:
                startCountDown(LEFT);
                break;
            case R.id.btn_right:
                startCountDown(RIGHT);
                break;
            default:
                break;
        }
    }

    private void startCountDown(final int dir) {
        stopCountDown();
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    String commend = null;
                    switch (dir) {
                        case FORWARD:
                            commend = Config.CMD_FORWARD;
                            break;
                        case BACK:
                            commend = Config.CMD_BACK;
                            break;
                        case LEFT:
                            commend = Config.CMD_LEFT;
                            break;
                        case RIGHT:
                            commend = Config.CMD_RIGHT;
                            break;
                        default:
                            break;
                    }

                    if (!TextUtils.isEmpty(commend)) {
                        MessageManager.getInstance().sendMessage(commend);
                    }
                }
            };
            timer.schedule(task, 0, 200);
        }
    }

    private void stopCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountDown();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "loading " + percent + "%");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (vvScreen.isPlaying()) {
                    vvScreen.pause();
                    pbProgress.setVisibility(View.VISIBLE);
                    tvDownload.setText("");
                    tvLoad.setText("");
                    tvDownload.setVisibility(View.VISIBLE);
                    tvLoad.setVisibility(View.VISIBLE);

                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                vvScreen.start();
                pbProgress.setVisibility(View.GONE);
                tvDownload.setVisibility(View.GONE);
                tvLoad.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                tvDownload.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }
}
