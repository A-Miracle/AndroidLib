package a.miracle.androidlib.adapter;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import a.miracle.androidlib.R;
import a.miracle.androidlib.act.TabAct;
import a.miracle.lib_widget.azlist.AZBaseAdapter;
import a.miracle.lib_widget.azlist.AZItemEntity;


public class ItemAdapter extends AZBaseAdapter<String, ItemAdapter.ItemHolder> {
	TabAct activity;

	public void setOnItemListener(OnItemListener mOnItemListener) {
		this.mOnItemListener = mOnItemListener;
	}

	OnItemListener mOnItemListener;

	public ItemAdapter(List<AZItemEntity<String>> dataList, TabAct activity) {
		super(dataList);
		this.activity = activity;
	}

	@Override
	public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, parent, false));
	}

	@Override
	public void onBindViewHolder(ItemHolder holder, final int position) {
		if (position == activity.getCurrentTab()){
			holder.mTextName.setTextColor(Color.parseColor("#FF333333"));
		}else {
			holder.mTextName.setTextColor(Color.parseColor("#FF999999"));
		}
		holder.mTextName.setText(mDataList.get(position).getValue());
		holder.mTextName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnItemListener.onItemClick(v,position);
			}
		});
	}

	static class ItemHolder extends RecyclerView.ViewHolder {

		TextView mTextName;

		ItemHolder(View itemView) {
			super(itemView);
			mTextName = itemView.findViewById(R.id.text_item_name);
		}
	}

	public interface OnItemListener {
		void onItemClick(View v, int position);
	}
}
