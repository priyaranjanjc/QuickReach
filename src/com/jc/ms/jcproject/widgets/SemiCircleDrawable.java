package com.jc.ms.jcproject.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class SemiCircleDrawable extends Drawable  {

    private Paint paint;
    private RectF rectF;
    private int color;
    private Direction angle;

    public enum Direction
    {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public SemiCircleDrawable() {
        this(Color.BLUE, Direction.LEFT);
    }

    public SemiCircleDrawable(int color, Direction angle) {
        this.color = color;
        this.angle = angle;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        rectF = new RectF();
    }

    public int getColor() {
        return color;
    }

    /**
     * A 32bit color not a color resources.
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        Rect bounds = getBounds();



        if(angle == Direction.LEFT)
            canvas.drawCircle(bounds.left, bounds.bottom, bounds.right, paint);
        else if(angle == Direction.TOP)
        	canvas.drawCircle(bounds.top, bounds.left, bounds.right, paint);
        else if(angle == Direction.RIGHT)
        	canvas.drawCircle(bounds.right, bounds.bottom, bounds.right, paint);
        else if(angle == Direction.BOTTOM)
        	canvas.drawCircle(bounds.top, bounds.right, bounds.right, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // Has no effect
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Has no effect
    }

    @Override
    public int getOpacity() {
        // Not Implemented
        return 0;
    }

}