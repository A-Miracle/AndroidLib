package a.miracle.androidlib.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import a.miracle.lib_widget.anim.PathPoint;

/**
 * Created by c.tao on 2019/12/3
 */
public class AnimView extends FloatingActionButton {
    public AnimView(Context context) {
        super(context);
    }

    public AnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置View的属性通过ObjectAnimator.ofObject()的反射机制来调用
     * @param newLoc
     */
    public void setAnim(PathPoint newLoc) {
        setTranslationX(newLoc.mX);
        setTranslationY(newLoc.mY);
    }
}
