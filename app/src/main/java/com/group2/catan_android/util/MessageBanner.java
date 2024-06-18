package com.group2.catan_android.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.group2.catan_android.R;


public class MessageBanner {
    public static final int LENGTH_SHORT = 3000;
    public static final int LENGTH_LONG = 5000;
    private final FragmentActivity activity;
    private final MessageType type;
    private final String message;
    private int durationMillis;
    private int gravity;
    private View mView;
    private ViewGroup mViewGroup;
    private boolean isShowing;

    private MessageBanner(FragmentActivity activity, MessageType type, String message) {
        this.activity = activity;
        this.type = type;
        this.message = message;
        this.durationMillis = LENGTH_SHORT;
        this.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
    }

    public static MessageBanner makeBanner(FragmentActivity activity, MessageType type, String message) {
        return new MessageBanner(activity, type, message);
    }

    public MessageBanner setDuration(int durationMillis) {
        this.durationMillis = durationMillis;
        return this;
    }

    public MessageBanner setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public void show() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.notification, null);

        TextView messageView = mView.findViewById(R.id.notification_message);
        ImageButton closeView = mView.findViewById(R.id.notification_close_button);

        colorBanner();
        messageView.setText(message);

        mViewGroup = activity.findViewById(android.R.id.content);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = gravity;
        params.topMargin = 20;

        mViewGroup.addView(mView, params);

        Animation slideIn = AnimationUtils.loadAnimation(activity, R.anim.slide_in_top);
        mView.startAnimation(slideIn);

        new Handler().postDelayed(this::hide, durationMillis);
        closeView.setOnClickListener(v -> hide());
        isShowing = true;
    }

    private void hide() {
        if(isShowing) {
            isShowing = false;
            PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
            Animator animator = ObjectAnimator.ofPropertyValuesHolder(mView, propertyValuesHolder);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {
                    //nothing
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    mViewGroup.removeView(mView);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {
                    //nothing
                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {
                    //nothing
                }
            });
            animator.start();
        }
    }

    private void colorBanner() {
        switch (type) {
            case INFO:
                mView.setBackgroundColor(mView.getResources().getColor(R.color.GrassGreen, null));
                break;
            case ERROR:
                mView.setBackgroundColor(mView.getResources().getColor(R.color.red, null));
                break;
            case WARNING:
                mView.setBackgroundColor(mView.getResources().getColor(R.color.yellow, null));
        }
    }
}