package com.dixon.onsengine.fun.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dixon.onsengine.core.bean.IItemData;

import java.io.File;
import java.util.List;

public class FileListAdapter extends BaseAdapter implements IItemData<File> {

    private List<File> mItems;
    private Context mContext;
    // 仅支持一个文件or文件夹的发送
    private File mSelectFile;

    public FileListAdapter(Context context, List<File> items) {
        this.mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        File file = mItems.get(position);
        return convertView;
    }

    @Override
    public List<File> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        ViewHolder(View item) {

        }
    }
}
