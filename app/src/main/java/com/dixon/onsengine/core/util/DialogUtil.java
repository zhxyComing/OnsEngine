package com.dixon.onsengine.core.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.core.view.CustomDialog;

public class DialogUtil {

    public static void showGuideDialog(Context context, String desc, View.OnClickListener onSureListener) {
        if (!canShow(context)) {
            return;
        }
        CustomDialog dialog = new CustomDialog.Builder(context)
                .view(R.layout.dialog_guide)
                .style(R.style.dialog)
                .isCancelOnTouchOutSide(false)
                .windowAnimStyle(R.style.dialogAnim)
                .widthPx(ScreenUtil.dpToPxInt(context, 280))
//                .heightPx(ScreenUtils.dpToPxInt(context, 196))
                .addViewOnClick(R.id.dt_tv_ok, onSureListener)
                .build();
        ((TextView) dialog.getView().findViewById(R.id.dt_tv_desc)).setText(desc);
        show(dialog);
    }

    private static void show(Dialog dialog) {
        if (dialog != null) {
            dialog.show();
        }
    }


    private static boolean canShow(Context context) {
        if (context instanceof Activity && Looper.myLooper() == Looper.getMainLooper()) {
            if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                return true;
            }
        }
        return false;
    }
}
