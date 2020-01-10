package com.dixon.onsengine.fun.path;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.bean.event.HomeRefreshEvent;
import com.dixon.onsengine.bean.event.PathRefreshEvent;
import com.dixon.onsengine.core.Params;
import com.dixon.onsengine.core.bean.PositionMonitor;
import com.dixon.onsengine.core.util.DialogUtil;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 路径设置 只显示文件夹 不显示文件 方便添加路径
 */
public class PathSetActivity extends BaseActivity {

    private ListView mDirListView;
    private LinearLayout mSdChangeView;

    private DirListAdapter mAdapter;
    private List<PositionMonitor<String>> mHistoryPath = new ArrayList<>();
    private String mCurrentPath;
    private int mCurrentPosition;
    private String mCurrentSd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_set);

        mCurrentPath = FileUtil.getStoragePath(this, false);
        mCurrentSd = mCurrentPath;
        // 只有Dir路径
        mAdapter = new DirListAdapter(this, FileUtil.getDirList(mCurrentPath));
        mDirListView.setAdapter(mAdapter);

        mDirListView.setOnItemClickListener((parent, view, position, id) -> {
            File file = mAdapter.getItems().get(position);
            if (FileUtil.getDirCount(file) == 0) {
                Toast.show(PathSetActivity.this, "在山与海的尽头～");
                return;
            }
            //将当钱路径记入历史方便回退
            recordHistoryPath(file);
            //跳页
            jumpPage(file);
        });

        mDirListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mCurrentPosition = firstVisibleItem;
            }
        });

        mDirListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String path = mAdapter.getItems().get(position).getPath();
            if (Params.getGameDirectory().equals(path)) {
                Toast.show(PathSetActivity.this, "该目录为默认搜索目录，无需重复添加！");
                return true;
            }
            DialogUtil.showTipDialog(PathSetActivity.this, "确定将【" + path + "】添加到游戏搜索目录中？（请尽量选择下层目录，以减少游戏定位的时间）", v -> {
                boolean b = SharedConfig.Instance().addGameDirPath(path);
                if (b) {
                    Toast.show(PathSetActivity.this, "新路径已添加！");
                    EventBus.getDefault().post(new PathRefreshEvent());
                    EventBus.getDefault().post(new HomeRefreshEvent());
                } else {
                    Toast.show(PathSetActivity.this, "添加失败，你可能添加过该路径！");
                }
            });
            return true;
        });

        mSdChangeView.setOnClickListener(v -> {
            String path = FileUtil.getStoragePath(PathSetActivity.this, true);
            if (TextUtils.isEmpty(path)) {
                Toast.show(PathSetActivity.this, "外置SD卡不存在！");
                return;
            }
            if (mCurrentSd != null && !mCurrentSd.equals(path)) {
                //切换到外置sd
                changeSD(path);
                mCurrentSd = path;
            } else {
                String insideSd = FileUtil.getStoragePath(PathSetActivity.this, false);
                changeSD(insideSd);
                mCurrentSd = insideSd;
            }
        });
    }

    private void changeSD(String path) {
        //清空历史
        mHistoryPath.clear();
        mCurrentPath = path;
        mCurrentPosition = 0;
        //进入下一级
        mAdapter.getItems().clear();
        mAdapter.getItems().addAll(FileUtil.getDirList(path));
        mAdapter.notifyDataSetChanged();
        mDirListView.setSelection(mCurrentPosition);
    }

    private void recordHistoryPath(File file) {
        if (file.isDirectory() && file.listFiles() != null) {
            mHistoryPath.add(new PositionMonitor<>(mCurrentPath, mCurrentPosition));
        }
    }

    private void jumpPage(File file) {
        if (file.isDirectory() && file.listFiles() != null) {
            //更新最新当前路径
            mCurrentPath = file.getPath();
            mCurrentPosition = 0;
            //进入下一级
            mAdapter.getItems().clear();
            mAdapter.getItems().addAll(FileUtil.getDirList(file.getPath()));
            mAdapter.notifyDataSetChanged();
            mDirListView.setSelection(mCurrentPosition);
        }
    }

    private void backPage(File file, int position) {
        if (file.isDirectory() && file.listFiles() != null) {
            //更新最新当前路径
            mCurrentPath = file.getPath();
            mCurrentPosition = position;
            //进入下一级
            mAdapter.getItems().clear();
            mAdapter.getItems().addAll(FileUtil.getDirList(file.getPath()));
            mAdapter.notifyDataSetChanged();
            mDirListView.setSelection(mCurrentPosition);
        }
    }

    @Override
    public void onBackPressed() {
        if (mHistoryPath.isEmpty()) {
            super.onBackPressed();
        } else {
            PositionMonitor<String> backData = mHistoryPath.remove(mHistoryPath.size() - 1);
            backPage(new File(backData.getData()), backData.getPosition());
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mDirListView = findViewById(R.id.aps_lv_file_list);
        mSdChangeView = findViewById(R.id.aps_ll_sd_change);
    }
}
