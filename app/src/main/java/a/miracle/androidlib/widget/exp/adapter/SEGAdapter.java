package a.miracle.androidlib.widget.exp.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import a.miracle.androidlib.base.RViewHolder;
import a.miracle.androidlib.widget.exp.listener.OnBindViewHolder;
import a.miracle.androidlib.widget.exp.listener.SectionStateChangeListener;
import a.miracle.androidlib.widget.exp.models.Item;
import a.miracle.androidlib.widget.exp.models.Section;


/**
 * Created by c.tao on 2019/11/21
 * SectionedExpandableGridAdapter
 */
class SEGAdapter extends RecyclerView.Adapter<RViewHolder> {

    // data list
    private final ArrayList<Item> mList = new ArrayList<>();
    private final LinkedHashMap<Section, ArrayList<Item>> mSectionDataMap = new LinkedHashMap<>();

    // section map
    private final HashMap<String, Section> mSectionMap = new HashMap<>();

    // listeners
    private final OnBindViewHolder mBindViewHolder;
    private final SectionStateChangeListener mListener;

    SEGAdapter(
            final GridLayoutManager gridLayoutManager,
            final OnBindViewHolder mHolder,
            final SectionStateChangeListener listener) {

        mBindViewHolder = mHolder;
        mListener = listener;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isSection(position)?gridLayoutManager.getSpanCount():1;
            }
        });
    }

    private boolean isSection(int position) {
        return mList.get(position) instanceof Section;
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RViewHolder(viewType, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder holder, int position) {
        Item item = mList.get(position);
        ArrayList<Item> items = null;
        if(item instanceof Section){
            Section section = (Section) item;
            items = mSectionDataMap.get(section);
        }
        mBindViewHolder.onBindViewHolder(holder, item, items, mListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getLayoutId();
    }

    public void addSection(String sectionKey, Section section, ArrayList<Item> items) {
        mSectionMap.put(sectionKey, section);
        mSectionDataMap.put(section, items);
    }

    public void addItem(String sectionKey, Item item) {
        Objects.requireNonNull(mSectionDataMap.get(mSectionMap.get(sectionKey))).add(item);
    }

    public void removeItem(String sectionKey, Item item) {
        Objects.requireNonNull(mSectionDataMap.get(mSectionMap.get(sectionKey))).remove(item);
    }

    public void removeSection(String sectionKey) {
        mSectionDataMap.remove(mSectionMap.get(sectionKey));
        mSectionMap.remove(sectionKey);
    }

    public void generateDataList () {
        mList.clear();
        for (Map.Entry<Section, ArrayList<Item>> entry : mSectionDataMap.entrySet()) {
            Section key;
            mList.add((key = entry.getKey()));
            if (key.isExpanded()){
                mList.addAll(entry.getValue());
            }
        }
    }
}