package a.miracle.androidlib.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by c.tao on 2019/10/21
 */
public abstract class RBaseAdapter<T> extends RecyclerView.Adapter<RViewHolder> {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    protected List<T> mList = new ArrayList<>();

    public List<T> getData() {
        return mList;
    }

    public void setData(List<T> data){
        if(data != null){
            mList = data;
        }
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    @Override
    public final RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return new RViewHolder(mHeaderViews.get(viewType), parent);
        } else if (mFootViews.get(viewType) != null) {
            return new RViewHolder(mFootViews.get(viewType), parent);
        } else if(isEmpty()){
            return new RViewHolder(mOtherViews.get(ITEM_TYPE_EMPTY), parent);
        }else if(isError()){
            return  new RViewHolder(mOtherViews.get(ITEM_TYPE_ERROR), parent);
        }
        return onCreateRViewHolder(parent, viewType);
    }

    public abstract RViewHolder onCreateRViewHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(final RViewHolder holder, final int position) {
        if (isHeaderViewPos(position)) {
            // 头部
            onBindHeaderRViewHolder(holder, position, holder.getItemViewType());
            return;
        } else if (isFooterViewPos(position)) {
            // 脚部
            onBindFooterRViewHolder(holder, position, holder.getItemViewType());
            return;
        }else if (isEmpty()) {
            // Empty
            onBindEmptyRViewHolder(holder, position, holder.getItemViewType());
            return;
        }else if (isError()) {
            // Error
            onBindErrorRViewHolder(holder, position, holder.getItemViewType());
            return;
        }

        onBindRViewHolder(holder, position, holder.getItemViewType());
        if (null != mOnItemClickListener) {
            holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(RBaseAdapter.this, holder, holder.itemView, position));
        }
        if (null != mOnItemLongClickListener) {
            holder.itemView.setOnLongClickListener(v -> {
                mOnItemLongClickListener.onItemLongClick(RBaseAdapter.this, holder, holder.itemView, position);
                return true;
            });
        }
    }

    public abstract void onBindRViewHolder(RViewHolder holder, int position, int viewType);

    public void onBindHeaderRViewHolder(RViewHolder holder, int position, int viewType) {
    }

    public void onBindFooterRViewHolder(RViewHolder holder, int position, int viewType) {
    }

    public void onBindEmptyRViewHolder(RViewHolder holder, int position, int viewType) {
    }

    public void onBindErrorRViewHolder(RViewHolder holder, int position, int viewType) {
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter<RViewHolder> parent, RViewHolder holder, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView.Adapter<RViewHolder> parent, RViewHolder holder, View view, int position);
    }

    /// And Header, And Footer

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<Integer> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<Integer> mFootViews = new SparseArrayCompat<>();

    @Override
    public int getItemCount() {
        if (isEmpty()) return 1;
        else if (isError()) return 1;
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        } else if (isEmpty()){
            return ITEM_TYPE_EMPTY;
        }else if (isError()){
            return ITEM_TYPE_ERROR;
        }
        return super.getItemViewType(position - getHeadersCount());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(null, recyclerView, (layoutManager, oldLookup, position) -> {
            int viewType = getItemViewType(position);
            if (mHeaderViews.get(viewType) != null) {
                return layoutManager.getSpanCount();
            } else if (mFootViews.get(viewType) != null) {
                return layoutManager.getSpanCount();
            }else if (isEmpty()){
                return layoutManager.getSpanCount();
            }else if (isError()){
                return layoutManager.getSpanCount();
            }
            if (oldLookup != null)
                return oldLookup.getSpanSize(position);
            return 1;
        });
    }

    @Override
    public void onViewAttachedToWindow(RViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position) || isEmpty() || isError()) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public void addHeaderView(@LayoutRes int headerId) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, headerId);
    }

    public void addHeaderView(@LayoutRes int headerId, int viewType) {
        mHeaderViews.put(viewType + BASE_ITEM_TYPE_HEADER, headerId);
    }

    public void addFootView(@LayoutRes int footId) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, footId);
    }

    public void addFootView(@LayoutRes int footId, int viewType) {
        mFootViews.put(viewType + BASE_ITEM_TYPE_HEADER, footId);
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }

    public int getRealItemCount() {
        return mList.size();
    }

    /// add Empty, add Error
    public static final int ITEM_TYPE_EMPTY = Integer.MAX_VALUE - 1;
    public static final int ITEM_TYPE_ERROR = Integer.MAX_VALUE - 2;
    private boolean isError;

    private SparseArrayCompat<Integer> mOtherViews = new SparseArrayCompat<>();

    public void setError(boolean isError){
        this.isError = isError;
    }

    private boolean isEmpty(){
        return (mOtherViews.get(ITEM_TYPE_EMPTY) != null) && getRealItemCount() == 0;
    }

    private boolean isError(){
        return (mOtherViews.get(ITEM_TYPE_ERROR) != null) && getRealItemCount() == 0 && isError;
    }

    public void setEmptyView(@LayoutRes int emptyId) {
        mOtherViews.put(ITEM_TYPE_EMPTY, emptyId);
    }

    public void setErrorView(@LayoutRes int errorId) {
        mOtherViews.put(ITEM_TYPE_ERROR, errorId);
    }
}
