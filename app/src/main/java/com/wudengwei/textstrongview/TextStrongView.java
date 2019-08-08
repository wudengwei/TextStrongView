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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
    private boolean isSetDrawableSelected = false;

    private boolean isTextDrawableCenter = false;
    //点击效果设置
    private Paint clickPaint;
    //two radius values [X, Y]. The corners are ordered top-left, top-right, bottom-right, bottom-left
    private float[] radiusArray = new float[8];
    private Paint roundPaint;//裁剪圆角的画笔
    private RectF roundRect;//包含圆角矩形的路径的layer区域
    private Path roundPath;//圆角矩形的路径
    //边框
    private float[] radiusStrokeArray = new float[8];
    private Paint strokePaint;//边框的画笔
    private RectF strokeRect;//边框的区域
    private Path strokePath;//边框的路径
    private int strokeColor;//边框颜色
    private float strokeWidth = 0;//边框宽度

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
        float strokeRadiusTopLeft = 0;
        float strokeRadiusTopRight = 0;
        float strokeRadiusBottomLeft = 0;
        float strokeRadiusBottomRight = 0;
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

            strokeColor = typedArray.getColor(R.styleable.TextStrongView_strokeColor, Color.BLACK);
            strokeWidth = typedArray.getDimension(R.styleable.TextStrongView_strokeWidth, strokeWidth);
            float strokeRadius = typedArray.getDimension(R.styleable.TextStrongView_strokeRadius, 0);
            strokeRadiusTopLeft = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusTopLeft, strokeRadius);
            strokeRadiusTopRight = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusTopRight, strokeRadius);
            strokeRadiusBottomLeft = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusBottomLeft, strokeRadius);
            strokeRadiusBottomRight = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusBottomRight, strokeRadius);

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
        roundPaint.setColor(Color.WHITE);
        roundPaint.setAntiAlias(true);
        roundPaint.setStyle(Paint.Style.FILL);
        //圆角矩形的路径
        roundPath = new Path();
        //包含圆角矩形的路径的layer区域
        roundRect = new RectF();

        radiusStrokeArray[0] = strokeRadiusTopLeft;
        radiusStrokeArray[1] = strokeRadiusTopLeft;
        radiusStrokeArray[2] = strokeRadiusTopRight;
        radiusStrokeArray[3] = strokeRadiusTopRight;
        radiusStrokeArray[4] = strokeRadiusBottomRight;
        radiusStrokeArray[5] = strokeRadiusBottomRight;
        radiusStrokeArray[6] = strokeRadiusBottomLeft;
        radiusStrokeArray[7] = strokeRadiusBottomLeft;
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(strokeColor);
        if (isClip()) {
            strokePaint.setStrokeWidth(strokeWidth*2);
        } else {
            strokePaint.setStrokeWidth(strokeWidth);
        }
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePath = new Path();
        strokeRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isClip()) {
            roundRect.set(0,0,getWidth(),getHeight());
            strokeRect.set(0,0,getWidth(),getHeight());
        } else {
            strokeRect.set(strokeWidth,strokeWidth,getWidth()-strokeWidth,getHeight()-strokeWidth);
        }
    }

    /**
     * 在onDraw剪切圆角会会出现背景色不会被剪切的问题
     * draw(Canvas canvas)方法包含背景色绘制和前景色绘制
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        if (isClip()) {
            //使用canvas.saveLayer()配合roundPaint.setXfermode裁剪圆角区域
            canvas.saveLayer(roundRect, null, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            //裁剪圆角区域
            clipRound(canvas);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if (drawableTop != null) {
//            Rect rect = new Rect();
////            float textWidth = getPaint().measureText(getText().toString());
//            getPaint().getTextBounds(getText().toString(),0,getText().toString().length(),rect);
//            int textWidth = rect.right - rect.left;
//            float textHeight = rect.bottom - rect.top;
//            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
//            Log.e("onDraw","getPaint().getTextBounds测量出来的textHeight: "+textHeight+"==="+(fontMetrics.bottom-fontMetrics.top));
//            textHeight = getPaint().descent()-getPaint().ascent();
//            Log.e("onDraw","getPaint().descent()-getPaint().ascent()测量出来的textHeight: "+textHeight+"==="+(fontMetrics.bottom-fontMetrics.top));
//            //文本行数*行高度（行高度包含getLineSpacingExtra()）
//            if (getMaxLines() < getLineCount()) {
//                textHeight = getMaxLines()*getLineHeight();
//            } else {
//                textHeight = getLineCount()*getLineHeight();
//            }
//            Log.e("onDraw","文本行数+额外行间距的textHeight: "+textHeight);
//            Rect lineRect = new Rect();
//            Log.e("onDraw","getLineCount(): "+getLineCount()+" ,getLineSpacingMultiplier(): "+getLineSpacingMultiplier()+" ,getLineHeight(): "+getLineHeight());
//            int drawablePadding = getCompoundDrawablePadding();
//            //高度居中
//            int drawableHeight = drawableTop.getIntrinsicHeight();
//            float bodyHeight = textHeight + drawableTopHeight + drawablePadding;
//            float dy = (getHeight() - bodyHeight) * 0.5f;
//            canvas.translate(0, (getHeight() - bodyHeight) * 0.5f);
////            setPaddingRelative(0, (int) dy,0,0);
//            Log.e("onDraw","getHeight(): "+getHeight()+" ,bodyHeight: "+bodyHeight+" ,textHeight: "+textHeight+" ,drawableHeight: "+drawableTopHeight+" ,drawablePadding: "+drawablePadding);
//
//            int drawableWidth = drawableTop.getIntrinsicWidth();
//            float bodyWidth = textWidth + drawableWidth + drawablePadding;
//            //canvas.translate((getWidth() - bodyWidth) / 2, 0);
//        }
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
                setPaddingRelative(0, dy,0,dy);
//                canvas.translate(0, dy);
            }
            if (drawableLeft != null || drawableRight != null) {
                float textWidth;
                int drawableWidth;
                textWidth = getPaint().measureText(getText().toString());
                drawableWidth = drawableLeftWidth + drawableRightWidth;
                float bodyWidth = textWidth + drawableWidth + getCompoundDrawablePadding();
                int dx = (int) ((getWidth() - bodyWidth) * 0.5f);
                setPaddingRelative(dx, 0,dx,0);
            }
        }
        super.onDraw(canvas);
        //画边框
        if (strokeWidth > 0) {
            strokePath.addRoundRect(strokeRect,radiusStrokeArray, Path.Direction.CW);
            canvas.drawPath(strokePath,strokePaint);
        }
    }

    private boolean isClip() {
        return radiusArray[0] != 0 || radiusArray[1] != 0 || radiusArray[2] != 0 || radiusArray[3] != 0
                || radiusArray[4] != 0 || radiusArray[5] != 0 || radiusArray[6] != 0 || radiusArray[7] != 0;
    }

    //裁剪圆角区域
    private void clipRound(Canvas canvas) {
        roundPath.reset();
        roundPath.addRoundRect(roundRect, radiusArray, Path.Direction.CW);
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