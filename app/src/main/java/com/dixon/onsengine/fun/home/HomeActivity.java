package com.dixon.onsengine.fun.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.bean.event.HomeRefreshEvent;
import com.dixon.onsengine.core.util.AnimationUtils;
import com.dixon.onsengine.core.util.ScreenUtil;
import com.dixon.onsengine.core.view.ToastView;
import com.dixon.onsengine.fun.game.GameActivity;
import com.dixon.onsengine.R;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.bean.FileAndType;
import com.dixon.onsengine.fun.set.AppSetActivity;
import com.dixon.onsengine.fun.set.OnsSetActivity;
import com.dixon.onsengine.zip.IUnZipCallback;
import com.dixon.onsengine.core.Params;
import com.dixon.onsengine.core.monitor.AppStateRegister;
import com.dixon.onsengine.core.monitor.AppStateTracker;
import com.dixon.onsengine.core.util.DialogUtil;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.SizeFormat;
import com.dixon.onsengine.core.util.UnZipUtil;
import com.dixon.onsengine.core.view.CustomDialog;
import com.dixon.onsengine.fun.about.AboutActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements AppStateTracker.AppStateChangeListener {

    private static final int PAGE_APP = 0;
    private static final int PAGE_SET = 1;
    private static final int PAGE_CHANGING = -1;
    private int mCurrentPage = PAGE_APP;

    private ListView mFileListView;
    private FileListAdapter mFileListAdapter;

    // todo 页面临时方案 后期改造为组件化分发结构
    private LinearLayout mSetLayout;

    private TextView mAppTab, mSetTab;
    private LinearLayout mAboutTab, mAppSetTab, mOnsSetTab;

    private ToastView mToastView;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EventBus.getDefault().register(this);

        AppStateRegister.register(this, this);
        initView();
        loadData();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mFileListView = findViewById(R.id.ah_file_list);
        mSetLayout = findViewById(R.id.ah_set_layout);
        mAppTab = findViewById(R.id.ah_tv_app);
        mSetTab = findViewById(R.id.ah_tv_set);
        mOnsSetTab = findViewById(R.id.ah_ll_ons_set);
        mAboutTab = findViewById(R.id.ah_ll_about_tab);
        mAppSetTab = findViewById(R.id.ah_ll_app_set_tab);
        mToastView = findViewById(R.id.ah_tv_exit);
    }

    private void initView() {
        mFileListView.setOnItemClickListener((parent, view, position, id) -> startItem(mFileListAdapter.getItems().get(position)));
        mFileListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteFile(position);
            return true;
        });
        mAppTab.setOnClickListener(v -> openAppTab());
        mSetTab.setOnClickListener(v -> openSetTab());
        mAboutTab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class)));
        mAppSetTab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AppSetActivity.class)));
        mOnsSetTab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, OnsSetActivity.class)));
        initToastCard();
    }

    private void openSetTab() {
        if (mCurrentPage == PAGE_SET || mCurrentPage == PAGE_CHANGING) {
            return;
        }
        mCurrentPage = PAGE_CHANGING;
        //setTextSize
        mAppTab.setTextSize(16);
        mAppTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mAppTab.setTextColor(getResources().getColor(R.color.md_grey_600));
        mSetTab.setTextSize(24);
        mSetTab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mSetTab.setTextColor(getResources().getColor(R.color.md_grey_900));

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mFileListView, "alpha", 1f, 0f);
        fadeOut.setDuration(150);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mSetLayout, "alpha", 0f, 1f);
        fadeIn.setDuration(150);

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSetLayout.setVisibility(View.VISIBLE);
                mFileListView.setVisibility(View.GONE);
                fadeIn.start();
            }
        });

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentPage = PAGE_SET;
            }
        });

        fadeOut.start();
    }

    private void openAppTab() {
        if (mCurrentPage == PAGE_APP || mCurrentPage == PAGE_CHANGING) {
            return;
        }
        mCurrentPage = PAGE_CHANGING;
        //setTextSize
        mAppTab.setTextSize(24);
        mAppTab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mAppTab.setTextColor(getResources().getColor(R.color.md_grey_900));
        mSetTab.setTextSize(16);
        mSetTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mSetTab.setTextColor(getResources().getColor(R.color.md_grey_600));

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mSetLayout, "alpha", 1f, 0f);
        fadeOut.setDuration(150);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mFileListView, "alpha", 0f, 1f);
        fadeIn.setDuration(150);

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSetLayout.setVisibility(View.GONE);
                mFileListView.setVisibility(View.VISIBLE);
                fadeIn.start();
            }
        });

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentPage = PAGE_APP;
            }
        });

        fadeOut.start();
    }

    private void showDeleteFile(int index) {
        File file = mFileListAdapter.getItems().get(index).getFile();
        DialogUtil.showDeleteDialog(this, v -> {
            DialogUtil.showWarnDialog(HomeActivity.this, "这将会删除你的本地文件并且不可恢复！",
                    sure -> {
                        FileUtil.deleteFile(file);
                        loadData();
                    },
                    cancel -> {

                    });
        });
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
     * 未知文件提示
     */
    private void startUnknownTip() {
        DialogUtil.showTipDialog(this, "未知文件，无法启动", v -> {
        });
    }

    /**
     * 启动解压
     *
     * @param target
     */
    private void startUnzip(FileAndType target) {
        DialogUtil.showTipDialog(this, "压缩文件，点击 OK 尝试解压，如解压失败请使用专业解压软件再试", v -> {
            CustomDialog customDialog = DialogUtil.showProgressDialog(this);
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

    /**
     * 启动Game
     *
     * @param gamePath
     */
    private void startGame(final String gamePath) {
        DialogUtil.showTipDialog(this, "点击 OK 启动游戏，或点击空白区域忽略", v -> GameActivity.startGame(HomeActivity.this, gamePath));
    }

    private void loadData() {
        List<FileAndType> list = parseFileList(FileUtil.getFileList(Params.getGameDirectory()));
        if (mFileListAdapter == null) {
            mFileListAdapter = new FileListAdapter(this, list);
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

    @Override
    public void appTurnIntoForeground() {
        // App重新进入时刷新列表
        loadData();
    }

    @Override
    public void appTurnIntoBackGround() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppStateRegister.unRegister(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRefreshEvent(HomeRefreshEvent event) {
        // 刷新页面
        loadData();
    }

    @Override
    public void onBackPressed() {
        //与上次点击返回键时刻作差
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            mToastView.show("再按一次退出程序", 2000);
            mExitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    private void initToastCard() {
        mToastView.setVisibility(View.GONE);
        mToastView.setToastAnimEvent(new ToastView.ToastAnimEvent() {
            @Override
            public void show(long time) {
                mToastView.setVisibility(View.VISIBLE);
                AnimationUtils.tranX(mToastView,
                        ScreenUtil.dpToPx(HomeActivity.this, -200),
                        0, 300, new DecelerateInterpolator(), null).start();
            }

            @Override
            public void hide(long time) {
                AnimationUtils.tranX(mToastView, 0,
                        ScreenUtil.dpToPx(HomeActivity.this, -200), 300,
                        new DecelerateInterpolator(),
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mToastView.setVisibility(View.GONE);
                            }
                        }).start();
            }
        });
    }
}
