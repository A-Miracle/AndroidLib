package a.miracle.lib_widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Created by A Miracle on 2019/11/6.
 * 聊天气泡图片 ImageView
 * 原创 : https://github.com/A-Miracle
 */
public class BubbleImageView extends ImageView {
    private static final int LEFT_TOP = 0;
    private static final int LEFT_BOTTOM = 1;
    private static final int RIGHT_TOP = 2;
    private static final int RIGHT_BOTTOM = 3;

    private static final int COLOR_DRAWABLE_DIMENSION = 1;// 颜色可拉维度
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private int mAngleRadius = dp2px(8); // 圆角半径
    private int mCursorWidth = dp2px(10); // 指针宽
    private int mCursorHeight = dp2px(10); // 指针高
    private int mCursorLocation = LEFT_BOTTOM; // 指针位置
    private float mBorderSize = dp2px(1.5f); // 边框大小
    private int mBorderColor = Color.WHITE; // 边框颜色
    private boolean mIsBorder = true; // 是否有边框

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint;
    private Matrix mShaderMatrix;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private RectF mRect;
    private Path mPath;

    private Paint mBorderPaint;

    public BubbleImageView(Context context) {
        super(context);
        initView(null);
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleImageView);
            mAngleRadius = (int) a.getDimension(R.styleable.BubbleImageView_bubble_angleRadius, mAngleRadius);
            mCursorWidth = (int) a.getDimension(R.styleable.BubbleImageView_bubble_cursorWidth, mCursorWidth);
            mCursorHeight = (int) a.getDimension(R.styleable.BubbleImageView_bubble_cursorHeight, mCursorHeight);
            mCursorLocation = a.getInt(R.styleable.BubbleImageView_bubble_cursorLocation, mCursorLocation);
            mBorderSize = a.getDimension(R.styleable.BubbleImageView_bubble_borderSize, mBorderSize);
            mBorderColor = a.getColor(R.styleable.BubbleImageView_bubble_borderColor, mBorderColor);
            mIsBorder = a.getBoolean(R.styleable.BubbleImageView_bubble_isBorder, mIsBorder);
            a.recycle();
        }

        mPath = new Path();

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setColor(mBorderColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        float w_scale_h;
        if (width == 0 || height == 0) {
            w_scale_h = 1;
        } else {
            w_scale_h = 1.0f * width / height;
        }

        int maxHeight = getMaxHeight();
        int maxWidth = getMaxWidth();
        if (width > maxWidth) {
            width = maxWidth;
            height = (int) (width / w_scale_h + 0.5);
        }
        if (height > maxHeight) {
            height = maxHeight;
            width = (int) (height * w_scale_h + 0.5);
        }

        int minimumHeight = getMinimumHeight();
        int minimumWidth = getMinimumWidth();
        if (width < minimumWidth) {
            width = minimumWidth;
            height = (int) (width / w_scale_h + 0.5);
        }
        if (height < minimumHeight) {
            height = minimumHeight;
            width = (int) (height * w_scale_h + 0.5);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mRect = new RectF(getPaddingLeft() + mBorderSize,
                    getPaddingTop() + mBorderSize,
                    w - getPaddingRight() - mBorderSize,
                    h - getPaddingBottom() - mBorderSize);
        }
        setup();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }

        mPath.reset();
        switch (mCursorLocation) {
            case LEFT_TOP:
                leftTopPath(mRect, mPath);
                break;
            case LEFT_BOTTOM:
                leftBottomPath(mRect, mPath);
                break;
            case RIGHT_TOP:
                rightTopPath(mRect, mPath);
                break;
            case RIGHT_BOTTOM:
                rightBottomPath(mRect, mPath);
                break;
        }
        canvas.drawPath(mPath, mBitmapPaint);
        if (mIsBorder) {
            canvas.drawPath(mPath, mBorderPaint);
        }
    }

    public void leftTopPath(RectF rect, Path path) {
        path.moveTo(rect.left, rect.top);
        path.lineTo(rect.right - mAngleRadius, rect.top);
        // 右上圆角
        path.arcTo(
                new RectF(
                        rect.right - mAngleRadius * 2, rect.top,
                        rect.right, rect.top + mAngleRadius * 2
                ),
                270f, 90f
        );
        path.lineTo(rect.right, rect.bottom - mAngleRadius);
        // 右下圆角
        path.arcTo(
                new RectF(
                        rect.right - mAngleRadius * 2, rect.bottom - mAngleRadius * 2,
                        rect.right, rect.bottom
                ),
                0f, 90F
        );
        path.lineTo(rect.left + mCursorWidth + mAngleRadius, rect.bottom);
        // 左下圆角
        path.arcTo(
                new RectF(
                        rect.left + mCursorWidth, rect.bottom - mAngleRadius * 2,
                        rect.left + mCursorWidth + mAngleRadius * 2, rect.bottom
                ),
                90f, 90f
        );

        path.lineTo(rect.left + mCursorWidth, rect.top + mCursorHeight);
        // 左上指针
        path.arcTo(
                new RectF(
                        rect.left - mCursorWidth, rect.top,
                        rect.left + mCursorWidth, rect.top + mCursorHeight * 2
                ),
                0f, -90f
        );
        path.close();
    }

    public void leftBottomPath(RectF rect, Path path) {
        path.moveTo(rect.left + mCursorWidth + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius, rect.top);
        // 右上圆角
        path.arcTo(new RectF(
                        rect.right - mAngleRadius * 2, rect.top,
                        rect.right, rect.top + mAngleRadius * 2),
                270, 90);
        path.lineTo(rect.right, rect.bottom - mAngleRadius);
        // 右下圆角
        path.arcTo(new RectF(
                        rect.right - mAngleRadius * 2, rect.bottom - mAngleRadius * 2,
                        rect.right, rect.bottom),
                0, 90);
        path.lineTo(rect.left, rect.bottom);
        // 左下指针
        path.arcTo(new RectF(rect.left - mCursorWidth, rect.bottom - mCursorHeight * 2,
                        rect.left + mCursorWidth, rect.bottom),
                90f, -90f);
        path.lineTo(rect.left + mCursorWidth, rect.top + mAngleRadius);
        // 左上圆角
        path.arcTo(new RectF(rect.left + mCursorWidth, rect.top,
                        rect.left + mCursorWidth + mAngleRadius * 2, rect.top + mAngleRadius * 2),
                180, 90);
        path.close();
    }

    public void rightTopPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top);
        path.lineTo(rect.right, rect.top);
        // 右上指针
        path.arcTo(
                new RectF(
                        rect.right - mCursorWidth, rect.top,
                        rect.right + mCursorWidth, rect.top + mCursorHeight * 2
                ),
                270f, -90f
        );
        path.lineTo(rect.right - mCursorWidth, rect.bottom - mAngleRadius);
        // 右下圆角
        path.arcTo(
                new RectF(
                        rect.right - mCursorWidth - mAngleRadius * 2, rect.bottom - mAngleRadius * 2,
                        rect.right - mCursorWidth, rect.bottom
                ),
                0f, 90F
        );
        path.lineTo(rect.left + mAngleRadius, rect.bottom);
        // 左下圆角
        path.arcTo(
                new RectF(
                        rect.left, rect.bottom - mAngleRadius * 2,
                        rect.left + mAngleRadius * 2, rect.bottom
                ),
                90f, 90f
        );
        path.lineTo(rect.left, rect.top + mAngleRadius);
        // 左上圆角
        path.arcTo(
                new RectF(
                        rect.left, rect.top,
                        rect.left + mAngleRadius * 2, rect.top + mAngleRadius * 2
                ),
                180f, 90f
        );
        path.close();
    }

    public void rightBottomPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius - mCursorWidth, rect.top);
        // 右上圆角
        path.arcTo(new RectF(
                        rect.right - mAngleRadius * 2 - mCursorWidth, rect.top,
                        rect.right - mCursorWidth, rect.top + mAngleRadius * 2),
                270, 90);
        path.lineTo(rect.right - mCursorWidth, rect.bottom - mCursorHeight);
        // 右下指针
        path.arcTo(new RectF(
                        rect.right - mCursorWidth, rect.bottom - mCursorHeight * 2,
                        rect.right + mCursorWidth, rect.bottom),
                180, -90);
        path.lineTo(rect.left + mAngleRadius, rect.bottom);
        // 左下圆角
        path.arcTo(new RectF(rect.left, rect.bottom - mAngleRadius * 2,
                        rect.left + mAngleRadius * 2, rect.bottom),
                90f, 90f);
        path.lineTo(rect.left, rect.top + mAngleRadius);
        // 左上圆角
        path.arcTo(new RectF(rect.left, rect.top,
                        rect.left + mAngleRadius * 2, rect.top + mAngleRadius * 2),
                180, 90);
        path.close();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION,
                        COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (mBitmap == null) {
            return;
        }
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;
        if (mShaderMatrix == null) {
            mShaderMatrix = new Matrix();
        } else {
            mShaderMatrix.set(null);
        }
        Rect mDrawableRect = new Rect(0, 0, getRight() - getLeft(), getBottom()
                - getTop());
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
                * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
