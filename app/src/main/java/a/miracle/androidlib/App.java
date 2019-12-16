package a.miracle.androidlib;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;

/**
 * Created by c.tao on 2019/11/8
 */
public class App extends Application {

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //layout.setPrimaryColorsId(R.color.FFF3F3F3, R.color.FF333333); // 全局设置主题颜色
                //return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
                layout.setPrimaryColorsId(R.color.centerColor, android.R.color.white);
                return new BezierRadarHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                //return new ClassicsFooter(context).setDrawableSize(20).setAccentColorId(R.color.FF333333);
                return new BallPulseFooter(context);
            }
        });
    }

    private static App sApp;
    private static int mMainThreadId = -1;
    private static Handler mMainThreadHandler;
    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        mMainThreadId = android.os.Process.myTid();
        mMainThreadHandler = new Handler();
    }

    public static App get() {
        return sApp;
    }

    public static Handler getHandler() {
        return mMainThreadHandler;
    }

    public static int getThreadId() {
        return mMainThreadId;
    }

}
