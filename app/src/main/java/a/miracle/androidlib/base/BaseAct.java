package a.miracle.androidlib.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import a.miracle.lib_utils.StatusBarUtil;

/**
 * Created by c.tao on 2019/11/5
 */
public abstract class BaseAct extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        AppManager.getInstance().removeActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);

        onBeforeSetContentLayout();

        // setContentView
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        } else {
            View view = createContentView();
            if (view != null) {
                setContentView(view);
            }
        }

        initStatusBar();

        onAfterSetContentLayout(savedInstanceState);

        initListener();
    }

    protected void initStatusBar(){
        //设置状态栏透明
        StatusBarUtil.immersive(this);
    }

    /**setContent之前*/
    protected void onBeforeSetContentLayout() { }

    /**获取 setContent Id*/
    protected abstract int getLayoutId();

    /**直接 setContent View*/
    protected View createContentView() {
        return null;
    }

    /**setContent之后*/
    protected void onAfterSetContentLayout(@Nullable Bundle savedInstanceState) { }

    protected void initListener() { }

}
