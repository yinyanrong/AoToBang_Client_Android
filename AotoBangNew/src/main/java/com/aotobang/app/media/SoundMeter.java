package com.aotobang.app.media;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.aotobang.app.ConstantValues;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.PathUtil;

public  class SoundMeter {
	static final private double EMA_FILTER = 0.6;
	private MediaRecorder mRecorder = null;
	private double mEMA = 0.0;
public SoundMeter(Context ctx){
	LogUtil.info(SoundMeter.class, AotoBangApp.getInstance().getCachePath(PathType.Audio)+"");
	File file=new File(AotoBangApp.getInstance().getCachePath(PathType.Audio));
	if (!file.exists()) {
		   file.mkdir();
		  }
}
	public void start(String name) {
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}
		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(AotoBangApp.getInstance().getCachePath(PathType.Audio)+name);
			try {
				mRecorder.prepare();
				mRecorder.start();
				mEMA = 0.0;
			} catch (IllegalStateException e) {
				LogUtil.info(SoundMeter.class, e.getMessage());
			} catch (IOException e) {
				LogUtil.info(SoundMeter.class, e.getMessage());
			}

		}
	}

	public void stop() {
		if (mRecorder != null) {
			mRecorder.setOnErrorListener(null);
			mRecorder.setPreviewDisplay(null);
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void pause() {
		if (mRecorder != null) {
			mRecorder.stop();
		}
	}

	public void start() {
		if (mRecorder != null) {
			mRecorder.start();
		}
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
}
