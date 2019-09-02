package com.wudengwei.textstrongview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;


/**
 * Copyright (C)
 * FileName: TextStrongView
 * Author: wudengwei
 * Date: 2019/8/23 22:27
 * Description: ${DESCRIPTION}
 */
public class ShadowHelper {
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

    public ShadowHelper(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextStrongView);
            shadowElevation = typedArray.getDimension(R.styleable.TextStrongView_shadowElevation, shadowElevation);
            shadowColor = typedArray.getColor(R.styleable.TextStrongView_shadowColor, Color.BLACK);
            shadowBackground = typedArray.getColor(R.styleable.TextStrongView_shadowBackground, Color.WHITE);
            clipShadowPath = typedArray.getBoolean(R.styleable.TextStrongView_clipShadowPath, clipShadowPath);
            float shadowRadius = typedArray.getDimension(R.styleable.TextStrongView_shadowRadius, 0);
            float shadowRadiusTopLeft = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusTopLeft, shadowRadius);
            float shadowRadiusTopRight = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusTopRight, shadowRadius);
            float shadowRadiusBottomLeft = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusBottomLeft, shadowRadius);
            float shadowRadiusBottomRight = typedArray.getDimension(R.styleable.TextStrongView_shadowRadiusBottomRight, shadowRadius);
            shadow_x = typedArray.getDimension(R.styleable.TextStrongView_shadow_x, 0);
            shadow_y = typedArray.getDimension(R.styleable.TextStrongView_shadow_y, 0);
            typedArray.recycle();

            radiusShadowArray[0] = shadowRadiusTopLeft;
            radiusShadowArray[1] = shadowRadiusTopLeft;
            radiusShadowArray[2] = shadowRadiusTopRight;
            radiusShadowArray[3] = shadowRadiusTopRight;
            radiusShadowArray[4] = shadowRadiusBottomRight;
            radiusShadowArray[5] = shadowRadiusBottomRight;
            radiusShadowArray[6] = shadowRadiusBottomLeft;
            radiusShadowArray[7] = shadowRadiusBottomLeft;
        }

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowRect = new RectF();
        shadowPath = new Path();
        clipShadowPaint = new Paint();
        clipShadowPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas, int width, int height, boolean isInEditMode) {
        //画阴影
        if (shadowElevation > 0) {
            Bitmap srcBmp = createShadowBitmap((int) (width + shadowElevation * 2), (int) (height + shadowElevation * 2),isInEditMode);
            canvas.drawBitmap(srcBmp, shadow_x - shadowElevation, shadow_y - shadowElevation, clipShadowPaint);//绘制源目标
        }
    }

    //添加阴影bitmap
    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, boolean isInEditMode) {
        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        shadowRect.set(shadowElevation-shadow_x,shadowElevation-shadow_y,shadowWidth-shadowElevation-shadow_x,shadowHeight-shadowElevation-shadow_y);

        shadowPaint.setColor(shadowBackground);
        if (!isInEditMode) {
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

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public void setShadowElevation(float shadowElevation) {
        this.shadowElevation = shadowElevation;
    }
}
