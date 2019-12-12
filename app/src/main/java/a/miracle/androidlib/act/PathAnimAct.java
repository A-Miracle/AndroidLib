package a.miracle.androidlib.act;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import a.miracle.androidlib.R;
import a.miracle.androidlib.base.BaseAct;
import a.miracle.androidlib.widget.AnimView;
import a.miracle.lib_widget.anim.AnimatorPath;
import a.miracle.lib_widget.anim.PathEvaluator;

import static a.miracle.lib_utils.DisplayUtil.dp2px;

/**
 * Created by c.tao on 2019/12/3
 * Kotlin ObjectAnimator.ofObject Object... values 参数有问题
 */
public class PathAnimAct extends BaseAct implements View.OnClickListener {
    private List<View> mViews;
    private List<AnimatorPath> mPaths;

    @Override
    protected int getLayoutId() {
        return R.layout.act_path_anim;
    }

    @Override
    protected void onAfterSetContentLayout(Bundle savedInstanceState) {
        FrameLayout fl_root = findViewById(R.id.fl_content);
        findViewById(R.id.pathView).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("PathAnim");

        mViews = new ArrayList<>();
        for (int i = 0; i < 6; i++) {

            AnimView actionButton = new AnimView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp2px(30),dp2px(30));
            params.gravity = Gravity.CENTER;
            actionButton.setLayoutParams(params);
            mViews.add(actionButton);

            fl_root.addView(actionButton);

        }

        mPaths = new ArrayList<>();
        int cx = 0;//WIDTH / 2;
        int cy = 0;//HEIGHT / 2;
        int radius = dp2px(155);
        double v_60 = Math.toRadians(60);
        float length = (float) (Math.cos(v_60) * radius);
        for (int i = 0; i < 6; i++) {

            AnimatorPath path = new AnimatorPath();
            path.moveTo(cx, cy);
            path.secondBesselCurveTo(cx + (float) (Math.cos(v_60 * (i + 1)) * length), cy - (float) (Math.sin(v_60 * (i + 1)) * length),
                    cx + (float) (Math.cos(v_60 * i) * radius), cy - (float) (Math.sin(v_60 * i) * radius));

            mPaths.add(path);
        }

    }

    int count;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pathView:
                boolean flag = ++count % 2 == 0;
                for (int i = 0; i < 6; i++) {
                    startAnimatorPath(mViews.get(i), mPaths.get(i), flag);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void startAnimatorPath(View view, AnimatorPath path, boolean reverse) {
        ObjectAnimator anim = ObjectAnimator.ofObject(view, "anim", new PathEvaluator(), path.getPoints().toArray());
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(1000);
        if(reverse){
            anim.reverse();
        }else {
            anim.start();
        }
    }
}
