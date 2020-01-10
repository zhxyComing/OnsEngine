package com.dixon.onsengine.fun.home.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.bean.FileAndType;
import com.dixon.onsengine.bean.event.HomeRefreshEvent;
import com.dixon.onsengine.core.Params;
import com.dixon.onsengine.core.enumbean.GameType;
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
import java.util.Collections;
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

    private void startItem(FileAndType target) {
        switch (target.getType()) {
            case FileAndType.TYPE_DIR:
                startDir(target);
                break;
            case FileAndType.TYPE_ZIP:
                startUnzip(target);
                break;
            case FileAndType.TYPE_UNKNOWN:
                startUnknownTip();
                break;
        }
    }

    private void startDir(FileAndType target) {
        switch (target.getGameType()) {
            case GameType.ONS:
                startOnsGame(target.getFile().getPath());
                break;
            case GameType.KRKR:
                startKrKrGame(target.getFile().getPath());
                break;
            default:
                startUnknownTip();
                break;
        }
    }

    private void startKrKrGame(String path) {

    }

    /**
     * 启动Game
     *
     * @param gamePath
     */
    private void startOnsGame(final String gamePath) {
        DialogUtil.showTipDialog(activity, "点击 OK 启动游戏，或点击空白区域忽略", v -> GameActivity.startGame(activity, gamePath));
    }

    /**
     * 启动解压
     *
     * @param target
     */
    private void startUnzip(FileAndType target) {

        CustomDialog dialog = DialogUtil.showUnZipDialog(activity, "压缩文件，点击 OK 尝试解压，如解压失败请使用专业解压软件再试。");
        View view = dialog.getView();
        if (view == null) {
            return;
        }
        EditText passwordView = view.findViewById(R.id.dt_et_password);
        TextView okView = view.findViewById(R.id.dt_tv_ok);
        okView.setOnClickListener(v -> {
            String password = passwordView.getText().toString();
            if (TextUtils.isEmpty(password)) {
                // 直接普通解压
                unZip(target);
            } else {
                // 加密文件解压缩
                unZip(target, password);
            }
            dialog.dismiss();
        });
    }

    private void unZip(FileAndType target, String password) {
        CustomDialog customDialog = DialogUtil.showProgressDialog(activity);
        if (customDialog == null) {
            return;
        }
        UnZipUtil.unZip(target.getFile(), target.getZipType(), password, new AppStoreUnZipCallback(customDialog, target.getFile()));
    }

    private void unZip(FileAndType target) {
        CustomDialog customDialog = DialogUtil.showProgressDialog(activity);
        if (customDialog == null) {
            return;
        }
        UnZipUtil.unZip(target.getFile(), target.getZipType(), new AppStoreUnZipCallback(customDialog, target.getFile()));
    }

    private class AppStoreUnZipCallback implements IUnZipCallback {

        private CustomDialog unZipDialog;
        private TextView sizeView;
        private TextView nameView;
        private File unZipFile;

        public AppStoreUnZipCallback(CustomDialog unZipDialog, File unZipFile) {
            this.unZipDialog = unZipDialog;
            this.unZipFile = unZipFile;
            this.nameView = unZipDialog.getView().findViewById(R.id.dp_tv_content);
            this.sizeView = unZipDialog.getView().findViewById(R.id.dp_tv_size);
        }

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
            unZipDialog.setCanceledOnTouchOutside(true);
            sizeView.setText("解压失败");
            nameView.setText(message);
            //刷新页面
            loadData();
        }

        @Override
        public void onSucceed() {
            unZipDialog.setCanceledOnTouchOutside(true);
            sizeView.setText("解压成功");
            deleteAfterUnZip(unZipFile);
            //刷新页面
            loadData();
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onProcess(int process) {
            sizeView.setText(String.format("当前进度：%d", process));
            nameView.setText("解压中...");
        }
    }

    // 解压完成删除压缩包
    private void deleteAfterUnZip(File file) {
        if (SharedConfig.Instance().isDeleteAfterUnZip()) {
            FileUtil.deleteFile(file);
        }
    }

    private void loadData() {
        List<File> fileList = FileUtil.getFileList(Params.getGameDirectory());
        List<String> otherPath;
        if ((otherPath = SharedConfig.Instance().getGameDirPath()) != null) {
            for (String path : otherPath) {
                fileList.addAll(FileUtil.getFileList(path));
            }
        }
        List<FileAndType> list = parseFileList(fileList);
        if (mFileListAdapter == null) {
            mFileListAdapter = new FileListAdapter(activity, list);
            mFileListView.setAdapter(mFileListAdapter);
        } else {
            mFileListAdapter.getItems().clear();
            mFileListAdapter.getItems().addAll(list);
            mFileListAdapter.notifyDataSetChanged();
        }
    }

    // todo 条件抽离
    private List<FileAndType> parseFileList(List<File> fileList) {
        boolean isHideUnknown = SharedConfig.Instance().isHideUnknown();
        boolean isHideUnknownDir = SharedConfig.Instance().isHideUnknownDir();
        List<FileAndType> list = new ArrayList<>();

        // 过滤未知文件
        for (File file : fileList) {
            FileAndType fileAndType = new FileAndType(file);
            if (isHideUnknown &&
                    fileAndType.getType() == FileAndType.TYPE_UNKNOWN) {
                continue;
            }
            if (isHideUnknownDir &&
                    fileAndType.getType() == FileAndType.TYPE_DIR &&
                    fileAndType.getGameType() == GameType.UNKNOW) {
                continue;
            }
            list.add(fileAndType);
        }

        // 排序
        if (SharedConfig.Instance().isAppSortAsType()) {
            Collections.sort(list);
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
