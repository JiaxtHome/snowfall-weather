package com.xlgzs.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class VoiceManager {
    private static final String VOICE_PATH_BASE_SD = Environment.getExternalStorageDirectory().getPath() + "/.xueluo/voice/";
    private static final String VOICE_PATH_BASE_ASSERT = "voice/";
    private Context mContext;
    private MediaPlayer mPlayer;
    private MediaPlayer mBgPlayer;
    private static final int PLAY_COMPLETE = 0;
    private List<String> musics = new ArrayList<String>();
    private boolean mPlaying = false;
    private static VoiceManager mInstance;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case PLAY_COMPLETE:
                musics.remove(0);
                if (musics.isEmpty()) {
                    stop(false);
                    return;
                }
                if (!mPlaying) {
                    return;
                }
                playMusic(musics.get(0));
                break;
            }
        }
    };

    interface PlayCallback {
        void onPlayStart();

        void onPlayStop();
    }

    private PlayCallback mPlayCallback;

    public static synchronized VoiceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VoiceManager(context);
        }
        return mInstance;
    }

    private VoiceManager(Context context) {
        mContext = context;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mBgPlayer = new MediaPlayer();
        mBgPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void play(WeatherInfo info, PlayCallback callback) {
        mPlayCallback = callback;
        if (mPlaying) {
            stop();
            return;
        }
        if (info == null) {
            return;
        }
        produceVoice(info);
        if (musics.isEmpty()) {
            return;
        }
        mPlaying = true;
        if (mPlayCallback != null) {
            mPlayCallback.onPlayStart();
        }
        palyBgMusic();
        playMusic(musics.get(0));
    }

    private void palyBgMusic() {
        try {
            AssetFileDescriptor afd = mContext.getResources().getAssets().openFd("bg/voice_bg.mp3");
            if (mBgPlayer == null) {
                mBgPlayer = new MediaPlayer();
                mBgPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mBgPlayer.reset();
            mBgPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mBgPlayer.setVolume(0.5f, 0.5f);
            mBgPlayer.prepare();
            mBgPlayer.setLooping(true);
            mBgPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic(String path) {
        boolean failed = false;
        try {
            AssetFileDescriptor afd = mContext.getResources().getAssets().openFd(VOICE_PATH_BASE_ASSERT + path);
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer arg0) {
                    mPlayer.reset();
                    mHandler.sendEmptyMessage(PLAY_COMPLETE);
                }
            });
        } catch (IllegalArgumentException e) {
            failed = true;
            e.printStackTrace();
        } catch (SecurityException e) {
            failed = true;
            e.printStackTrace();
        } catch (IllegalStateException e) {
            failed = true;
            e.printStackTrace();
        } catch (IOException e) {
            failed = true;
            e.printStackTrace();
        }
        if (failed) {
            mPlayer.reset();
            mHandler.sendEmptyMessage(PLAY_COMPLETE);
        }
    }

    public void stop() {
        stop(true);
    }

    public void stop(boolean stopBgMusicImediately) {
        if (mPlayCallback != null) {
            mPlayCallback.onPlayStop();
        }
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.reset();
        }
        if (mBgPlayer != null && mBgPlayer.isPlaying()) {
            if (stopBgMusicImediately) {
                mBgPlayer.stop();
                mBgPlayer.reset();
            } else {
                mBgPlayer.setLooping(false);
            }
        }
        mPlaying = false;
    }

    public void destroy() {
        if (mPlayCallback != null) {
            mPlayCallback.onPlayStop();
        }
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
            mPlayer = null;
        }
        if (mBgPlayer != null) {
            if (mBgPlayer.isPlaying()) {
                mBgPlayer.stop();
            }
            mBgPlayer.release();
            mBgPlayer = null;
        }
        mPlaying = false;
    }

    private void produceVoice(WeatherInfo info) {
        musics.clear();
        produceSayHello(info);
        produceDate(info);
        produceCity(info);
        produceWeather(info);
        produceTemperature(info);
        produceWind(info);
        produceSayGoodBye(info);
    }

    private void produceSayHello(WeatherInfo info) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (Utils.isNight()) {
            musics.add("other/bgmusic_begin_night.mp3");
        } else {
            musics.add("other/bgmusic_begin_day.mp3");
        }
        if (hour >= 5 && hour < 12) {
            musics.add("hello/good_morning.mp3");
        } else if (hour >= 12 && hour < 13) {
            musics.add("hello/good_noon.mp3");
        } else if (hour >= 13 && hour < 18) {
            musics.add("hello/good_afternoon.mp3");
        } else if ((hour >= 18 && hour < 20)) {
            musics.add("hello/good_evening.mp3");
        } else if ((hour >= 20 && hour < 24) || (hour >= 0 && hour < 5)) {
            musics.add("hello/good_night.mp3");
        }
    }

    private void produceDate(WeatherInfo info) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        musics.add(NOWIS);
        // hour
        int hour = c.get(Calendar.HOUR_OF_DAY);
        produceNumber(hour);
        musics.add(HOUR);
        // minute
        int minute = c.get(Calendar.MINUTE);
        produceNumber(minute);
        musics.add(MINUTE);
    }

    private void produceNumber(int number) {
        if (number > 10) {
            int tens = number / 10;
            int ones = number % 10;
            musics.add("number/num_" + tens + ".mp3");
            musics.add("number/num_10.mp3");
            if (ones != 0) {
                musics.add("number/num_" + ones + ".mp3");
            }
        } else {
            musics.add("number/num_" + number + ".mp3");
        }
    }

    private void produceCity(WeatherInfo info) {
        String city = null;
        if (info.mBasicInfo.city.equals("北京")) {
            city = "beijing";
        } else if (info.mBasicInfo.city.equals("石家庄")) {
            city = "shijiazhuang";
        } else if (info.mBasicInfo.city.equals("拉萨")) {
            city = "lasa";
        } else if (info.mBasicInfo.city.equals("呼和浩特")) {
            city = "huhehaote";
        } else if (info.mBasicInfo.city.equals("温江")){
            city = "wenjiang";
        }else if (info.mBasicInfo.city.equals("成都")){
            city = "chengdu";
        }
        if (city != null){
            musics.add("city/" + city + ".mp3");
        }
    }

    private void produceWeather(WeatherInfo info) {
        int weather = Utils.isNight() ? info.mBasicInfo.icon2 : info.mBasicInfo.icon;
        weather += 1;
        musics.add("weather/weather_" + weather + ".mp3");
    }

    private void produceTemperature(WeatherInfo info) {
        musics.add(TEMP);
        int tempMin = info.mBasicInfo.temp2;
        if (tempMin < 0) {
            musics.add(SUBZERO);
        }
        produceNumber(tempMin);
        musics.add(TO);
        int tempMax = info.mBasicInfo.temp;
        if (tempMax < 0) {
            musics.add(SUBZERO);
        }
        produceNumber(tempMax);
        musics.add(DEGREE);
    }

    private void produceWind(WeatherInfo info) {
        String wind = Utils.isNight() ? info.mBasicInfo.wind : info.mBasicInfo.wind2;
        if (wind.equals(WIND_XI_ZH)) {
            musics.add(WIND_XI);
        } else if (wind.equals(WIND_DONG_ZH)) {
            musics.add(WIND_DONG);
        } else if (wind.equals(WIND_NAN_ZH)) {
            musics.add(WIND_NAN);
        } else if (wind.equals(WIND_BEI_ZH)) {
            musics.add(WIND_BEI);
        } else if (wind.equals(WIND_DONGBEI_ZH)) {
            musics.add(WIND_DONGBEI);
        } else if (wind.equals(WIND_DONGNAN_ZH)) {
            musics.add(WIND_DONGNAN);
        } else if (wind.equals(WIND_XINAN_ZH)) {
            musics.add(WIND_XINAN);
        } else if (wind.equals(WIND_XIBEI_ZH)) {
            musics.add(WIND_XIBEI);
        } else if (wind.equals(WIND_WEI_ZH)) {
            musics.add(WIND_WEI);
        }
        String windL = Utils.isNight() ? info.mBasicInfo.windL2 : info.mBasicInfo.windL;
        if (windL.equals(WIND_WEI_ZH)) {
            musics.add(WIND_WEI);
        } else {
            for (int i = 0; i < windL.length(); i++) {
                if (windL.charAt(i) == '-') {
                    musics.add(TO);
                } else if (windL.charAt(i) > '0' && windL.charAt(i) <= '9') {
                    musics.add("number/num_" + windL.charAt(i) + ".mp3");
                }
            }
            musics.add(LEVEL);
        }
    }

    private void produceSayGoodBye(WeatherInfo info) {
        // musics.add(SAYGOODBYE);
    }

    static final String NOWIS = "other/now_is.mp3";
    static final String HOUR = "other/hour.mp3";
    static final String MINUTE = "other/minute.mp3";
    static final String TEMPMIN = "other/lowest_temp.mp3";
    static final String SUBZERO = "other/subzero.mp3";
    static final String DEGREE = "other/degree.mp3";
    static final String TEMP = "other/temperature.mp3";
    static final String TO = "other/to.mp3";
    static final String LEVEL = "other/level.mp3";
    static final String SAYGOODBYE = "goodbye.mp3";
    static final String WIND_XI = "wind/xi.mp3";
    static final String WIND_DONG = "wind/dong.mp3";
    static final String WIND_NAN = "wind/nan.mp3";
    static final String WIND_BEI = "wind/bei.mp3";
    static final String WIND_XINAN = "wind/xinan.mp3";
    static final String WIND_XIBEI = "wind/xibei.mp3";
    static final String WIND_DONGNAN = "wind/dongnan.mp3";
    static final String WIND_DONGBEI = "wind/dongbei.mp3";
    static final String WIND_WEI = "wind/wei.mp3";
    static final String WIND_XI_ZH = "西风";
    static final String WIND_DONG_ZH = "东风";
    static final String WIND_NAN_ZH = "南风";
    static final String WIND_BEI_ZH = "北风";
    static final String WIND_XINAN_ZH = "西南风";
    static final String WIND_XIBEI_ZH = "西北风";
    static final String WIND_DONGNAN_ZH = "东南风";
    static final String WIND_DONGBEI_ZH = "东北风";
    static final String WIND_WEI_ZH = "微风";

}
