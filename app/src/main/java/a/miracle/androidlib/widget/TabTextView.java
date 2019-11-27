package a.miracle.androidlib.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by c.tao on 2019/11/13
 */
public class TabTextView extends AppCompatTextView {
    public TabTextView(Context context) {
        super(context);
    }

    public TabTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}
