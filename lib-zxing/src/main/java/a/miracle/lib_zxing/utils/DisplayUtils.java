package a.miracle.lib_zxing.utils;

import android.content.res.Resources;

/**
 * Created by A Miracle on 2016/3/15.
 */
public class DisplayUtils {

    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final float SCALED_DENSITY = Resources.getSystem().getDisplayMetrics().scaledDensity;

    public static int dp2px(float dpValue) {
        return Math.round(dpValue * DENSITY);
    }

    public static int px2dp(float pxValue) {
        return Math.round(pxValue / DENSITY);
    }

    public static int sp2px(float spValue){
            return Math.round(spValue * SCALED_DENSITY);
    }

    public static int px2sp(float pxValue){
            return Math.round(pxValue / SCALED_DENSITY);
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
