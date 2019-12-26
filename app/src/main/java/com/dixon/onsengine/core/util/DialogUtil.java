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

    public static void showWarnDialog(Context context, String desc, View.OnClickListener onSureListener, View.OnClickListener onCancelListener) {
        if (!canShow(context)) {
            return;
        }
        CustomDialog dialog = new CustomDialog.Builder(context)
                .view(R.layout.dialog_warn)
                .style(R.style.dialog)
                .isCancelOnTouchOutSide(true)
                .windowAnimStyle(R.style.dialogAnim)
                .widthPx(ScreenUtil.dpToPxInt(context, 280))
//                .heightPx(ScreenUtils.dpToPxInt(context, 196))
                .addViewOnClick(R.id.dt_tv_ok, onSureListener)
                .addViewOnClick(R.id.dt_tv_cancel, onCancelListener)
                .build();
        ((TextView) dialog.getView().findViewById(R.id.dt_tv_desc)).setText(desc);
        show(dialog);
    }

    public static void showDeleteDialog(Context context, View.OnClickListener onSureListener) {
        if (!canShow(context)) {
            return;
        }
        CustomDialog dialog = new CustomDialog.Builder(context)
                .view(R.layout.dialog_delete)
                .style(R.style.dialog)
                .isCancelOnTouchOutSide(true)
                .windowAnimStyle(R.style.dialogAnim)
                .widthPx(ScreenUtil.dpToPxInt(context, 280))
//                .heightPx(ScreenUtils.dpToPxInt(context, 196))
                .addViewOnClick(R.id.dd_tv_delete, onSureListener)
                .build();
        show(dialog);
    }

    public static void showTipDialog(Context context, String desc, View.OnClickListener onSureListener) {
        if (!canShow(context)) {
            return;
        }
        CustomDialog dialog = new CustomDialog.Builder(context)
                .view(R.layout.dialog_tip)
                .style(R.style.dialog)
                .isCancelOnTouchOutSide(true)
                .windowAnimStyle(R.style.dialogAnim)
                .widthPx(ScreenUtil.dpToPxInt(context, 280))
//                .heightPx(ScreenUtils.dpToPxInt(context, 196))
                .addViewOnClick(R.id.dt_tv_ok, onSureListener)
                .build();
        ((TextView) dialog.getView().findViewById(R.id.dt_tv_desc)).setText(desc);
        show(dialog);
    }

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

    /**
     * 进度
     * <p>
     * 包括下载百分比、速度、下载量 多用于单个大型文件下载或上传
     *
     * @param context
     * @return
     */
    public static CustomDialog showProgressDialog(Context context) {
        if (!canShow(context)) {
            return null;
        }
        CustomDialog dialog = new CustomDialog.Builder(context)
                .view(R.layout.dialog_progress)
                .style(R.style.dialog)
                .isCancelOnTouchOutSide(false)
                .windowAnimStyle(R.style.dialogAnim)
                .widthPx(ScreenUtil.dpToPxInt(context, 280))
//                .heightPx(ScreenUtils.dpToPxInt(context, 196))
                .build();
        show(dialog);
        return dialog;
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
