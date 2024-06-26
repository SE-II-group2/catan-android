package com.group2.catan_android.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeListener implements SensorEventListener {
    private static final int SHAKE_TRIGGER_THRESHOLD = 3;
    private static final int SHAKE_RESET_TIME_MS = 3000;
    private static final int SHAKE_DEBOUNCE_TIME_MS = 200;
    private static final float SHAKE_FORCE_THRESHOLD_FORCE = 10.0f;

    private SensorManager mSensorManager;
    Sensor mSensor;
    private long mLastTime;
    private OnShakeListener mOnShakeListener;
    private final Context mContext;
    private boolean mRunning;
    private int mShakeCount;
    private boolean mBlocked;
    private boolean mInitialized;

    public interface OnShakeListener {
        void onShake();
    }

    public ShakeListener(Context context) {
        mContext = context;
        mRunning = false;
        mBlocked = false;
        mInitialized = false;
    }

    public void init() throws UnsupportedOperationException {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager == null)
            throw new UnsupportedOperationException("No sensors supported");
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mSensor == null)
            throw new UnsupportedOperationException("Linear accelerometer not supported");
        mInitialized = true;
    }

    public void doOnShake(OnShakeListener listener) {
        mOnShakeListener = listener;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float aX = event.values[0];
        float aY = event.values[1];
        float aZ = event.values[2];
        float force = (float) Math.sqrt(aX * aX + aY * aY + aZ * aZ);

        long now = System.currentTimeMillis();

        if (force > SHAKE_FORCE_THRESHOLD_FORCE) {
            if (now - mLastTime < SHAKE_DEBOUNCE_TIME_MS) {
                return;
            }
            if (now - mLastTime > SHAKE_RESET_TIME_MS) {
                Log.d("Sensors", "Reset from " + mShakeCount);
                mShakeCount = 0;
                mBlocked = false;
            }

            if (++mShakeCount >= SHAKE_TRIGGER_THRESHOLD && !mBlocked) {
                Log.d("Sensors", "Shake detected");
                if (mOnShakeListener != null) {
                    mOnShakeListener.onShake();
                }
                mBlocked = true;
            }
            mLastTime = now;
        }

    }

    public void resume() throws UnsupportedOperationException {
        if (!mInitialized) {
            init();
        }
        if (!mRunning) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
            mRunning = true;
        }
    }


    public void pause() {
        mSensorManager.unregisterListener(this);
        mRunning = false;
    }
}