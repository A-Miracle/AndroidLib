package a.miracle.androidlib.widget.exp.listener;

import androidx.annotation.NonNull;

import java.util.List;

import a.miracle.androidlib.base.RViewHolder;
import a.miracle.androidlib.widget.exp.models.Item;


/**
 * 数据绑定
 */
public interface OnBindViewHolder {
    /**
     * 数据绑定
     * @param holder RViewHolder
     * @param item item or section
     * @param items items
     * @param listener SectionStateChangeListener
     */
    void onBindViewHolder(@NonNull RViewHolder holder, Item item, List<Item> items, final SectionStateChangeListener listener);
}
