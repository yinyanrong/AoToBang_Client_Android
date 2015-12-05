package com.aotobang.app.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * 手机摇晃
 * 
 * @author Administrator
 * 
 */
public abstract class ShakeListener implements SensorEventListener {
	// 上一个点数据记录
	private float lastX;
	private float lastY;
	private float lastZ;
	private long lasttime;

	private int duration = 100;// 两点之间时间间隔
	private float total;// 记录的汇总
	private float switchValue = 200;// 判断手机摇晃的阈值

	private Context context;
	

	public ShakeListener(Context context) {
		this.context = context;
		init();
		
	}

	private void init() {
		lastX = 0;
		lastY = 0;
		lastZ = 0;
		lasttime = 0;

		total = 0;

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (lasttime == 0) {
			// a、记录第一个点的加速度值（三个轴），记录采样时间
			lastX = event.values[SensorManager.DATA_X];
			lastY = event.values[SensorManager.DATA_Y];
			lastZ = event.values[SensorManager.DATA_Z];

			lasttime = System.currentTimeMillis();
		} else {
			long currenttime = System.currentTimeMillis();
			// 当采样的时间间隔满足要求（大于等于100ms）
			if ((currenttime - lasttime) >= duration) {
				// b、当采样的时间间隔满足要求（大于等于100ms），记录第二个数据包信息，记录采样时间，计算一二点之间的增量
				float x = event.values[SensorManager.DATA_X];
				float y = event.values[SensorManager.DATA_Y];
				float z = event.values[SensorManager.DATA_Z];

				float dx = Math.abs(x - lastX);
				float dy = Math.abs(y - lastY);
				float dz = Math.abs(z - lastZ);

				// dx:0.xxxx; 10,100,1000

				if (dx < 1) {
					dx = 0;
				}
				if (dy < 1) {
					dy = 0;
				}
				if (dy < 1) {
					dy = 0;
				}

				//float shake = dx + dy + dz;
				float shake = dx + dy;
				if (shake == 0) {
					init();
				}

				total += shake;

				// c、以此类推，汇总增量值信息，当汇总的值大于指定的阈值（200），用户摇晃手机
				if (total >= switchValue) {
					// 摇晃手机
					// 机选一注彩票
					randomCure();
				
					// 初始化所有的值
					init();
				} else {
					lastX = event.values[SensorManager.DATA_X];
					lastY = event.values[SensorManager.DATA_Y];
					lastZ = event.values[SensorManager.DATA_Z];

					lasttime = System.currentTimeMillis();
				}

			}
		}
	}

	public abstract void randomCure();

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
