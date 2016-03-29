package com.xlgzs.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class RecorderActivity extends Activity implements OnClickListener {
    private TextView start;
    private TextView stopAndConvert;

    private boolean canClean = false;

    AudioRecorder2Mp3Util util = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        start = (TextView) findViewById(R.id.start);
        stopAndConvert = (TextView) findViewById(R.id.stop);

        start.setOnClickListener(this);
        stopAndConvert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start:
            if (util == null) {
                util = new AudioRecorder2Mp3Util(null,
                        "/sdcard/test_audio_recorder_for_mp3.raw",
                        "/sdcard/test_audio_recorder_for_mp3.mp3");
            }
            if (canClean) {
                util.cleanFile(AudioRecorder2Mp3Util.MP3
                        | AudioRecorder2Mp3Util.RAW);
            }
            Toast.makeText(this, "��˵��", 0).show();

            util.startRecording();
            canClean = true;
            break;

        case R.id.stop:
            Toast.makeText(this, "����ת��", 0).show();
            util.stopRecordingAndConvertFile();
            Toast.makeText(this, "ok", 0).show();
            util.cleanFile(AudioRecorder2Mp3Util.RAW);
            // ���Ҫ�رտ���
            util.close();
            util = null;
            break;
        }
    }
}
