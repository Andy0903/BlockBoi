package com.example.andy.blockboi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Calendar;

public class BlockerService extends Service {

    WindowManager windowManager;
    View blockerView;

    public BlockerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the chat head layout we created
        blockerView = LayoutInflater.from(this).inflate(R.layout.blocker, null, false);

         int width = 1080;
         final int height = 800;
         final int screenHeight = 1920;

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Set the close button.
        ImageView closeButton = (ImageView) blockerView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service and remove the chat head from the window
                stopSelf();
            }
        });

        //Specify the chat head position
        //Initially view will be added to top-left corner
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = screenHeight - height;

        //Add the view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(blockerView, params);

        //Drag and move chat head using user's touch action.
        final ImageView blockerImage = (ImageView) blockerView.findViewById(R.id.blocker);

        blockerImage.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;
            final int maxClickDuration = 1000;
            final int maxClickDistance = 15;
            long startClickTime;
            boolean stayedWithinClickDistance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        stayedWithinClickDistance = true;
                        return true;
                    case MotionEvent.ACTION_UP:
                        //long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        //if(clickDuration < maxClickDuration && stayedWithinClickDistance) {
                        //   stopSelf();
                        //}
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        if(stayedWithinClickDistance && distance(initialTouchX, initialTouchY, event.getRawX(), event.getRawY()) > maxClickDistance) {
                            stayedWithinClickDistance = false;
                        }

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(blockerView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (blockerView != null)
            windowManager.removeView(blockerView);
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return distanceInPx;
    }
}