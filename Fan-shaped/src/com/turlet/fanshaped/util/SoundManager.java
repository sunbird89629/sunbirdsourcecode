package com.turlet.fanshaped.util;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 
 * @ClassName: SoundManager 
 * @Description: 声音管理类
 * @author <a href="mailto:yfldyxl@163.com">yfldyxl@163.com</a>
 * @date 2011-12-17 上午10:54:52 
 * @version V1.0
 */
public class SoundManager {
	private Context context;
	private SoundPool mSoundPool;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private AudioManager mAudioManager;

	public SoundManager(Context context){
		this.context = context;
	}
	
	public void initSounds() {
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new HashMap<Integer, Integer>();
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	public void addSound(int index, int SoundID) {
		mSoundPoolMap.put(index, mSoundPool.load(context, SoundID, 1));
	}

	public void playSound(int index) {
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
	}

	public void playLoopedSound(int index) {
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
	}
	
	public void removeAll(){
		mSoundPoolMap.clear();
	}
}
