package com.dixon.onsengine.fun.photo;

import android.os.Bundle;
import android.widget.GridView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.base.BaseActivity;
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
        initData();
    }

    private void initView() {
        mPhotoGridView.setOnItemClickListener((parent, view, position, id) ->
                ImageViewerActivity.displayImage(PhotoDisplayActivity.this, mPhotoAdapter.getItems().get(position).getPath()));
    }

    private void initData() {
        List<File> fileList = FileUtil.getFileList(FileUtil.getSDPath() + "/OERunnerSetting/ScreenShot/");
        mPhotoAdapter = new PhotoListAdapter(this, fileList);
        mPhotoGridView.setAdapter(mPhotoAdapter);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mPhotoGridView = findViewById(R.id.apd_gv_photo);
    }
}
