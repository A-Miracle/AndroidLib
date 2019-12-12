package a.miracle.androidlib.base;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by c.tao on 2019/10/21
 */
public class RViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    public RViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    public RViewHolder(@LayoutRes int layoutId, ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        mViews = new SparseArray<>();
    }

    /** 通过ViewId获取控件 */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /** ImageView及子类专用 */
    public <T extends ImageView> RViewHolder setImageBitmap(@IdRes int viewId, Bitmap bm) {
        T imageView = getView(viewId);
        imageView.setImageBitmap(bm);
        return this;
    }

    /** ImageView及子类专用 */
    public <T extends ImageView> RViewHolder setImageDrawable(@IdRes int viewId, Drawable drawable) {
        T imageView = getView(viewId);
        imageView.setImageDrawable(drawable);
        return this;
    }

    /** ImageView及子类专用 */
    public <T extends ImageView> RViewHolder setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        T imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    /** TextView及子类专用 */
    public <T extends TextView> RViewHolder setText(@IdRes int viewId, CharSequence text) {
        T textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /** TextView及子类专用 */
    public <T extends TextView> RViewHolder setText(@IdRes int viewId, @StringRes int resId) {
        T textView = getView(viewId);
        textView.setText(resId);
        return this;
    }

    /** TextView及子类专用 */
    public <T extends TextView> RViewHolder setTextColor(@IdRes int viewId, @ColorInt int color) {
        T textView = getView(viewId);
        textView.setTextColor(color);
        return this;
    }

    /** TextView及子类专用 */
    public <T extends TextView> RViewHolder setTextColor(@IdRes int viewId, ColorStateList colors) {
        T textView = getView(viewId);
        textView.setTextColor(colors);
        return this;
    }

    /** CheckBox及子类专用 */
    public <T extends CheckBox> RViewHolder setChecked(@IdRes int viewId, boolean checked) {
        T checkBox = getView(viewId);
        checkBox.setChecked(checked);
        return this;
    }

    public RViewHolder setVisibility(@IdRes int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    public RViewHolder setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
        getView(viewId).setBackgroundColor(color);
        return this;
    }

    public RViewHolder setBackground(@IdRes int viewId, Drawable background) {
        getView(viewId).setBackground(background);
        return this;
    }
}
