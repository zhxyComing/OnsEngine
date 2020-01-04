package com.dixon.onsengine.fun.home.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.bean.FileAndType;
import com.dixon.onsengine.bean.event.HomeRefreshEvent;
import com.dixon.onsengine.core.Params;
import com.dixon.onsengine.core.module.AbsModule;
import com.dixon.onsengine.core.module.ModuleContext;
import com.dixon.onsengine.core.util.DialogUtil;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.SizeFormat;
import com.dixon.onsengine.core.util.UnZipUtil;
import com.dixon.onsengine.core.view.CustomDialog;
import com.dixon.onsengine.fun.game.GameActivity;
import com.dixon.onsengine.fun.home.FileListAdapter;
import com.dixon.onsengine.zip.IUnZipCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppStoreModule extends AbsModule {

    private Activity activity;
    private ViewGroup parentViewGroup;

    private ListView mFileListView;
    private FileListAdapter mFileListAdapter;

    @Override
    public void init(ModuleContext moduleContext) {
        activity = moduleContext.getContext();
        parentViewGroup = moduleContext.getViewGroups().get(0);
        initView();
    }

    private void initView() {
        LayoutInflater.from(activity).inflate(R.layout.module_home_app_store, parentViewGroup, true);
        findView();
        initClick();
        initData();
    }

    private void findView() {
        mFileListView = parentViewGroup.findViewById(R.id.ah_file_list);
    }

    private void initClick() {
        mFileListView.setOnItemClickListener((parent, view, position, id) -> startItem(mFileListAdapter.getItems().get(position)));
        mFileListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteFile(position);
            return true;
        });
    }

    private void initData() {
        loadData();
        EventBus.getDefault().register(this);
    }

    private void startItem(final FileAndType target) {
        switch (target.getType()) {
            case FileAndType.TYPE_GAME:
                startGame(target.getFile().getPath());
                break;
            case FileAndType.TYPE_ZIP:
                startUnzip(target);
                break;
            case FileAndType.TYPE_UNKNOW:
                startUnknownTip();
                break;
        }
    }

    /**
     * 启动Game
     *
     * @param gamePath
     */
    private void startGame(final String gamePath) {
        DialogUtil.showTipDialog(activity, "点击 OK 启动游戏，或点击空白区域忽略", v -> GameActivity.startGame(activity, gamePath));
    }

    /**
     * 启动解压
     *
     * @param target
     */
    private void startUnzip(FileAndType target) {
        DialogUtil.showTipDialog(activity, "压缩文件，点击 OK 尝试解压，如解压失败请使用专业解压软件再试", v -> {
            CustomDialog customDialog = DialogUtil.showProgressDialog(activity);
            if (customDialog == null) {
                return;
            }
            TextView nameView = customDialog.getView().findViewById(R.id.dp_tv_content);
            TextView sizeView = customDialog.getView().findViewById(R.id.dp_tv_size);
            UnZipUtil.unZip(target.getFile(), target.getZipType(), new IUnZipCallback() {
                @Override
                public void onStart() {
                    sizeView.setText("解压开始");
                }

                @Override
                public void onProgress(String name, long size) {
                    sizeView.setText(String.format("已解压：%s", SizeFormat.format(size)));
                    nameView.setText(name);
                }

                @Override
                public void onError(String message) {
                    customDialog.setCanceledOnTouchOutside(true);
                    sizeView.setText("解压失败");
                    nameView.setText(message);
                }

                @Override
                public void onSucceed() {
                    customDialog.setCanceledOnTouchOutside(true);
                    sizeView.setText("解压成功");
                    deleteAfterUnZip(target.getFile());
                    loadData();
                }

                @SuppressLint("DefaultLocale")
                @Override
                public void onProcess(int process) {
                    sizeView.setText(String.format("当前进度：%d", process));
                    nameView.setText("解压中...");
                }
            });
        });
    }

    // 解压完成删除压缩包
    private void deleteAfterUnZip(File file) {
        if (SharedConfig.Instance().isDeleteAfterUnZip()) {
            FileUtil.deleteFile(file);
        }
    }

    private void loadData() {
        List<FileAndType> list = parseFileList(FileUtil.getFileList(Params.getGameDirectory()));
        if (mFileListAdapter == null) {
            mFileListAdapter = new FileListAdapter(activity, list);
            mFileListView.setAdapter(mFileListAdapter);
        } else {
            mFileListAdapter.getItems().clear();
            mFileListAdapter.getItems().addAll(list);
            mFileListAdapter.notifyDataSetChanged();
        }
    }

    private List<FileAndType> parseFileList(List<File> fileList) {
        boolean isHideUnknown = SharedConfig.Instance().isHideUnknown();
        List<FileAndType> list = new ArrayList<>();
        if (!isHideUnknown) {
            for (File file : fileList) {
                list.add(new FileAndType(file));
            }
        } else {
            // 过滤未知文件
            for (File file : fileList) {
                FileAndType fileAndType = new FileAndType(file);
                if (fileAndType.getType() == FileAndType.TYPE_UNKNOW) {
                    continue;
                }
                list.add(fileAndType);
            }
        }
        return list;
    }

    private void showDeleteFile(int index) {
        File file = mFileListAdapter.getItems().get(index).getFile();
        DialogUtil.showDeleteDialog(activity, v -> {
            DialogUtil.showWarnDialog(activity, "这将会删除你的本地文件并且不可恢复！",
                    sure -> {
                        FileUtil.deleteFile(file);
                        loadData();
                    },
                    cancel -> {

                    });
        });
    }

    /**
     * 未知文件提示
     */
    private void startUnknownTip() {
        DialogUtil.showTipDialog(activity, "未知文件，无法启动", v -> {
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRefreshEvent(HomeRefreshEvent event) {
        // 刷新页面
        loadData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onOrientationChanges(boolean isLandscape) {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void appTurnIntoForeground() {
        // App重新进入时刷新列表
        loadData();
    }

    @Override
    public void appTurnIntoBackGround() {

    }
}
