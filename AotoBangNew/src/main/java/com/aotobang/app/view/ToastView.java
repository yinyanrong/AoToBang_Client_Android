package com.aotobang.app.view;//package com.aotobang.app.view;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.widget.Toast;
/**
 * Created by Admin on 2015/11/24.
 */
public class ToastView {
    public static Toast toast;
    private int time;
    private Timer timer;
    public ToastView(Context context, String text) {
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    }
    //居中
    public void setGravity(int gravity, int xOffset, int yOffset) {
        //toast.setGravity(Gravity.CENTER, 0, 0); //居中
        toast.setGravity(gravity, xOffset, yOffset);
    }

    //设置时间
    public void setDuration(int duration) {
        toast.setDuration(duration);
    }

    //自定义时间
    public void setLongTime(int duration) {
        //toast.setDuration(duration);
        time = duration;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (time - 1000 >= 0) {
                    show();
                    time = time - 1000;
                } else {
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }
    public void show() {
        toast.show();
    }

    public static void cancel() {
        if(toast != null) {
            toast.cancel();
        }
    }

}
