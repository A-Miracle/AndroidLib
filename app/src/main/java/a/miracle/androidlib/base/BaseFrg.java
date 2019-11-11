package a.miracle.androidlib.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import a.miracle.lib_utils.ViewUtils;

/**
 * Created by c.tao on 2019/11/11
 */
public abstract class BaseFrg extends Fragment {
    protected BaseAct activity;
    protected View rootView;

    @Override
    public void onAttach(Context context) {
        activity = (BaseAct) context;
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = createView();
            if(rootView == null){
                rootView = inflater.inflate(getLayoutId(), null, false);
            }
        }else{
            ViewUtils.removeSelfFromParent(rootView);
        }
        return rootView;
    }

    /**获取 Layout Id*/
    protected abstract int getLayoutId();

    /**获取视图返回View*/
    protected View createView(){ return null; }

    @Override
    public abstract void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);
}
