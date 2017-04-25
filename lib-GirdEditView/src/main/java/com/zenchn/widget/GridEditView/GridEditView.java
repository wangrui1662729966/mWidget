package com.zenchn.widget.GridEditView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenchn.widget.factory.DrawableFactory;
import com.zenchn.widget.utils.DensityUtils;
import com.zenchn.widget.utils.ScreenUtils;


public class GridEditView extends RelativeLayout {

    private static final int DEFAULT_LENGTH = 4;
    private static final float DEFAULT_TEXT_SIZE_SP = 15.0f;
    private static final float DEFAULT_VIEW_WIDTH_DP = 46.0f;
    private static final float DEFAULT_VIEW_HEIGHT_DP = 46.0f;
    private static final float DEFAULT_LINE_WIDTH_DP = 1.2f;
    private static final float DEFAULT_RADIUS_DP = 6.0f;
    private static final float DEFAULT_LINE_MARGIN_DP = 8.0f;

    private EditText etInput;
    private TextView[] TextViews;
    private StringBuffer clipBoard;

    private int mTextColor;
    private float mTextSize;
    private int mMaxLength;

    private Drawable mBoxEmptyBackgroundDrawable;
    private Drawable mBoxFillBackgroundDrawable;

    private Drawable mBoxEmptyBottomLineDrawable;
    private Drawable mBoxFillBottomLineDrawable;
    private int mBottomLineMarginPx;

    private int inputType;
    private Style style = Style.BelowLine;


    public GridEditView(Context context) {
        this(context, null);
    }

    public GridEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.GridEditView);
        //获取自定义属性和默认值
        mTextColor = mTypedArray.getColor(R.styleable.GridEditView_text_color, Color.BLACK);//文字颜色
        mTextSize = mTypedArray.getDimension(R.styleable.GridEditView_text_size, DEFAULT_TEXT_SIZE_SP);//文字大小

        int styleValue = mTypedArray.getLayoutDimension(R.styleable.GridEditView_style, Style.BelowLine.ordinal());//输入框风格（0边框/1单下线）
        style = Style.classifyStyle(styleValue);

        switch (style) {
            case EdgeFrame:
                int mSolidColor = mTypedArray.getColor(R.styleable.GridEditView_solid_color, Color.TRANSPARENT);//填充颜色
                float mSolidWidth = mTypedArray.getDimension(R.styleable.GridEditView_solid_width, DEFAULT_VIEW_WIDTH_DP);//填充宽度
                float mSolidHeight = mTypedArray.getDimension(R.styleable.GridEditView_solid_height, DEFAULT_VIEW_HEIGHT_DP);//填充高度
                int mLineFillColor = mTypedArray.getColor(R.styleable.GridEditView_sideline_fill_color, getResources().getColor(R.color.colorAccent));//边框颜色
                int mLineEmptyColor = mTypedArray.getColor(R.styleable.GridEditView_sideline_empty_color, getResources().getColor(R.color.colorPrimary));//边框颜色
                float mLineWidth = mTypedArray.getDimension(R.styleable.GridEditView_bottom_line_width, DEFAULT_LINE_WIDTH_DP);//边线宽度
                float mRadius = mTypedArray.getDimension(R.styleable.GridEditView_sideline_radius, DEFAULT_RADIUS_DP);//边角
                mBoxFillBackgroundDrawable = DrawableFactory.createBackgroundDrawable(mSolidColor, DensityUtils.dp2px(mSolidWidth), DensityUtils.dp2px(mSolidHeight), DensityUtils.dp2px(mLineWidth), mLineFillColor, mRadius);
                mBoxEmptyBackgroundDrawable = DrawableFactory.createBackgroundDrawable(mSolidColor, DensityUtils.dp2px(mSolidWidth), DensityUtils.dp2px(mSolidHeight), DensityUtils.dp2px(mLineWidth), mLineEmptyColor, mRadius);
                break;

            case BelowLine:
                float mBottomLineWidth = mTypedArray.getDimension(R.styleable.GridEditView_bottom_line_width, DEFAULT_LINE_WIDTH_DP);//输入框下线宽度
                int mBottomLineFillColor = mTypedArray.getColor(R.styleable.GridEditView_bottom_line_fill_color, getResources().getColor(R.color.colorAccent));//边框颜色
                int mBottomLineEmptyColor = mTypedArray.getColor(R.styleable.GridEditView_bottom_line_empty_color, getResources().getColor(R.color.colorPrimary));//边框颜色
                float mBottomLineMargin = mTypedArray.getDimension(R.styleable.GridEditView_bottom_line_margin, DEFAULT_LINE_MARGIN_DP);//输入框下线距离文字间距
                mBottomLineMarginPx = DensityUtils.dp2px(mBottomLineMargin);
                mBoxFillBottomLineDrawable = DrawableFactory.createDrawableLine(mBottomLineFillColor, ScreenUtils.getScreenWidth(context), DensityUtils.dp2px(mBottomLineWidth));
                mBoxEmptyBottomLineDrawable = DrawableFactory.createDrawableLine(mBottomLineEmptyColor, ScreenUtils.getScreenWidth(context), DensityUtils.dp2px(mBottomLineWidth));
                break;

            case Background:
                mBoxEmptyBackgroundDrawable = mTypedArray.getDrawable(R.styleable.GridEditView_box_empty_background);//输入框内容为空时的背景
                mBoxFillBackgroundDrawable = mTypedArray.getDrawable(R.styleable.GridEditView_box_fill_background);//输入框内容非空时的背景
                break;
        }

        int type = mTypedArray.getInteger(R.styleable.GridEditView_type, 0);//显示类型（0明文/1暗文）
        inputType = getInputType(type);//设置输入框类型

        mMaxLength = mTypedArray.getInteger(R.styleable.GridEditView_max_length, DEFAULT_LENGTH);//验证码长度
        mTypedArray.recycle();

        clipBoard = new StringBuffer(mMaxLength);//初始化剪切板
    }

    private int getInputType(int type) {

        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;

        switch (type) {

            case 0:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;

            case 1:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;

        }
        return inputType;
    }

    private void initViews(Context context) {

        View.inflate(context, R.layout.widget_gird_editview, this);

        LinearLayout llContainer = (LinearLayout) findViewById(R.id.ll_container);
        llContainer.setOrientation(LinearLayout.HORIZONTAL);
        llContainer.setWeightSum(mMaxLength - 1);

        TextViews = new TextView[mMaxLength];

        for (int i = 0; i < mMaxLength; i++) {

            TextView tv = getTextView(context, style);
            TextViews[i] = tv;
            llContainer.addView(tv);

            if (i != mMaxLength - 1) {
                View v = getDivideView(context);
                llContainer.addView(v);
            }

        }

        etInput = (EditText) findViewById(R.id.et_input);
        etInput.setCursorVisible(false);//将光标隐藏
        etInput.requestFocus();
        setListener();
    }

    @NonNull
    private View getDivideView(Context context) {
        View divideView = new View(context);
        divideView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        return divideView;
    }

    @NonNull
    private TextView getTextView(Context context, Style style) {
        TextView mTextView = new TextView(context);
        mTextView.setTextSize(mTextSize);
        mTextView.setTextColor(mTextColor);
        mTextView.setEms(2);
        mTextView.setInputType(inputType);

        switch (style) {

            case Background:
            case EdgeFrame:
                mTextView.setBackgroundDrawable(mBoxEmptyBackgroundDrawable);
                break;

            case BelowLine:
                mBoxEmptyBottomLineDrawable.setBounds(0, 0, mBoxEmptyBottomLineDrawable.getMinimumWidth(), mBoxEmptyBottomLineDrawable.getMinimumHeight());
                mTextView.setCompoundDrawablePadding(mBottomLineMarginPx);
                mTextView.setCompoundDrawables(null, null, null, mBoxEmptyBottomLineDrawable);
                break;

        }
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return mTextView;
    }

    private void setListener() {

        etInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //重点   如果字符不为""时才进行操作
                if (!editable.toString().equals("")) {

                    if (clipBoard.length() < mMaxLength && editable.length() > 0) {//一点要判断是否还能容纳

                        int freeCapacity = mMaxLength - clipBoard.length();//计算剩余可以容纳长度
                        CharSequence freeCharSequence = editable.toString();
                        if (editable.length() > freeCapacity)//如果长度超过，则分割出可以容纳的内容
                            freeCharSequence = editable.subSequence(0, freeCapacity);

                        for (int i = 0, maxLength = freeCharSequence.length(); i < maxLength; i++) {
                            clipBoard.append(freeCharSequence.charAt(i));//将文字添加到clipBoard中
                            insertIntoTextView(clipBoard.length() - 1);//将文字显示
                        }

                        if (clipBoard.length() == mMaxLength)
                            if (mInputListener != null)
                                mInputListener.inputComplete();
                    }

                    etInput.setText("");
                }
            }
        });

        etInput.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (onKeyDelete()) return true;
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 向索引TextView插入数据
     *
     * @param index
     */
    private void insertIntoTextView(int index) {
        if (index >= 0 && index < mMaxLength) {
            TextViews[index].setText(String.valueOf(clipBoard.charAt(index)));
            switch (style) {

                case Background:
                case EdgeFrame:
                    TextViews[index].setBackgroundDrawable(mBoxFillBackgroundDrawable);
                    break;

                case BelowLine:
                    mBoxFillBottomLineDrawable.setBounds(0, 0, mBoxFillBottomLineDrawable.getMinimumWidth(), mBoxFillBottomLineDrawable.getMinimumHeight());
                    TextViews[index].setCompoundDrawablePadding(mBottomLineMarginPx);
                    TextViews[index].setCompoundDrawables(null, null, null, mBoxFillBottomLineDrawable);
                    break;
            }
            if (mInputListener != null)
                mInputListener.insertContent(clipBoard);
        }
    }

    /**
     * 向索引TextView删除数据
     *
     * @param index
     */
    private void deleteFromTextView(int index) {
        if (index >= 0 && index < mMaxLength) {
            TextViews[index].setText("");

            switch (style) {

                case Background:
                case EdgeFrame:
                    TextViews[index].setBackgroundDrawable(mBoxEmptyBackgroundDrawable);
                    break;

                case BelowLine:
                    mBoxEmptyBottomLineDrawable.setBounds(0, 0, mBoxEmptyBottomLineDrawable.getMinimumWidth(), mBoxEmptyBottomLineDrawable.getMinimumHeight());
                    TextViews[index].setCompoundDrawablePadding(mBottomLineMarginPx);
                    TextViews[index].setCompoundDrawables(null, null, null, mBoxEmptyBottomLineDrawable);
                    break;
            }

            if (mInputListener != null)
                mInputListener.deleteContent(clipBoard);
        }
    }

    private boolean onKeyDelete() {
        if (clipBoard.length() == 0) {
            return true;
        }

        //删除相应位置的字符
        int deleteIndex = clipBoard.length() - 1;//获取最后一位索引
        clipBoard.deleteCharAt(deleteIndex);//从粘贴板上删除
        deleteFromTextView(deleteIndex);//从显示的TextView上删除

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public interface InputListener {

        void inputComplete();

        void deleteContent(CharSequence charSequence);

        void insertContent(CharSequence charSequence);

    }

    /**
     * 清空输入内容
     */
    public void clearText() {
        clipBoard.setLength(0);//清除剪切板数据
        for (int i = 0, length = TextViews.length; i < length; i++) {
            deleteFromTextView(i);
        }
    }

    /**
     * 获取输入文本
     *
     * @return
     */
    public String getText() {
        return clipBoard.toString();
    }

    /**
     * 设置输入的文本
     *
     * @param text
     */
    public void setText(String text) {
        if (text != null && text.matches("^[0-9]*$")) {
            clearText();//清空输入内容
            etInput.setText(text);//重新添加数据
        }
    }

    private InputListener mInputListener;

    public void addInputListener(InputListener mInputListener) {
        this.mInputListener = mInputListener;
    }

    public GridEditView setStyle(Style style) {
        this.style = style;
        return this;
    }

    /**
     * 输入框设置背景
     * --------------------------------------------------------------------
     */

    public GridEditView setBoxEmptyBackgroundDrawable(Drawable mBoxEmptyBackgroundDrawable) {
        this.mBoxEmptyBackgroundDrawable = mBoxEmptyBackgroundDrawable;
        return this;
    }

    public GridEditView setBoxFillBackgroundDrawable(Drawable mBoxFillBackgroundDrawable) {
        this.mBoxFillBackgroundDrawable = mBoxFillBackgroundDrawable;
        return this;
    }

    /**
     * 输入框设置底部线
     * --------------------------------------------------------------------
     */

    public GridEditView setBoxEmptyBottomLineDrawable(Drawable mBoxEmptyBottomLineDrawable) {
        this.mBoxEmptyBottomLineDrawable = mBoxEmptyBottomLineDrawable;
        return this;
    }

    public GridEditView setBoxFillBottomLineDrawable(Drawable mBoxFillBottomLineDrawable) {
        this.mBoxFillBottomLineDrawable = mBoxFillBottomLineDrawable;
        return this;
    }

    /**
     * 设置输入框基本参数
     * --------------------------------------------------------------------
     */

    public GridEditView setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        return this;
    }

    public GridEditView setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        return this;
    }

    public GridEditView setMaxLength(int mMaxLength) {
        this.mMaxLength = mMaxLength;
        return this;
    }

    public GridEditView setInputType(int inputType) {
        this.inputType = inputType;
        return this;
    }

    enum Style {
        EdgeFrame,
        BelowLine,
        Background;

        public static Style classifyStyle(int styleValue) {
            for (Style styleElement : values()) {
                if (styleElement.ordinal() == styleValue)
                    return styleElement;
            }
            return BelowLine;
        }
    }

}