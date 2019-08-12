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
    private float[] strokeDashEffectArr = new float[2];
    //阴影
    private float[] radiusShadowArray = new float[8];
    private Paint shadowPaint;
    private RectF shadowRect;
    private Path shadowPath;
    private float shadowElevation = 0;
    private int shadowColor;//阴影色
    private int shadowBackground;//shadowPaint填充色
    private float shadow_x = 0;//阴影偏移
    private float shadow_y = 0;//阴影偏移
    private Paint clipShadowPaint;
    private boolean clipShadowPath = true;
    //渐变
    private float[] radiusGradientArray = new float[8];
    private int[] gradientColorArr = new int[2];
    private int gradientColorStart;//渐变色-开始
    private int gradientStrokeColor;//边框颜色
    private float gradientStrokeDashWidth;//边框虚线宽度
    private float gradientStrokeDashGap;//边框虚线间隙
    private int gradientStrokeWidth;//边框宽度
    private int gradientColorEnd;//阴影色-结束
    private Rect gradientRect;//渐变区域


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
        float shadowRadiusTopLeft = 0;
        float shadowRadiusTopRight = 0;
        float shadowRadiusBottomLeft = 0;
        float shadowRadiusBottomRight = 0;
        float gradientRadiusTopLeft = 0;
        float gradientRadiusTopRight = 0;
        float gradientRadiusBottomLeft = 0;
        float gradientRadiusBottomRight = 0;
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
            //边框
            strokeColor = typedArray.getColor(R.styleable.TextStrongView_strokeColor, Color.parseColor("#55000000"));
            strokeWidth = typedArray.getDimension(R.styleable.TextStrongView_strokeWidth, strokeWidth);
            float strokeDashWidth = typedArray.getDimension(R.styleable.TextStrongView_strokeDashWidth, 0);
            float strokeDashGap = typedArray.getDimension(R.styleable.TextStrongView_strokeDashGap, 0);
            strokeDashEffectArr[0] = strokeDashWidth;
            strokeDashEffectArr[1] = strokeDashGap;
            float strokeRadius = typedArray.getDimension(R.styleable.TextStrongView_strokeRadius, 0);
            strokeRadiusTopLeft = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusTopLeft, strokeRadius);
            strokeRadiusTopRight = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusTopRight, strokeRadius);
            strokeRadiusBottomLeft = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusBottomLeft, strokeRadius);
            strokeRadiusBottomRight = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusBottomRight, strokeRadius);
            //阴影
            shadowElevation = typedArray.getDimension(R.styleable.TextStrongView_shadowElevation, shadowElevation);
            shadowColor = typedArray.getColor(R.styleable.TextStrongView_shadowColor, Color.BLACK);
            shadowBackground = typedArray.getColor(R.styleable.TextStrongView_shadowBackground, Color.BLACK);
            clipShadowPath = typedArray.getBoolean(R.styleable.TextStrongView_clipShadowPath, clipShadowPath);
            float shadowRadius = typedArray.getDimension(R.styleable.TextStrongView_shadowRadius, 0);
            shadowRadiusTopLeft = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusTopLeft, shadowRadius);
            shadowRadiusTopRight = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusTopRight, shadowRadius);
            shadowRadiusBottomLeft = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusBottomLeft, shadowRadius);
            shadowRadiusBottomRight = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusBottomRight, shadowRadius);
            shadow_x = typedArray.getDimension(R.styleable.TextStrongView_shadow_x, 0);
            shadow_y = typedArray.getDimension(R.styleable.TextStrongView_shadow_y, 0);
            //渐变
            float gradientRadius = typedArray.getDimension(R.styleable.TextStrongView_gradientRadius, 0);
            gradientRadiusTopLeft = typedArray.getDimension(R.styleable.TextStrongView_gradientRadiusTopLeft, gradientRadius);
            gradientRadiusTopRight = typedArray.getDimension(R.styleable.TextStrongView_gradientRadiusTopRight, gradientRadius);
            gradientRadiusBottomLeft = typedArray.getDimension(R.styleable.TextStrongView_gradientRadiusBottomLeft, gradientRadius);
            gradientRadiusBottomRight = typedArray.getDimension(R.styleable.TextStrongView_gradientRadiusBottomRight, gradientRadius);
            gradientColorStart = typedArray.getColor(R.styleable.TextStrongView_gradientColorStart, 0);
            gradientColorEnd = typedArray.getColor(R.styleable.TextStrongView_gradientColorEnd, 0);
            gradientStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.TextStrongView_gradientStrokeWidth, 0);
            gradientStrokeColor = typedArray.getColor(R.styleable.TextStrongView_gradientStrokeColor, 0);
            gradientStrokeDashWidth = typedArray.getDimension(R.styleable.TextStrongView_gradientStrokeDashWidth, 0);
            gradientStrokeDashGap = typedArray.getDimension(R.styleable.TextStrongView_gradientStrokeDashGap, 0);

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
        //--------------------------裁剪--------------------------
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

        //--------------------------边框--------------------------
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
        if (strokeDashEffectArr[0] != 0 && strokeDashEffectArr[1] != 0) {
            DashPathEffect pathEffect = new DashPathEffect(strokeDashEffectArr, 0);
            strokePaint.setPathEffect(pathEffect);
        }
        if (isClip()) {
            strokePaint.setStrokeWidth(strokeWidth*2);
        } else {
            strokePaint.setStrokeWidth(strokeWidth);
        }
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePath = new Path();
        strokeRect = new RectF();

        //--------------------------阴影--------------------------
        radiusShadowArray[0] = shadowRadiusTopLeft;
        radiusShadowArray[1] = shadowRadiusTopLeft;
        radiusShadowArray[2] = shadowRadiusTopRight;
        radiusShadowArray[3] = shadowRadiusTopRight;
        radiusShadowArray[4] = shadowRadiusBottomRight;
        radiusShadowArray[5] = shadowRadiusBottomRight;
        radiusShadowArray[6] = shadowRadiusBottomLeft;
        radiusShadowArray[7] = shadowRadiusBottomLeft;
        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowRect = new RectF();
        shadowPath = new Path();
        clipShadowPaint = new Paint();
        clipShadowPaint.setAntiAlias(true);

        //--------------------------渐变--------------------------
        radiusGradientArray[0] = gradientRadiusTopLeft;
        radiusGradientArray[1] = gradientRadiusTopLeft;
        radiusGradientArray[2] = gradientRadiusTopRight;
        radiusGradientArray[3] = gradientRadiusTopRight;
        radiusGradientArray[4] = gradientRadiusBottomRight;
        radiusGradientArray[5] = gradientRadiusBottomRight;
        radiusGradientArray[6] = gradientRadiusBottomLeft;
        radiusGradientArray[7] = gradientRadiusBottomLeft;
        gradientRect = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isClip()) {
            roundRect.set(0,0,getWidth(),getHeight());
            strokeRect.set(0,0,getWidth(),getHeight());
        } else {
            final float offset = strokeWidth*0.5f;
            strokeRect.set(offset,offset,getWidth()-offset,getHeight()-offset);
        }
        int offset = 0;
        gradientRect.set(-offset,-offset,getWidth()+offset,getHeight()+offset);
    }

    /**
     * 在onDraw剪切圆角会会出现背景色不会被剪切的问题
     * draw(Canvas canvas)方法包含背景色绘制和前景色绘制
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        //画阴影
        if (shadowElevation > 0) {
            Bitmap srcBmp = createShadowBitmap((int) (getWidth()+shadowElevation*2), (int) (getHeight()+shadowElevation*2));
            canvas.drawBitmap(srcBmp, shadow_x-shadowElevation, shadow_y-shadowElevation, clipShadowPaint);//绘制源目标
        }
        //渐变背景
        if (gradientColorStart != 0 || gradientColorEnd != 0) {
            GradientDrawable gradientDrawable = createGradientDrawable(gradientRect);
            gradientDrawable.draw(canvas);
        }
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

    //添加阴影bitmap
    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight) {
        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        shadowRect.set(shadowElevation-shadow_x,shadowElevation-shadow_y,shadowWidth-shadowElevation-shadow_x,shadowHeight-shadowElevation-shadow_y);

        shadowPaint.setColor(shadowBackground);
        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowElevation, shadow_x, shadow_y, shadowColor);
        }
        shadowPath.reset();
        shadowPath.addRoundRect(shadowRect, radiusShadowArray, Path.Direction.CW);
        if (clipShadowPath) {
            //保留shadowPath路径以外区域
            canvas.clipPath(shadowPath, Region.Op.DIFFERENCE);
        }
        canvas.drawPath(shadowPath, shadowPaint);

        return output;
    }

    //渐变
    private GradientDrawable createGradientDrawable(Rect bounds) {
        gradientColorArr[0] = gradientColorStart;
        gradientColorArr[1] = gradientColorEnd;
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,gradientColorArr);
        gradientDrawable.setBounds(bounds);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadii(radiusGradientArray);
        if (gradientStrokeWidth != 0)
            gradientDrawable.setStroke(gradientStrokeWidth,gradientStrokeColor,gradientStrokeDashWidth,gradientStrokeDashGap);
        return gradientDrawable;
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
                        setDrawableSelected(!isDrawableSelected);
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
}