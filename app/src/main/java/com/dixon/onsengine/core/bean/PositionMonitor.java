package com.dixon.onsengine.core.bean;

/**
 * List记录back数据及位置
 *
 * @param <T>
 */
public class PositionMonitor<T> {

    private T data;

    private int position;

    public PositionMonitor(T data, int position) {
        this.data = data;
        this.position = position;
    }

    public PositionMonitor() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
