package com.dixon.onsengine.fun.photo;

import android.os.Bundle;
import android.widget.GridView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.core.util.DialogUtil;
import com.dixon.onsengine.core.util.FileUtil;

import java.io.File;
import java.util.List;

public class PhotoDisplayActivity extends BaseActivity {

    private GridView mPhotoGridView;
    private PhotoListAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        initView();
        loadData();
    }

    private void initView() {
        mPhotoGridView.setOnItemClickListener((parent, view, position, id) ->
                ImageViewerActivity.displayImage(PhotoDisplayActivity.this, mPhotoAdapter.getItems().get(position).getPath()));
        mPhotoGridView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(mPhotoAdapter.getItems().get(position));
            return true;
        });
    }

    /**
     * 长按删除弹窗
     *
     * @param file
     */
    private void showDeleteDialog(File file) {
        DialogUtil.showDeleteDialog(this, v -> {
            FileUtil.deleteFile(file);
            loadData();
        });
    }

    private void loadData() {
        List<File> fileList = FileUtil.getFileList(FileUtil.getSDPath() + "/OERunnerSetting/ScreenShot/");
        if (mPhotoAdapter == null) {
            mPhotoAdapter = new PhotoListAdapter(this, fileList);
            mPhotoGridView.setAdapter(mPhotoAdapter);
        } else {
            mPhotoAdapter.getItems().clear();
            mPhotoAdapter.getItems().addAll(fileList);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mPhotoGridView = findViewById(R.id.apd_gv_photo);
    }
}
