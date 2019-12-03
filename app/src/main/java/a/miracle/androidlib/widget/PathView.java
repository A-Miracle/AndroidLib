package a.miracle.androidlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import static a.miracle.lib_utils.DisplayUtil.dp2px;

public class PathView extends View {

    private Paint paint;
    private Path path;

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 防抖动
        paint.setDither(true);
        // 设置画笔未实心
        paint.setStyle(Paint.Style.STROKE);
        // 设置颜色
        paint.setColor(Color.parseColor("#FF2A99FA"));
        // 设置画笔宽度
        paint.setStrokeWidth(dp2px(2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = dp2px(155);

        // 大圆
        canvas.drawCircle(cx, cy, radius, paint);

        double v_60 = Math.toRadians(60);

        for (int i = 0; i < 6; i++) {
            canvas.drawLine(cx, cy,
                    cx + (float) (Math.cos(v_60 * i) * radius), cy - (float) (Math.sin(v_60 * i) * radius), paint);

            float length = (float) (Math.cos(v_60) * radius);

            canvas.drawLine(cx + (float) (Math.cos(v_60 * i) * radius), cy - (float) (Math.sin(v_60 * i) * radius),
                    cx + (float) (Math.cos(v_60 * (i + 1)) * length), cy - (float) (Math.sin(v_60 * (i + 1)) * length),
                    paint);

            path.reset();
            path.moveTo(cx, cy);
            path.quadTo(cx + (float) (Math.cos(v_60 * (i + 1)) * length), cy - (float) (Math.sin(v_60 * (i + 1)) * length),
                    cx + (float) (Math.cos(v_60 * i) * radius), cy - (float) (Math.sin(v_60 * i) * radius));
            canvas.drawPath(path, paint);
        }
    }
}
