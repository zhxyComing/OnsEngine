package com.dixon.onsengine.fun.photo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dixon.onsengine.R;
import com.dixon.onsengine.core.bean.IItemData;

import java.io.File;
import java.util.List;

public class PhotoListAdapter extends BaseAdapter implements IItemData<File> {

    private List<File> mItems;
    private Context mContext;

    public PhotoListAdapter(Context context,
                            List<File> items) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_display, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        File file = mItems.get(position);
        Glide.with(mContext)
                .load(Uri.fromFile(new File(file.getPath())))
                .into(vh.ivImgView);
        return convertView;
    }

    @Override
    public List<File> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        private ImageView ivImgView;

        ViewHolder(View item) {
            ivImgView = item.findViewById(R.id.ipd_iv_photo);
        }
    }
}
