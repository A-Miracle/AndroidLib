package a.miracle.lib_zxing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

import a.miracle.lib_zxing.camera.CameraManager;
import a.miracle.lib_zxing.decoding.CaptureActivityHandler;
import a.miracle.lib_zxing.decoding.InactivityTimer;
import a.miracle.lib_zxing.view.ViewfinderView;

/**
 * @author juns
 * @ClassName: CaptureAct
 * @Description: 二维码扫描
 * @date 2013-8-14
 */
public class CaptureAct extends AppCompatActivity implements Callback {

    private static final String TAG = "CaptureAct";

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private static final long VIBRATE_DURATION = 200L;

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;
    private View cl_scan;

    /**获取 setContent Id*/
    protected int getLayoutId() {
        return R.layout.act_qr_scan;
    }

    /** 设置状态栏 */
    protected void initStatusBar() {
    }

    /**setContent之后*/
    protected void onAfterSetContentLayout(@Nullable Bundle savedInstanceState) { }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestMail();

        // setContentView
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }

        initStatusBar();

        onAfterSetContentLayout(savedInstanceState);

        surfaceView = findViewById(R.id.preview_view);
        viewfinderView = findViewById(R.id.viewfinder_view);
        cl_scan = findViewById(R.id.cl_scan);

        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    protected void startScan(){
        /*continuePreview();
        cl_scan.setVisibility(View.VISIBLE);*/

        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        if (!hasSurface) {
            hasSurface = true;
            continuePreview();
        }
        cl_scan.setVisibility(View.VISIBLE);
    }

    protected void stopScan(){
        /*if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        cl_scan.setVisibility(View.GONE);*/

        cl_scan.setVisibility(View.GONE);

        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();

        if (inactivityTimer != null){
            inactivityTimer.shutdown();
        }

        hasSurface = false;
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        if (inactivityTimer != null){
            inactivityTimer.shutdown();
        }
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    /** 画取景器 */
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /** 扫描正确后的震动声音,如果感觉apk大了,可以删除 */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    /** 播放声音和震动 */
    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = mediaPlayer -> mediaPlayer.seekTo(0);

    /** 处理扫描结果 */
    public void handleDecode(Result result, Bitmap barcode) {
        Log.i(TAG, "CaptureAct -> handleDecode :" + result);
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate(); // 播放声音震动

        final String resultString = result.getText();
        //---返回结果给上一个页面---//
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("result", resultString);
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);

        disposeResult(result.getText());
    }

    /** 处理解析结果 */
    protected void disposeResult(String result) {
        // TODO 处理解析结果
    }

    protected void continuePreview(){
        initCamera(surfaceView.getHolder());
        if(handler != null){
            handler.restartPreviewAndDecode();
        }
    }

    //----------------------------------------------------------------------------------------------

    private static final int RESULT_CODE_START_CAMERA = 0xA;
    protected boolean hasPermission;
    private String[] mPermissionList = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE};

    //判断应用是否已经授权权限
    public boolean requestMail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission =
                    + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            + ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE);
            // 没有授权
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                //进行授权提示 101为返回标识
                ActivityCompat.requestPermissions(this, mPermissionList, RESULT_CODE_START_CAMERA);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case RESULT_CODE_START_CAMERA:
                if (grantResults.length > 0){
                    hasPermission = (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED;
                }
                break;
        }
    }
}
