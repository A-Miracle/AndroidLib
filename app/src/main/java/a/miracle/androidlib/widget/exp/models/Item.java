package a.miracle.androidlib.widget.exp.models;

import androidx.annotation.LayoutRes;

/**
 * Item
 */
public class Item {

    protected final int layoutId; // viewType
    protected String name;
    protected int id;

    public Item(@LayoutRes int layoutId, String name, int id) {
        this.layoutId = layoutId;
        this.name = name;
        this.id = id;
    }

    public Item(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLayoutId() {
        return layoutId;
    }
}
