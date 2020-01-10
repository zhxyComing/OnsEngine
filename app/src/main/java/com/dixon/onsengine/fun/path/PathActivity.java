package com.dixon.onsengine.fun.path;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.bean.event.HomeRefreshEvent;
import com.dixon.onsengine.bean.event.PathRefreshEvent;
import com.dixon.onsengine.core.Params;
import com.dixon.onsengine.core.util.DialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加路径的风险
 * <p>
 * 因为游戏搜索是以文件夹的子文件为子节点的。eg：dir/gameA/childDir/xx.nsa，如果dir为游戏搜索目录，则gameA会被识别为游戏，而非childDir。运行时实际交给模拟器的是childDir，而非gameA。
 * 不能直接将childDir作为游戏文件，因为childDir同级可能还有别的游戏内文件，只不过暂时不清楚如何识别而已。
 * 这样在设定路径时可能会产生问题。
 * eg：folder/dir/gameA/xx.nsa folder/dir/gameB/xx.nsa
 * 如果我设定dir为搜索目录，那么没有问题，会正常识别为gameA、gameB俩个游戏。
 * 但是我设定folder为搜索目录，则dir会被识别为游戏，运行时只会执行gameA！
 * <p>
 * 本质原因时游戏的识别方式（只识别下一层）和路径搜索的可自定义（可设定为多个游戏的上上层目录）存在冲突，所以添加路径要谨慎！
 */
public class PathActivity extends BaseActivity {

    private LinearLayout mPathLayout;
    private TextView mPathMustView;
    private ImageView mPathAddView;
    private List<View> cache = new ArrayList<>();

    private static final String WARN_TIP = "慎重添加游戏路径！请选择多个游戏文件所在目录的上一层目录（如游戏A放在OERunner文件夹下，则应当设定OERunner为游戏搜索路径），" +
            "添加错误的路径（如直接设置SD卡根目录）不仅会导致游戏识别错误，还可能导致已识别的游戏无法正常运行！";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        EventBus.getDefault().register(this);

        initView();
        loadData();
        showWarn();
    }

    private void showWarn() {
        DialogUtil.showWarnTipDialog(this, WARN_TIP, v -> {
        });
    }

    private void loadData() {
        clearListView();
        List<String> pathList = SharedConfig.Instance().getGameDirPath();
        if (pathList == null) {
            return;
        }
        for (String path : pathList) {
            addPathItem(path);
        }
    }

    private void clearListView() {
        for (View view : cache) {
            mPathLayout.removeView(view);
        }
        cache.clear();
    }

    private void addPathItem(String path) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_path, null);
        TextView pathView = item.findViewById(R.id.ip_et_path);
        pathView.setText(path);
        pathView.setOnLongClickListener(v -> {
            DialogUtil.showDeleteDialog(PathActivity.this, v1 -> {
                SharedConfig.Instance().deleteGameDirPath(path);
                loadData();
                EventBus.getDefault().post(new HomeRefreshEvent());
            });
            return true;
        });
        mPathLayout.addView(item);
        cache.add(item);
    }

    private void initView() {
        mPathMustView.setText(Params.getGameDirectory());
        mPathAddView.setOnClickListener(v -> startActivity(new Intent(PathActivity.this, PathSetActivity.class)));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mPathMustView = findViewById(R.id.ap_tv_path_must);
        mPathLayout = findViewById(R.id.ap_ll_path_layout);
        mPathAddView = findViewById(R.id.ap_iv_add_path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRefreshEvent(PathRefreshEvent event) {
        // 刷新页面
        loadData();
    }
}
