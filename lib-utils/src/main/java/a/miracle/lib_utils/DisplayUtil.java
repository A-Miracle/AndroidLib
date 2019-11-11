package a.miracle.lib_utils;

import android.content.res.Resources;

/**
 * Created by A Miracle on 2016/3/15.
 */
public class DisplayUtil {

    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return Math.round(dpValue * DENSITY);
    }

    /**
     * 根据手机的分辨率从 px(像素)的单位 转成为dp
     */
    public static int px2dp(float pxValue) {
        return Math.round(pxValue / DENSITY);
    }
}
