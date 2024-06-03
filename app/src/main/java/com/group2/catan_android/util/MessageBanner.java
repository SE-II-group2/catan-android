package com.group2.catan_android.util;

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
import androidx.fragment.app.FragmentActivity;

import com.group2.catan_android.R;


public class MessageBanner {
    public static final int LENGTH_SHORT = 3000;
    public static final int LENGTH_LONG = 5000;

    public enum MessageType {
        ERROR,
        INFO,
        WARNING
    }

    public static void showBanner(FragmentActivity activity, MessageType type, String message){
        showBanner(activity, type, message, LENGTH_SHORT);
    }
    public static void showBanner(FragmentActivity activity, MessageType type, String message, int durationMillis){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bannerView = inflater.inflate(R.layout.notification, null);

        TextView messageView = bannerView.findViewById(R.id.notification_message);
        ImageButton closeView = bannerView.findViewById(R.id.notification_close_button);

        colorBanner(bannerView, type);
        messageView.setText(message);

        ViewGroup rootLayout = activity.findViewById(android.R.id.content);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = 20;

        rootLayout.addView(bannerView, params);

        Animation slideIn = AnimationUtils.loadAnimation(activity, R.anim.slide_in_top);
        bannerView.startAnimation(slideIn);

        new Handler().postDelayed(() -> hideBanner(rootLayout, bannerView), durationMillis);
        closeView.setOnClickListener(v -> hideBanner(rootLayout, bannerView));
    }

    private static void hideBanner(ViewGroup rootLayout, View bannerView){
        Animation slideOut = AnimationUtils.loadAnimation(rootLayout.getContext(), R.anim.fade_away);
        bannerView.startAnimation(slideOut);
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootLayout.removeView(bannerView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //do nothing
            }
        });
    }

    private static void colorBanner(View bannerView, MessageType type){
        switch (type){
            case INFO:
                bannerView.setBackgroundColor(bannerView.getResources().getColor(R.color.GrassGreen, null));
                break;
            case ERROR:
                bannerView.setBackgroundColor(bannerView.getResources().getColor(R.color.red, null));
                break;
            case WARNING:
                bannerView.setBackgroundColor(bannerView.getResources().getColor(R.color.yellow, null));
        }
    }
}
