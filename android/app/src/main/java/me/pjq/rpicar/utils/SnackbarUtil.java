package me.pjq.rpicar.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 15/10/2017.
 */

public class SnackbarUtil {

    private static final String TAG = "SFToast";

    private static Toast mToast;
    private Snackbar mSnackbar;


    public static SnackbarUtil makeText(Context ctx, int strId, int duration) {
        return makeText(ctx, ctx.getString(strId), duration, null);
    }

    public static SnackbarUtil makeText(Context ctx, String msg, int duration) {
        return makeText(ctx, msg, duration, null);
    }


    @SuppressWarnings("ShowToast")
    // this method will only make the toast, it will be shown later
    public static SnackbarUtil makeText(Context ctx, String msg, int duration, View topView) {

        SnackbarUtil toast = new SnackbarUtil();
        try {
            // Use snackbar if possible
            if (ctx instanceof Activity) {
                toast.mSnackbar = makeSnackbar(msg,
                        (duration == Toast.LENGTH_LONG) ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT, topView == null ? ((Activity) ctx).getWindow().getDecorView().findViewById(android.R.id.content) : topView);
            } else {
                if (null == toast.mToast) {
                    toast.mToast = Toast.makeText(ctx, msg, duration);
                } else {
                    toast.mToast.cancel();
                    toast.mToast = null;
                    toast.mToast = Toast.makeText(ctx, msg, duration);
                }
            }
        } catch (Exception ex) {
            // catch error due to Android version incompatibility e.g. MOB-11770
        }

        return toast;
    }

    public static void showText(Context ctx, String msg) {
        SnackbarUtil toast = makeText(ctx, msg, Toast.LENGTH_LONG, null);
        toast.show();
    }

    private static Snackbar makeSnackbar(String msg, int duration, View topView) {

        Snackbar snackbar = Snackbar.make(topView, msg, duration);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            if (tv != null) {
                tv.setTextColor(Color.WHITE);
            }
        }
        return snackbar;
    }

    public void show() {
        if (mSnackbar != null) {
            mSnackbar.show();
        } else if (mToast != null) {
            mToast.show();
        }
    }

    public boolean isShown() {
        return mSnackbar != null && mSnackbar.isShown();
    }

    public void dismiss() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        } else if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}

