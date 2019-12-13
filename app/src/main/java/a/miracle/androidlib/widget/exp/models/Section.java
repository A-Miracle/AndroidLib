package a.miracle.androidlib.widget.exp.models;

import androidx.annotation.LayoutRes;

/**
 * Section
 */
public class Section extends Item {

    protected boolean isExpanded;

    public Section(@LayoutRes int layoutId, String name, int id) {
        super(layoutId, name, id);
        isExpanded = true;
    }

    public Section(@LayoutRes int layoutId) {
        super(layoutId);
        isExpanded = true;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
