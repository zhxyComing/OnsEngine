package com.dixon.onsengine.fun.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.bean.FileAndType;
import com.dixon.onsengine.core.bean.IItemData;
import com.dixon.onsengine.core.enumbean.GameType;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.GameUtil;
import com.dixon.onsengine.core.util.SizeFormat;

import java.io.File;
import java.util.List;

public class FileListAdapter extends BaseAdapter implements IItemData<FileAndType> {

    private List<FileAndType> mItems;
    private Context mContext;

    public FileListAdapter(Context context, List<FileAndType> items) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        FileAndType fileAndType = mItems.get(position);
        File file = fileAndType.getFile();
        vh.nameView.setText(file.getName());
        vh.sizeView.setText(SizeFormat.format(file.isDirectory() ? FileUtil.getFolderSize(file) : file.length()));
        if (fileAndType.isDir()) {
            switch (fileAndType.getGameType()) {
                case GameType.ONS:
                    vh.packView.setText(mContext.getResources().getString(R.string.ons_game_tag));
                    vh.tagView.setImageResource(R.mipmap.ic_game);
                    break;
                case GameType.KRKR:
                    vh.packView.setText(mContext.getResources().getString(R.string.krkr_game_tag));
                    vh.tagView.setImageResource(R.mipmap.ic_game);
                    break;
                default:
                    vh.packView.setText("未知文件夹");
                    vh.tagView.setImageResource(R.mipmap.ic_unknow_dir);
                    break;
            }
        } else if (fileAndType.isZip()) {
            vh.packView.setText("可解压");
            vh.tagView.setImageResource(R.mipmap.ic_zip);
        } else {
            vh.packView.setText("未知文件");
            vh.tagView.setImageResource(R.mipmap.ic_unknow);
        }
        return convertView;
    }

    @Override
    public List<FileAndType> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        private TextView nameView, sizeView, packView;
        private ImageView tagView;

        ViewHolder(View item) {
            nameView = item.findViewById(R.id.ifl_tv_title);
            sizeView = item.findViewById(R.id.ifl_tv_size);
            packView = item.findViewById(R.id.ifl_tv_pack);
            tagView = item.findViewById(R.id.ifl_iv_tag);
        }
    }
}
