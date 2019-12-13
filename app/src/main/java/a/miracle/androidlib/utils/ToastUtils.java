package a.miracle.androidlib.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;

import a.miracle.androidlib.App;
import a.miracle.androidlib.R;
import a.miracle.lib_utils.ViewUtils;

/**
 * Created b A Miracle on 2016/10/26.
 * ToastUtils
 */
public class ToastUtils {

    private static CharSequence oldMsg;
    private static long oneTime = 0;
    private static long twoTime = 0;

    private static Toast mToast;

    public static void show(int resid){
        show(resid, 0);
    }

    public static void show(final int resid, final int duration){
        show(App.get().getString(resid), duration);
    }

    public static void show(final CharSequence text){
        show(text, 0);
    }

    public static void show(final CharSequence text, final int duration){
        if(android.os.Process.myTid() != App.getThreadId()){
            App.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    show(App.get(), text, duration);
                }
            });
        }else{
            show(App.get(), text, duration);
        }
    }

    private static void show(Context context, CharSequence text, int duration) {
        if (mToast == null) {
            mToast = new Toast(App.get());
            mToast.setGravity(Gravity.CENTER, 0, 0);
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflate.inflate(R.layout.view_toast, null);
            TextView tv = ViewUtils.getView(view, R.id.tv_toast);
            tv.setText(text);

            mToast.setView(view);

            mToast.show();
            oneTime = System.currentTimeMillis();
            return;
        }

        twoTime = System.currentTimeMillis();

        if (text.equals(oldMsg)) {
            if (twoTime - oneTime > duration) {
                mToast.show();
            }
        } else {
            oldMsg = text;

            TextView tv = ViewUtils.getView(mToast.getView(), R.id.tv_toast);
            tv.setText(text);

            mToast.show();
        }

        oneTime = twoTime;
    }

    public static void cancel(){
        if(null != mToast){
            mToast.cancel();
        }
    }

    public static final class Builder {
        private Toast toast;
        private int background = -1;
        private int textColor = -1;
        private int gravity = -1;
        private int xOffset;
        private int yOffset;

        public Builder() { }

        public Builder background(@ColorInt int color) {
            this.background = color;
            return this;
        }
        public Builder textColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }
        public Builder gravity(int gravity, int xOffset, int yOffset) {
            this.gravity = gravity;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            return this;
        }

        public void show(String text, int duration) {
            toast = new Toast(App.get());

            LayoutInflater inflate = (LayoutInflater) App.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflate.inflate(R.layout.view_toast, null);
            TextView tv = ViewUtils.getView(view, R.id.tv_toast);
            CardView cv = ViewUtils.getView(view, R.id.cv_group);
            tv.setText(text);
            if (background != -1) {
                cv.setCardBackgroundColor(background);
            }
            if (textColor != -1) {
                tv.setTextColor(textColor);
            }
            if(gravity != -1){
                toast.setGravity(gravity, xOffset, yOffset);
            }
            toast.setView(view);
            toast.setDuration(duration);
            toast.show();
        }

        public void show(@StringRes int textId, int duration) {
            String text = App.get().getString(textId);
            show(text, duration);
        }

        public void cancel(){
            if(null != toast){
                toast.cancel();
            }
        }
    }
}
