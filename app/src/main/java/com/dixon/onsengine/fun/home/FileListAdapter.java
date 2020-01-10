package com.dixon.onsengine.fun.home;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dixon.onsengine.R;
import com.dixon.onsengine.bean.FileAndType;
import com.dixon.onsengine.core.bean.IItemData;
import com.dixon.onsengine.core.enumbean.GameType;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.SizeFormat;
import com.dixon.onsengine.zip.IZipType;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        setName(file, vh.nameView);
        vh.sizeView.setText(SizeFormat.format(file.isDirectory() ? FileUtil.getFolderSize(file) : file.length()));
        setAppTag(vh.packView, fileAndType);
        String iconPath = fileAndType.getIcon();
        Log.e("FileListAdapter", "iconPath " + iconPath);
        if (TextUtils.isEmpty(iconPath)) {
            setAppSupportIcon(vh.iconView, fileAndType);
            vh.iconView.setBorderColor(mContext.getResources().getColor(R.color.transparent));
        } else {
            loadImageFromSd(iconPath, vh.iconView);
            vh.iconView.setBorderColor(mContext.getResources().getColor(R.color.md_grey_800));
        }
        return convertView;
    }

    private void setName(File file, TextView nameView) {
        // 排除重名的情况 后来发现必要性不大 毕竟存在用户设定文件夹父子集为路径导致的重复情况 这种情况path都一样 只能说你要这样玩 那就祝你玩的开心～^_^
        /*
        for (FileAndType fileAndType : mItems) {
            File temp = fileAndType.getFile();
            if (file != temp && file.getName().equals(temp.getName())) {
                nameView.setText(file.getPath());
                return;
            }
        }
        */
        nameView.setText(file.getName());
    }

    private void setAppTag(TextView tagView, FileAndType fileAndType) {
        if (fileAndType.isDir()) {
            switch (fileAndType.getGameType()) {
                case GameType.ONS:
                    tagView.setText(mContext.getResources().getString(R.string.ons_game_tag));
                    break;
                case GameType.KRKR:
                    tagView.setText(mContext.getResources().getString(R.string.krkr_game_tag));
                    break;
                default:
                    tagView.setText("未知文件夹");
                    break;
            }
        } else if (fileAndType.isZip()) {
            tagView.setText("压缩包-可解压");
        } else {
            tagView.setText("未知文件");
        }
    }

    private void setAppSupportIcon(ImageView view, FileAndType fileAndType) {
        if (fileAndType.isUnknow()) {
            view.setImageResource(R.mipmap.ic_unknown_icon);
        } else if (fileAndType.isDir()) {
            if (fileAndType.getGameType() == GameType.ONS) {
                view.setImageResource(R.mipmap.ic_ons_icon);
            } else {
                view.setImageResource(R.mipmap.ic_unknown_dir_icon);
            }
        } else if (fileAndType.isZip()) {
            if (fileAndType.getZipType() == IZipType.RAR) {
                view.setImageResource(R.mipmap.ic_rar_icon);
            } else if (fileAndType.getZipType() == IZipType.SevenZ) {
                view.setImageResource(R.mipmap.ic_7z_icon);
            } else if (fileAndType.getZipType() == IZipType.ZIP) {
                view.setImageResource(R.mipmap.ic_zip_icon);
            }
        }
    }

    private void loadImageFromSd(String path, ImageView view) {
        File file = new File(path);
        Uri imageUri = Uri.fromFile(file);
        Glide.with(mContext)
                .load(imageUri)
                .into(view);
    }

    @Override
    public List<FileAndType> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        private TextView nameView, sizeView, packView;
        private CircleImageView iconView;

        ViewHolder(View item) {
            nameView = item.findViewById(R.id.ifl_tv_title);
            sizeView = item.findViewById(R.id.ifl_tv_size);
            packView = item.findViewById(R.id.ifl_tv_pack);
            iconView = item.findViewById(R.id.ifl_iv_icon);
        }
    }
}
