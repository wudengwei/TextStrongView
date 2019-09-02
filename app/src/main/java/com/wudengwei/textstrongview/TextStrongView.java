package com.wudengwei.textstrongview;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright (C)
 * FileName: TextStrongView
 * Author: wudengwei
 * Date: 2019/7/28 0:27
 * Description: ${DESCRIPTION}
 */
public class TextStrongView extends AppCompatTextView {
    // 左、上、右、下图标的宽和高
    private int drawableLeftWidth, drawableTopWidth, drawableRightWidth, drawableBottomWidth;
    private int drawableLeftHeight, drawableTopHeight, drawableRightHeight, drawableBottomHeight;
    // 左、上、右、下图标
    Drawable drawableLeft,drawableTop,drawableRight,drawableBottom;
    // 左、上、右、下图标选中
    Drawable drawableLeftSelected,drawableTopSelected,drawableRightSelected,drawableBottomSelected;
    //app:drawableLeftSelected...设置后，点击后左、上、右、下图标自动设置为app:drawableLeftSelected...对应的图片
    private boolean isSetDrawableSelectedFlag = false;
    //是否设置左、上、右、下图标选择
    private boolean isDrawableSelected = false;

    private boolean isTextDrawableCenter = false;
    //点击效果设置
    private Paint clickPaint;
    //裁剪
    private ClipHelper mClipHelper;
    //边框
    private StrokeHelper mStrokeHelper;
    //阴影
    private ShadowHelper mShadowHelper;
    //渐变
    private GradientHelper mGradientHelper;


    public TextStrongView(Context context) {
        this(context, null);
    }

    public TextStrongView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextStrongView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextStrongView);
            drawableLeftWidth = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableLeftWidth, 0);
            drawableTopWidth = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableTopWidth, 0);
            drawableRightWidth = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableRightWidth, 0);
            drawableBottomWidth = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableBottomWidth, 0);
            drawableLeftHeight = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableLeftHeight, 0);
            drawableTopHeight = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableTopHeight, 0);
            drawableRightHeight = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableRightHeight, 0);
            drawableBottomHeight = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_drawableBottomHeight, 0);
            drawableLeft = typedArray.getDrawable(R.styleable.TextStrongView_drawableLeft);
            drawableTop = typedArray.getDrawable(R.styleable.TextStrongView_drawableTop);
            drawableRight = typedArray.getDrawable(R.styleable.TextStrongView_drawableRight);
            drawableBottom = typedArray.getDrawable(R.styleable.TextStrongView_drawableBottom);
            drawableLeftSelected = typedArray.getDrawable(R.styleable.TextStrongView_drawableLeftSelected);
            drawableTopSelected = typedArray.getDrawable(R.styleable.TextStrongView_drawableTopSelected);
            drawableRightSelected = typedArray.getDrawable(R.styleable.TextStrongView_drawableRightSelected);
            drawableBottomSelected = typedArray.getDrawable(R.styleable.TextStrongView_drawableBottomSelected);
            isTextDrawableCenter = typedArray.getBoolean(R.styleable.TextStrongView_isTextDrawableCenter, isTextDrawableCenter);

            drawableLeft = getCompoundDrawables()[0]==null?drawableLeft:getCompoundDrawables()[0];
            drawableTop = getCompoundDrawables()[1]==null?drawableTop:getCompoundDrawables()[1];
            drawableRight = getCompoundDrawables()[2]==null?drawableRight:getCompoundDrawables()[2];
            drawableBottom = getCompoundDrawables()[3]==null?drawableBottom:getCompoundDrawables()[3];
            //设置图片文字居中
            if (isTextDrawableCenter) {
                if (drawableTop != null || drawableBottom != null) {
                    setGravity(Gravity.CENTER_HORIZONTAL);
                }
                if (drawableLeft != null || drawableRight != null) {
                    setGravity(Gravity.CENTER_VERTICAL);
                }
            }
            typedArray.recycle();
            if (drawableLeftSelected == null && drawableTopSelected == null
                    && drawableRightSelected == null && drawableBottomSelected == null) {
                isSetDrawableSelectedFlag = false;
            } else {
                isSetDrawableSelectedFlag = true;
                if (!isClickable()) {
                    setClickable(true);
                }
            }
            //重新设置控件左、上、右、下图标（）
            setDrawable();
        }
        //--------------------------裁剪--------------------------
        mClipHelper = new ClipHelper(context, attrs);
        //--------------------------边框--------------------------
        mStrokeHelper = new StrokeHelper(context, attrs);
        if (mClipHelper.isClip()) {
            mStrokeHelper.setStrokeWidth(mStrokeHelper.getStrokeWidth()*2);
        } else {
            mStrokeHelper.setStrokeWidth(mStrokeHelper.getStrokeWidth());
        }
        //--------------------------阴影--------------------------
        mShadowHelper = new ShadowHelper(context,attrs);
        //--------------------------渐变--------------------------
        mGradientHelper = new GradientHelper(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mClipHelper.setRoundRect(0,0,w,h);
        if (mClipHelper.isClip()) {
            mStrokeHelper.setStrokeRect(0,0,w,h);
        } else {
            final float offset = mStrokeHelper.getStrokeWidth()*0.5f;
            mStrokeHelper.setStrokeRect(offset,offset,w-offset,h-offset);
        }
        int offset = 0;
        mGradientHelper.setGradientRect(-offset,-offset,w+offset,h+offset);
    }

    /**
     * 在onDraw剪切圆角会会出现背景色不会被剪切的问题
     * draw(Canvas canvas)方法包含背景色绘制和前景色绘制
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        //画阴影
        mShadowHelper.draw(canvas,getWidth(),getHeight(),isInEditMode());
        //渐变背景
        mGradientHelper.draw(canvas);
        //裁剪
        if (mClipHelper.isClip()) {
            //使用canvas.saveLayer()配合roundPaint.setXfermode裁剪圆角区域
            canvas.saveLayer(mClipHelper.getRoundRect(), null, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            //裁剪圆角区域
            mClipHelper.clipRound(canvas);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }



    private boolean isSetPadding = false;
    @Override
    protected void onDraw(Canvas canvas) {
        //图文居中
        if (isTextDrawableCenter) {
            if (drawableTop != null || drawableBottom != null) {
                int textHeight,drawableHeight;
                //文本行数*行高度（行高度包含getLineSpacingExtra()）
                if (getMaxLines() < getLineCount()) {
                    textHeight = getMaxLines()*getLineHeight();
                } else {
                    textHeight = getLineCount()*getLineHeight();
                }
                drawableHeight = drawableTopHeight + drawableBottomHeight;
                float bodyHeight = textHeight + drawableHeight + getCompoundDrawablePadding();
                int dy = (int) ((getHeight() - bodyHeight) * 0.5f);
                if (!isSetPadding) {
                    setPaddingRelative(0, dy,0,dy);
                    isSetPadding = true;
                }
//                canvas.translate(0, dy);
            }
            if (drawableLeft != null || drawableRight != null) {
                float textWidth;
                int drawableWidth;
                textWidth = getPaint().measureText(getText().toString());
                drawableWidth = drawableLeftWidth + drawableRightWidth;
                float bodyWidth = textWidth + drawableWidth + getCompoundDrawablePadding();
                int dx = (int) ((getWidth() - bodyWidth) * 0.5f);
                if (!isSetPadding) {
                    setPaddingRelative(dx, 0,dx,0);
                    isSetPadding = true;
                }
            }
        }
        super.onDraw(canvas);
        //画边框
        mStrokeHelper.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchPointInView((int) event.getRawX(),(int) event.getRawY())) {
                    if (isSetDrawableSelectedFlag) {
                        setDrawableSelected(!isDrawableSelected);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouchPointInView(int x, int y) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + getMeasuredWidth();
        int bottom = top + getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left && x <= right) {
            return true;
        }
        return false;
    }

    /**
     * 重新设置控件左、上、右、下图标（会导致android:drawableLeft、android:drawableTop、android:drawableRight、android:drawableBottom图片设置失效）
     */
    private void setDrawable() {
        if (drawableLeft != null) {
            drawableLeft.setBounds(0,0, drawableLeftWidth,drawableLeftHeight);
        }
        if (drawableTop != null) {
            drawableTop.setBounds(0,0, drawableTopWidth,drawableTopHeight);
        }
        if (drawableRight != null) {
            drawableRight.setBounds(0,0, drawableRightWidth,drawableRightHeight);
        }
        if (drawableBottom != null) {
            drawableBottom.setBounds(0,0, drawableBottomWidth,drawableBottomHeight);
        }
        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    /**
     * 设置控件左、上、右、下图标选中状态时的图标
     */
    private void setDrawableSelected() {
        if (drawableLeftSelected == null && drawableTopSelected == null
                && drawableRightSelected == null && drawableBottomSelected == null) {
            return;
        }
        if (drawableLeftSelected != null) {
            drawableLeftSelected.setBounds(0,0, drawableLeftWidth,drawableLeftHeight);
        }
        if (drawableTopSelected != null) {
            drawableTopSelected.setBounds(0,0, drawableTopWidth,drawableTopHeight);
        }
        if (drawableRightSelected != null) {
            drawableRightSelected.setBounds(0,0, drawableRightWidth,drawableRightHeight);
        }
        if (drawableBottomSelected != null) {
            drawableBottomSelected.setBounds(0,0, drawableBottomWidth,drawableBottomHeight);
        }
        setCompoundDrawables(drawableLeftSelected==null?drawableLeft:drawableLeftSelected, drawableTopSelected==null?drawableTop:drawableTopSelected, drawableRightSelected==null?drawableRight:drawableRightSelected, drawableBottomSelected==null?drawableBottom:drawableBottomSelected);
    }

    public void setDrawable(@Nullable Drawable left, @Nullable Drawable top,
                            @Nullable Drawable right, @Nullable Drawable bottom) {
        if (isDrawableSelected) {
            isDrawableSelected = false;
        }
        drawableLeft = left;
        drawableTop = top;
        drawableRight = right;
        drawableBottom = bottom;
        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    public void setDrawableSelected(@Nullable Drawable left, @Nullable Drawable top,
                                    @Nullable Drawable right, @Nullable Drawable bottom) {
        if (!isDrawableSelected) {
            isDrawableSelected = true;
        }
        drawableLeftSelected = left;
        drawableTopSelected = top;
        drawableRightSelected = right;
        drawableBottomSelected = bottom;
        setCompoundDrawables(drawableLeftSelected, drawableTopSelected, drawableRightSelected, drawableBottomSelected);
    }

    public boolean isDrawableSelected() {
        return isDrawableSelected;
    }

    /**
     * 主动设置是否被选中
     * @param drawableSelected true 选中，false 不选中
     */
    public void setDrawableSelected(boolean drawableSelected) {
        isDrawableSelected = drawableSelected;
        if (isDrawableSelected) {
            setDrawableSelected();
        } else {
            setDrawable();
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeHelper.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeHelper.setStrokeColor(strokeColor);
        invalidate();
    }

    public void setDrawableLeft(Drawable drawableLeft) {
        this.drawableLeft = drawableLeft;
        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    public void setGradientColorStart(int color) {
        mGradientHelper.setGradientColorStart(color);
        invalidate();
    }

    public void setGradientColorEnd(int color) {
        mGradientHelper.setGradientColorEnd(color);
        invalidate();
    }

    public void setShadowColor(int shadowColor) {
        mShadowHelper.setShadowColor(shadowColor);
        invalidate();
    }

    public void setShadowElevation(float shadowElevation) {
        mShadowHelper.setShadowElevation(shadowElevation);
        invalidate();
    }
}