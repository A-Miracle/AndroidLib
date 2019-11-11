package a.miracle.lib_widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by A Miracle on 2019/11/6.
 * 密码框
 * 参考: https://github.com/EoniJJ/PasswordView
 */
public class CodeView extends View implements View.OnKeyListener, View.OnTouchListener, Handler.Callback {

    public CodeView(Context context) {
        super(context);
    }
    public CodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }
    public CodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }
    private static final int WITH_CURSOR = 0xA;
    private int cursorFlashTime = 500;         // 光标时间间隔
    private static String CIPHER_TEXT = "*";   // 密文符号
    private int passwordLength = 4;            // 密码个数
    private int passwordSize = dp2px(60);      // 单个密码框大小
    private int passwordPadding = dp2px(15);   // 间隔

    private Paint mPaintBorder;
    private int borderColor = Color.BLACK;     // 边框样式
    private int borderWidth = dp2px(1);        // 边框宽度

    private Paint mPaintCursor;
    private int cursorColor = Color.BLACK;       // 光标颜色
    private int cursorWidth = dp2px(2);          // 光标宽度
    private int cursorHeight = passwordSize / 2; // 光标高度

    private Paint mPaintText;
    private int textColor = Color.BLACK;         // 密码颜色
    private int textSize = dp2px(36);            // 密码大小

    private boolean isCursorShowing;             // 光标isShow
    private boolean isCursorEnable;              // 是否启用光标
    private boolean isInputComplete;             // 是否完成输入
    private boolean isCipherEnable;              // 是否密文
    private int cursorPosition;                  // 当前位置长度

    private String[] mPasswords;                 // 密码数据

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private PasswordListener mListener;
    private InputMethodManager mInputManager;

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CodeView);
            passwordLength = a.getInteger(R.styleable.CodeView_cvLength, passwordLength);
            passwordPadding = a.getInteger(R.styleable.CodeView_cvPadding, passwordPadding);
            passwordSize = a.getDimensionPixelSize(R.styleable.CodeView_cvSize, passwordSize);
            borderColor = a.getColor(R.styleable.CodeView_cvBorderColor, borderColor);
            borderWidth = a.getDimensionPixelSize(R.styleable.CodeView_cvBorderWidth, borderWidth);
            isCursorEnable = a.getBoolean(R.styleable.CodeView_cvIsCursorEnable, isCursorEnable);
            cursorColor = a.getColor(R.styleable.CodeView_cvCursorColor, cursorColor);
            isCipherEnable = a.getBoolean(R.styleable.CodeView_cvIsCipherEnable, isCipherEnable);
            textSize = a.getDimensionPixelSize(R.styleable.CodeView_cvTextSize, textSize);
            textColor = a.getColor(R.styleable.CodeView_cvTextColor, textColor);
            a.recycle();
        }

        initView();

        if(isCursorEnable){
            mHandlerThread = new HandlerThread("codeView");
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper(), this);
            mHandler.sendEmptyMessage(WITH_CURSOR);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == WITH_CURSOR){
            isCursorShowing = !isCursorShowing;
            postInvalidate();
            mHandler.sendEmptyMessageDelayed(WITH_CURSOR, cursorFlashTime);
        }
        return true;
    }

    private void initView() {
        mPasswords = new String[passwordLength]; // 密码数组
        mInputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        setOnKeyListener(this);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);

        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder.setColor(borderColor);
        mPaintBorder.setStrokeWidth(borderWidth);
        mPaintBorder.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaintCursor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCursor.setColor(cursorColor);
        mPaintCursor.setStrokeWidth(cursorWidth);
        mPaintCursor.setStyle(Paint.Style.FILL);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(textColor);
        mPaintText.setTextSize(textSize);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                //没有指定大小，宽度 = 单个密码框大小 * 密码位数 + 密码框间距 *（密码位数 - 1）
                width = passwordSize * passwordLength + passwordPadding * (passwordLength - 1);
                break;
            case MeasureSpec.EXACTLY:
                //指定大小，宽度 = 指定的大小
                width = MeasureSpec.getSize(widthMeasureSpec);
                //密码框大小等于 (宽度 - 密码框间距 *(密码位数 - 1)) / 密码位数
                passwordSize = (width - (passwordPadding * (passwordLength - 1))) / passwordLength;
                break;
        }
        setMeasuredDimension(width, passwordSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBorder(canvas);

        drawCursor(canvas);

        drawChatText(canvas);
    }

    private void drawBorder(Canvas canvas) {
        for (int i = 0; i < passwordLength; i++) {
            // 根据密码位数for循环绘制直线
            // 起始点x为paddingLeft + (单个密码框大小 + 密码框边距) * i , 起始点y为paddingTop + 单个密码框大小
            // 终止点x为 起始点x + 单个密码框大小 , 终止点y与起始点一样不变
            canvas.drawLine(getPaddingLeft() + (passwordSize + passwordPadding) * i,
                    getPaddingTop() + passwordSize,
                    getPaddingLeft() + (passwordSize + passwordPadding) * i + passwordSize,
                    getPaddingTop() + passwordSize, mPaintBorder);
        }
    }

    private void drawCursor(Canvas canvas) {
        //光标未显示 && 开启光标 && 输入位数未满 && 获得焦点
        if (!isCursorShowing && isCursorEnable && !isInputComplete && hasFocus()) {
            // 起始点x = paddingLeft + 单个密码框大小 / 2 + (单个密码框大小 + 密码框间距) * 光标下标
            // 起始点y = paddingTop + (单个密码框大小 - 光标大小) / 2
            // 终止点x = 起始点x
            // 终止点y = 起始点y + 光标高度
            canvas.drawLine((getPaddingLeft() + passwordSize / 2) + (passwordSize + passwordPadding) * cursorPosition,
                    getPaddingTop() + (passwordSize - cursorHeight) / 2,
                    (getPaddingLeft() + passwordSize / 2) + (passwordSize + passwordPadding) * cursorPosition,
                    getPaddingTop() + (passwordSize + cursorHeight) / 2,
                    mPaintCursor);
        }
    }

    private void drawChatText(Canvas canvas) {
        //文字居中的处理
        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        if (isCipherEnable) {
            mPaintText.getTextBounds(CIPHER_TEXT, 0, CIPHER_TEXT.length(), r);
        } else {
            mPaintText.getTextBounds("0", 0, CIPHER_TEXT.length(), r);
        }
        float y = cHeight / 2f + r.height() / 2f - r.bottom;

        //根据输入的密码位数，进行for循环绘制
        String text;
        for (int i = 0; i < mPasswords.length; i++) {
            String value = mPasswords[i];
            if (!TextUtils.isEmpty(value)) {
                // x = paddingLeft + 单个密码框大小/2 + ( 密码框大小 + 密码框间距 ) * i
                // y = paddingTop + 文字居中所需偏移量
                if (isCipherEnable) {
                    // 没有开启明文显示，绘制密码密文
                    text = CIPHER_TEXT;
                } else {
                    //明文显示，直接绘制密码
                    text = value;
                }
                canvas.drawText(text,
                        (getPaddingLeft() + passwordSize / 2) + (passwordSize + passwordPadding) * i,
                        getPaddingTop() + y, mPaintText);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 弹出软键盘
            requestFocus();
            mInputManager.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        }
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                /** 删除操作 */
                if (TextUtils.isEmpty(mPasswords[0])) {
                    return true;
                }
                String deleteText = delete();
                if (mListener != null && !TextUtils.isEmpty(deleteText)) {
                    mListener.passwordChange(deleteText);
                }
                postInvalidate();
                return true;
            }
            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                /** 只支持数字 */
                if (isInputComplete) {
                    return true;
                }
                String addText = add((keyCode - 7) + "");
                if (mListener != null && !TextUtils.isEmpty(addText)) {
                    mListener.passwordChange(addText);
                }
                if(isInputComplete && mListener != null){
                    mListener.passwordComplete(getText());
                }
                postInvalidate();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                System.out.println(">>>>>> KEYCODE_ENTER");
                mInputManager.hideSoftInputFromWindow(getWindowToken(), 0);
                /**
                 * 确认键
                 */
                if (mListener != null) {
                    mListener.keyEnterPress(getText(), isInputComplete);
                }
                return true;
            }
        }
        return false;
    }

    /**删除*/
    private String delete() {
        String deleteText = null;
        if (cursorPosition > 0) {
            deleteText = mPasswords[cursorPosition - 1];
            mPasswords[cursorPosition - 1] = null;
            cursorPosition--;
        } else if (cursorPosition == 0) {
            deleteText = mPasswords[cursorPosition];
            mPasswords[cursorPosition] = null;
        }
        isInputComplete = false;
        return deleteText;
    }

    /**增加*/
    private String add(String c) {
        String addText = null;
        if (cursorPosition < passwordLength) {
            addText = c;
            mPasswords[cursorPosition] = c;
            cursorPosition++;
            if (cursorPosition == passwordLength) {
                isInputComplete = true;
            }
        }
        return addText;
    }

    /**获取密码*/
    public String getText() {
        StringBuilder stringBuffer = new StringBuilder();
        for (String c : mPasswords) {
            if (TextUtils.isEmpty(c)) {
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER; // 输入类型为数字
        return super.onCreateInputConnection(outAttrs);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            mInputManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putStringArray("password", mPasswords);
        bundle.putInt("cursorPosition", cursorPosition);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mPasswords = bundle.getStringArray("password");
            cursorPosition = bundle.getInt("cursorPosition");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        onTouchEvent(event);
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestFocus();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(CodeView.this, InputMethodManager.SHOW_FORCED);
            }
        }, 300);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mInputManager.hideSoftInputFromWindow(getWindowToken(), 0);
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
    }

    /** 密码监听者 */
    public interface PasswordListener {
        /**
         * 输入 / 删除监听
         * @param changeText 输入/删除的字符
         */
        void passwordChange(String changeText);

        /** 输入完成 */
        void passwordComplete(String password);

        /**
         * 确认键后的回调
         * @param password 密码
         * @param isComplete 是否达到要求位数
         */
        void keyEnterPress(String password, boolean isComplete);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    public void setOnPasswordListener(PasswordListener listener){
        mListener = listener;
    }

    public boolean isInputComplete(){
        return isInputComplete;
    }
}
