package com.wudengwei.textstrongview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Copyright (C)
 * FileName: TextStrongView
 * Author: wudengwei
 * Date: 2019/8/23 22:27
 * Description: ${DESCRIPTION}
 */
public class StrokeHelper {
    private float[] radiusStrokeArray = new float[8];
    private Paint strokePaint;//边框的画笔
    private RectF strokeRect;//边框的区域
    private Path strokePath;//边框的路径
    private int strokeColor;//边框颜色
    private float strokeWidth = 0;//边框宽度
    private float[] strokeDashEffectArr = new float[2];

    public StrokeHelper(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextStrongView);
            strokeColor = typedArray.getColor(R.styleable.TextStrongView_strokeColor, Color.parseColor("#55000000"));
            strokeWidth = typedArray.getDimension(R.styleable.TextStrongView_strokeWidth, strokeWidth);
            float strokeDashWidth = typedArray.getDimension(R.styleable.TextStrongView_strokeDashWidth, 0);
            float strokeDashGap = typedArray.getDimension(R.styleable.TextStrongView_strokeDashGap, 0);
            strokeDashEffectArr[0] = strokeDashWidth;
            strokeDashEffectArr[1] = strokeDashGap;
            float strokeRadius = typedArray.getDimension(R.styleable.TextStrongView_strokeRadius, 0);
            float strokeRadiusTopLeft = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusTopLeft, strokeRadius);
            float strokeRadiusTopRight = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusTopRight, strokeRadius);
            float strokeRadiusBottomLeft = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusBottomLeft, strokeRadius);
            float strokeRadiusBottomRight = typedArray.getDimension(R.styleable.TextStrongView_strokeRadiusBottomRight, strokeRadius);

            typedArray.recycle();

            radiusStrokeArray[0] = strokeRadiusTopLeft;
            radiusStrokeArray[1] = strokeRadiusTopLeft;
            radiusStrokeArray[2] = strokeRadiusTopRight;
            radiusStrokeArray[3] = strokeRadiusTopRight;
            radiusStrokeArray[4] = strokeRadiusBottomRight;
            radiusStrokeArray[5] = strokeRadiusBottomRight;
            radiusStrokeArray[6] = strokeRadiusBottomLeft;
            radiusStrokeArray[7] = strokeRadiusBottomLeft;
        }
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        if (strokeDashEffectArr[0] != 0 && strokeDashEffectArr[1] != 0) {
            DashPathEffect pathEffect = new DashPathEffect(strokeDashEffectArr, 0);
            strokePaint.setPathEffect(pathEffect);
        }
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePath = new Path();
        strokeRect = new RectF();
    }

    public void setStrokeRect(float left, float top, float right, float bottom) {
        strokeRect.set(left,top,right,bottom);
    }

    public void draw(Canvas canvas) {
        if (strokeWidth > 0) {
            strokePath.addRoundRect(strokeRect,radiusStrokeArray, Path.Direction.CW);
            canvas.drawPath(strokePath,strokePaint);
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        strokePaint.setStrokeWidth(strokeWidth);
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        strokePaint.setColor(strokeColor);
    }
}
