package a.miracle.androidlib.widget.exp.adapter;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import a.miracle.androidlib.widget.exp.listener.OnBindViewHolder;
import a.miracle.androidlib.widget.exp.listener.SectionStateChangeListener;
import a.miracle.androidlib.widget.exp.models.Item;
import a.miracle.androidlib.widget.exp.models.Section;


/**
 * Created by c.tao on 2019/11/21
 * SectionedExpandableLayoutHelper
 */
public class SEGHelper implements SectionStateChangeListener {

    //adapter
    private SEGAdapter mAdapter;

    //recycler view
    private RecyclerView mRecyclerView;

    public SEGHelper(RecyclerView recyclerView, GridLayoutManager gridLayoutManager, OnBindViewHolder holder) {
        mRecyclerView = recyclerView;
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new SEGAdapter(gridLayoutManager, holder, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void addSection(String sectionKey, Section section, ArrayList<Item> items) {
        mAdapter.addSection(sectionKey, section, items);
    }

    public void addItem(String sectionKey, Item item) {
        mAdapter.addItem(sectionKey, item);
    }

    public void removeItem(String sectionKey, Item item) {
        mAdapter.removeItem(sectionKey, item);
    }

    public void removeSection(String sectionKey) {
        mAdapter.removeSection(sectionKey);
    }

    @Override
    public void onSectionStateChanged(Section section, boolean isOpen) {
        if (!mRecyclerView.isComputingLayout()) {
            section.setExpanded(isOpen);
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        mAdapter.generateDataList();
        mAdapter.notifyDataSetChanged();
    }
}

