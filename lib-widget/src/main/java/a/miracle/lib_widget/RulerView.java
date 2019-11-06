package a.miracle.lib_widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by A Miracle on 2019/11/6.
 * 刻度尺
 * 参考: https://github.com/panacena/RuleView
 */
public class RulerView extends View {

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int mMinVelocity;    // 手势动作的最小速度值
    private Scroller mScroller;  // Scroller是一个专门用于处理滚动效果的工具类   用mScroller记录/计算View滚动的位置，再重写View的computeScroll()，完成实际的滚动
    private VelocityTracker mVelocityTracker; // 主要用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率。
    private int mWidth, mHeight;

    private float mSelectorValue = 50f; // 未选择时 默认的值 滑动后表示当前中间指针正在指着的值
    private float mMaxValue = 100f;     // 最大数值
    private float mMinValue = 0f;       // 最小的数值
    private float mUnitValue = 1f;      // 最小单位  如 1:表示 每2条刻度差为1.   0.1:表示 每2条刻度差为0.1

    private float mLineSpaceWidth = 25;   //  尺子刻度2条线之间的距离
    private float mLineWidth = 2;         //  尺子刻度的宽度
    private float mLineMaxHeight = 100;   //  尺子刻度分为3中不同的高度。 mLineMaxHeight表示最长的那根(也就是 10的倍数时的高度)
    private float mLineMidHeight = 60;    //  mLineMidHeight  表示中间的高度(也就是 5  15 25 等时的高度)
    private float mLineMinHeight = 40;    //  mLineMinHeight  表示最短的那个高度(也就是 1 2 3 4 等时的高度)
    private boolean mAlphaEnable = true;  //  尺子 最左边 最后边是否需要透明 (透明效果更好点)


    private Paint mTextPaint;             // 尺子刻度下方数字( 也就是每隔10个出现的数值) paint
    private float mTextMarginTop = 10;    // top
    private float mTextSize = 30;         // 尺子刻度下方数字
    private float mTextHeight = 40;       // 尺子刻度下方数字  的高度
    private int mTextColor = Color.BLACK; // 文字的颜色

    private Paint mLinePaint;             // 尺子刻度  paint
    private int mLineColor = Color.GRAY;  // 刻度的颜色
    private int mTotalLine;               // 共有多少条 刻度
    private int mMaxOffset;               // 所有刻度 共有多长
    private float mOffset;                // 默认状态下，mSelectorValue所在的位置  位于尺子总刻度的位置

    private OnValueChangeListener mListener;  // 滑动后数值回调
    private int mLastX, mMoveX;


    private void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);

        // 边缘透明
        mAlphaEnable = typedArray.getBoolean(R.styleable.RulerView_alphaEnable, mAlphaEnable);

        // 刻度线条之间间隙
        mLineSpaceWidth = typedArray.getDimension(R.styleable.RulerView_lineSpaceWidth, mLineSpaceWidth);
        // 线条粗细
        mLineWidth = typedArray.getDimension(R.styleable.RulerView_lineWidth, mLineWidth);
        // 10号线
        mLineMaxHeight = typedArray.getDimension(R.styleable.RulerView_lineMaxHeight, mLineMaxHeight);
        // 5号线
        mLineMidHeight = typedArray.getDimension(R.styleable.RulerView_lineMidHeight, mLineMidHeight);
        // 1号线
        mLineMinHeight = typedArray.getDimension(R.styleable.RulerView_lineMinHeight, mLineMinHeight);
        // 线条颜色
        mLineColor = typedArray.getColor(R.styleable.RulerView_lineColor, mLineColor);

        // 标注字体大小
        mTextSize = typedArray.getDimension(R.styleable.RulerView_textSize,  mTextSize);
        // 标注颜色
        mTextColor = typedArray.getColor(R.styleable.RulerView_textColor, mTextColor);
        // 标注顶部top
        mTextMarginTop = typedArray.getDimension(R.styleable.RulerView_textMarginTop, mTextMarginTop);

        // 当前中刻度
        mSelectorValue = typedArray.getFloat(R.styleable.RulerView_selectorValue, mSelectorValue);
        // 刻度最小值
        mMinValue = typedArray.getFloat(R.styleable.RulerView_minValue, mMinValue);
        // 刻度最大值
        mMaxValue = typedArray.getFloat(R.styleable.RulerView_maxValue, mMaxValue);
        // 最小单位, 0.1, 1
        mUnitValue = typedArray.getFloat(R.styleable.RulerView_unitValue, mUnitValue);

        typedArray.recycle();

        // Scroller
        mScroller = new Scroller(context);

        // 获得允许执行一个fling手势动作的最小速度值
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        // 标注 Paint
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextHeight = getFontHeight(mTextPaint);

        // 线条 Paint
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setColor(mLineColor);

        setValue(mSelectorValue, mMinValue, mMaxValue, mUnitValue);
    }

    private float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     *
     * @param selectorValue 未选择时 默认的值 滑动后表示当前中间指针正在指着的值
     * @param minValue 最小的数值
     * @param maxValue 最大数值
     * @param unit 最小单位  如 1:表示 每2条刻度差为1.   0.1:表示 每2条刻度差为0.1
     */
    public void setValue(float selectorValue, float minValue, float maxValue, float unit) {
        mSelectorValue = selectorValue;
        mMaxValue = maxValue;
        mMinValue = minValue;
        mUnitValue = unit;
        mTotalLine = ((int) ((mMaxValue - mMinValue) / mUnitValue)) + 1;

        mMaxOffset = (int) ((mTotalLine - 1) * mLineSpaceWidth);
        mOffset = (mSelectorValue - mMinValue) / mUnitValue * mLineSpaceWidth;

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mWidth = w;
            mHeight = h;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left, height;
        String value;
        int alpha = 0;
        float scale;
        int srcPointX = mWidth / 2;

        for (int i = 0; i < mTotalLine; i++) {
            left = srcPointX + i * mLineSpaceWidth - mOffset; // 尺子往左移

            if (left < 0 || left > mWidth) {
                continue; // 先画默认值在正中间，左右各一半的view。多余部分暂时不画(也就是从默认值在中间，画旁边左右的刻度线)
            }

            if (i % 10 == 0) {
                height = mLineMaxHeight;
            } else if (i % 5 == 0) {
                height = mLineMidHeight;
            } else {
                height = mLineMinHeight;
            }

            if (mAlphaEnable) {
                scale = 1 - Math.abs(left - srcPointX) / srcPointX;
                alpha = (int) (255 * scale * scale);

                mLinePaint.setAlpha(alpha);
            }

            // 画刻度线
            canvas.drawLine(left, 0, left, height, mLinePaint);

            if (i % 10 == 0) {
                value = String.valueOf((int) (mMinValue + i * mUnitValue));
                if (mAlphaEnable) {
                    mTextPaint.setAlpha(alpha);
                }

                // 画标注
                canvas.drawText(value, left - mTextPaint.measureText(value) / 2,
                        height + mTextMarginTop + mTextHeight, mTextPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = xPosition;
                mMoveX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = mLastX - xPosition;
                mLastX = xPosition;
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastX = 0;
                mMoveX = 0;
                countMoveEnd();
                countVelocityTracker();
                return false;
        }
        return true;
    }

    private void countVelocityTracker() {
        mVelocityTracker.computeCurrentVelocity(1000);  // 初始化速率的单位
        float xVelocity = mVelocityTracker.getXVelocity(); // 当前的速度
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    /** 滑动后的操作 */
    private void changeMoveAndValue() {
        mOffset += mMoveX;

        if(mOffset <= 0){
            mOffset = 0;
            mMoveX = 0;
            mScroller.forceFinished(true);
        }else if(mOffset >= mMaxOffset){
            mOffset = mMaxOffset;
            mMoveX = 0;
            mScroller.forceFinished(true);
        }

        mSelectorValue = mMinValue + Math.round(mOffset / mLineSpaceWidth) * mUnitValue;

        notifyValueChange();
        postInvalidate();
    }

    /** 滑动结束后，若是指针在2条刻度之间时，改变 mOffset 让指针正好在刻度上。*/
    private void countMoveEnd() {
        mOffset += mMoveX;

        if (mOffset >= mMaxOffset) {
            mOffset = mMaxOffset;
        } else if (mOffset <= 0) {
            mOffset = 0;
        }

        mSelectorValue = mMinValue + Math.round(mOffset / mLineSpaceWidth) * mUnitValue;
        mOffset = (mSelectorValue - mMinValue) / mUnitValue * mLineSpaceWidth;

        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(mSelectorValue);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) { // mScroller.computeScrollOffset()返回 true表示滑动还没有结束
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMoveX = (mLastX - xPosition);
                mLastX = xPosition;
                changeMoveAndValue();
            }
        }
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /** 滑动后的回调 */
    public interface OnValueChangeListener {
        void onValueChange(float value);
    }
}
