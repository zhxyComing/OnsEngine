package com.dixon.onsengine.fun.path;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.core.bean.IItemData;
import com.dixon.onsengine.core.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class DirListAdapter extends BaseAdapter implements IItemData<File> {

    private List<File> mItems;
    private Context mContext;

    public DirListAdapter(Context context,
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dir_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        File file = mItems.get(position);
        vh.tvNameView.setText(file.getName());
        //设置大小或几个子项
        vh.tvCountView.setText(String.format(Locale.CHINA, "包含 %d 个子文件夹", FileUtil.getDirCount(file)));
        return convertView;
    }

    @Override
    public List<File> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        private ImageView ivImgView;
        private TextView tvNameView, tvCountView;

        ViewHolder(View item) {
            ivImgView = item.findViewById(R.id.idl_iv_image);
            tvNameView = item.findViewById(R.id.idl_tv_name);
            tvCountView = item.findViewById(R.id.idl_tv_count);
        }
    }
}
