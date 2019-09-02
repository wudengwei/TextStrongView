package com.wudengwei.textstrongview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Copyright (C)
 * FileName: TextStrongView
 * Author: wudengwei
 * Date: 2019/8/23 22:27
 * Description: ${DESCRIPTION}
 */
public class ClipHelper {
    //two radius values [X, Y]. The corners are ordered top-left, top-right, bottom-right, bottom-left
    private float[] radiusArray = new float[8];
    private Paint roundPaint;//裁剪圆角的画笔
    private RectF roundRect;//包含圆角矩形的路径的layer区域
    private Path roundPath;//圆角矩形的路径

    public ClipHelper(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextStrongView);
            float radius = typedArray.getDimension(R.styleable.TextStrongView_radius, 0);
            float topLeftRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusTopLeft, radius);
            float topRightRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusTopRight, radius);
            float bottomLeftRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusBottomLeft, radius);
            float bottomRightRadius = typedArray.getDimension(R.styleable.TextStrongView_radiusBottomRight, radius);
            typedArray.recycle();

            radiusArray[0] = topLeftRadius;
            radiusArray[1] = topLeftRadius;
            radiusArray[2] = topRightRadius;
            radiusArray[3] = topRightRadius;
            radiusArray[4] = bottomRightRadius;
            radiusArray[5] = bottomRightRadius;
            radiusArray[6] = bottomLeftRadius;
            radiusArray[7] = bottomLeftRadius;
        }

        //裁剪圆角的画笔
        roundPaint = new Paint();
        roundPaint.setColor(Color.WHITE);
        roundPaint.setAntiAlias(true);
        roundPaint.setStyle(Paint.Style.FILL);
        //圆角矩形的路径
        roundPath = new Path();
        //包含圆角矩形的路径的layer区域
        roundRect = new RectF();
    }

    public void setRoundRect(int left, int top, int right, int bottom) {
        if (isClip()) {
            roundRect.set(left,top,right,bottom);
        }
    }

    public RectF getRoundRect() {
        return roundRect;
    }

    public boolean isClip() {
        return radiusArray[0] != 0 || radiusArray[1] != 0 || radiusArray[2] != 0 || radiusArray[3] != 0
                || radiusArray[4] != 0 || radiusArray[5] != 0 || radiusArray[6] != 0 || radiusArray[7] != 0;
    }

    //裁剪圆角区域
    public void clipRound(Canvas canvas) {
        roundPath.reset();
        roundPath.addRoundRect(roundRect, radiusArray, Path.Direction.CW);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            roundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawPath(roundPath, roundPaint);
        } else {
            roundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            final Path path = new Path();
            path.addRect(roundRect, Path.Direction.CW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                path.op(roundPath, Path.Op.DIFFERENCE);
            }
            canvas.drawPath(path, roundPaint);
        }
    }
}
