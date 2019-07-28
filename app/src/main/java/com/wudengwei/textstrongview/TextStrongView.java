package com.wudengwei.textstrongview;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
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
    private boolean isSetDrawableSelected = false;

    //点击效果设置
    private Paint clickPaint;
    //two radius values [X, Y]. The corners are ordered top-left, top-right, bottom-right, bottom-left
    private float[] radiusArray = new float[8];
    private Paint roundPaint;//裁剪圆角的画笔
    private RectF roundRect;//包含圆角矩形的路径的layer区域
    private Path roundPath;//圆角矩形的路径

    public TextStrongView(Context context) {
        this(context, null);
    }

    public TextStrongView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextStrongView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //四个边角的圆角半径
        float topLeftRadius = 0;
        float topRightRadius = 0;
        float bottomLeftRadius = 0;
        float bottomRightRadius = 0;
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
            //裁剪圆角
            float radius = typedArray.getDimension(R.styleable.TextStrongView_radius, 0);
            topLeftRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusTopLeft, radius);
            topRightRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusTopRight, radius);
            bottomLeftRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusBottomLeft, radius);
            bottomRightRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusBottomRight, radius);
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
        radiusArray[0] = topLeftRadius;
        radiusArray[1] = topLeftRadius;
        radiusArray[2] = topRightRadius;
        radiusArray[3] = topRightRadius;
        radiusArray[4] = bottomRightRadius;
        radiusArray[5] = bottomRightRadius;
        radiusArray[6] = bottomLeftRadius;
        radiusArray[7] = bottomLeftRadius;
        //裁剪圆角的画笔
        roundPaint = new Paint();
        //圆角矩形的路径
        roundPath = new Path();
        //包含圆角矩形的路径的layer区域
        roundRect = new RectF();
    }

    /**
     * 在onDraw剪切圆角会会出现背景色不会被剪切的问题
     * draw(Canvas canvas)方法包含背景色绘制和前景色绘制
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        //使用canvas.saveLayer()配合roundPaint.setXfermode裁剪圆角区域
        roundRect.set(0,0,getWidth(),getHeight());
        canvas.saveLayer(roundRect, null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        if (radiusArray[0] != 0 || radiusArray[2] != 0 || radiusArray[4] != 0 || radiusArray[6] != 0) {
            //裁剪圆角区域
            clipRound(canvas);
        }
        canvas.restore();
    }

    //裁剪圆角区域
    private void clipRound(Canvas canvas) {
        roundPath.reset();
        roundPath.addRoundRect(roundRect, radiusArray, Path.Direction.CW);
        //画笔设置
        roundPaint.setColor(Color.WHITE);
        roundPaint.setAntiAlias(true);
        roundPaint.setStyle(Paint.Style.FILL);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            roundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawPath(roundPath, roundPaint);
        } else {
            roundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            final Path path = new Path();
            path.addRect(0, 0, getWidth(), getHeight(), Path.Direction.CW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                path.op(roundPath, Path.Op.DIFFERENCE);
            }
            canvas.drawPath(path, roundPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() > getLeft() && event.getX() <getRight()
                        && event.getY() > getTop() && event.getY() <getBottom()) {
                    if (isSetDrawableSelectedFlag) {
                        setDrawableIsSelected(!isSetDrawableSelected);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
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
        if (isSetDrawableSelected) {
            isSetDrawableSelected = false;
        }
        drawableLeft = left;
        drawableTop = top;
        drawableRight = right;
        drawableBottom = bottom;
        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    public void setDrawableSelected(@Nullable Drawable left, @Nullable Drawable top,
                            @Nullable Drawable right, @Nullable Drawable bottom) {
        if (!isSetDrawableSelected) {
            isSetDrawableSelected = true;
        }
        drawableLeftSelected = left;
        drawableTopSelected = top;
        drawableRightSelected = right;
        drawableBottomSelected = bottom;
        setCompoundDrawables(drawableLeftSelected, drawableTopSelected, drawableRightSelected, drawableBottomSelected);
    }

    /**
     * 主动设置是否被选中
     * @param isSelected
     */
    public void setDrawableIsSelected(boolean isSelected) {
        isSetDrawableSelected = isSelected;
        if (isSetDrawableSelected) {
            setDrawableSelected();
        } else {
            setDrawable();
        }
    }
}
