package com.dixon.onsengine.core.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.core.util.TimerUtils;

/**
 * Created by dixon.xu on 2018/1/12.
 * <p>
 * 自定义Toast
 */

public class ToastView extends CardView {

    private TextView content;

    private boolean isShow;

    public static long SHOW_TIME = 600;
    public static long HIDE_TIME = 600;

    public ToastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToastView(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.toast_card_view, null);
        addView(view);

        content = view.findViewById(R.id.toast);
    }

    public void setToastAnimEvent(ToastAnimEvent event) {
        this.event = event;
    }

    public ToastAnimEvent event;

    public interface ToastAnimEvent {

        void show(long time);

        void hide(long time);
    }

    //显示完 才能接受下一个 展示期间show动画无效 但是可以利用show改变文字
    public void show(String content, long displayTime) {

        setText(content);

        if (event != null && !isShow) {
            isShow = true;
            event.show(SHOW_TIME);
            TimerUtils.mainDelay(SHOW_TIME + displayTime, (Activity) getContext(), new TimerUtils.MainEvent() {
                @Override
                public void main() {
                    event.hide(HIDE_TIME);
                    TimerUtils.back(HIDE_TIME, () -> isShow = false);
                }
            });
        }
    }

    public void setText(String text) {
        content.setText(text);
    }

    public boolean isShow() {
        return isShow;
    }
}
