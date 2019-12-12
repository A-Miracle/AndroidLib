package a.miracle.androidlib.base;


import java.util.ArrayList;
import java.util.Stack;

/**
 * activity 堆栈式管理
 * Created by A Miracle on 2016/9/19.
 */
public class AppManager {

    /** 堆栈列表 */
    private static Stack<BaseAct> mActivitys;// LinkedList

    private static class Single {
        static AppManager Instance = new AppManager();
    }
    public static AppManager getInstance() {
        return Single.Instance;
    }

    private AppManager() {
        mActivitys = new Stack<>();
    }

    /** 添加Activity到堆栈 */
    public void addActivity(BaseAct activity) {
        mActivitys.add(activity);
    }

    /** 添加Activity到堆栈 */
    public void removeActivity(BaseAct activity) {
        mActivitys.remove(activity);
    }

    /** 获取当前Activity（堆栈中最后一个压入的） */
    public BaseAct getCurrentActivity() {
        return mActivitys.lastElement();
    }

    /** 获取某一个Activity（堆栈中的） */
    public BaseAct getActivityByClass(Class<? extends BaseAct> exceptClass) {
        for (BaseAct activity : mActivitys) {
            if (activity.getClass().equals(exceptClass)) {
               return activity;
            }
        }
        return null;
    }

    /** 除了此Activity之外的所有Activity全部关闭 */
    public void finishExcept(BaseAct except) {
        ArrayList<BaseAct> copy;
        synchronized (mActivitys) {
            copy = new ArrayList<>(mActivitys);
        }
        for (BaseAct activity : copy) {
            if (activity != except) {
                activity.finish();
            }
        }
    }

    /** 除了此Activity之外的所有Activity全部关闭 */
    public void finishExcept(Class<? extends BaseAct> exceptClass) {
        ArrayList<BaseAct> arrayList;
        synchronized (mActivitys) {
            arrayList = new ArrayList<>(mActivitys);
        }
        for (BaseAct activity : arrayList) {
            if (!activity.getClass().equals(exceptClass)) {
                activity.finish();
            }
        }
    }

    /** 关闭所有Activity */
    public void finishAll() {
        ArrayList<BaseAct> copy;
        synchronized (mActivitys) {
            copy = new ArrayList<>(mActivitys);
        }
        for (BaseAct activity : copy) {
            activity.finish();
        }
    }

    /**  关闭从栈内某个Activity开始到某个Activity结束[start, end); */
    public void finishStartToEnd(Class<? extends BaseAct> startClass, Class<? extends BaseAct> endClass) {
        boolean isClose = false;
        ArrayList<BaseAct> arrayList;
        synchronized (mActivitys) {
            arrayList = new ArrayList<>(mActivitys);
        }
        for (BaseAct activity : arrayList) {
            if (activity.getClass() == startClass) {
                isClose = true;
            }
            if (activity.getClass() == endClass) {
                isClose = false;
            }
            if (isClose) {
                activity.finish();
            }
        }
    }

    /** 退出应用程序 */
    public void exitApp() {
        finishAll();
    }
}
