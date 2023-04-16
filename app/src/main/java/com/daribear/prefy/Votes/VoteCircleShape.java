package com.daribear.prefy.Votes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.daribear.prefy.R;

public class VoteCircleShape extends View {

    private Paint shadowColorPaint;
    private Paint mCirclePaint;//The brush in the center garden
    private Paint mArcPaint;//The brush of the outer ring
    private int mCircleX;//Set the center x coordinate
    private int mCircleY;//Set the center y coordinate
    private float strokeAngle;//Current angle
    private RectF mArcRectF;//Draw the circumscribed rectangle of the center circle, used to draw the circle

    private int radius;//The radius of the circle
    private int mCircleBackground;//The background color of the middle circle
    private int mRingColor;//The color of the outer ring The color of the second drawing circle
    private int roundColor; //The color of the outer ring The color of the first drawing circle
    private float strokeRadiusVariable;


    public VoteCircleShape(Context context) {
        super(context);
        init(context);
    }

    public VoteCircleShape(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCircleBackground = Color.TRANSPARENT;//context.getColor(R.color.very_transparent_red);
        roundColor = Color.TRANSPARENT;
        mRingColor = Color.TRANSPARENT;//context.getColor(R.color.red);
        strokeRadiusVariable = .05F;
        init(context);
    }

    public VoteCircleShape(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context){
        strokeAngle = 180;
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleBackground);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mArcPaint = new Paint();
        shadowColorPaint = new Paint();
        shadowColorPaint.setColor(roundColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(mRingColor);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth((float) (strokeRadiusVariable * radius));

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        mCircleX = getMeasuredWidth()/2;
        mCircleY = getMeasuredHeight()/2;
        if (radius >mCircleX) {
            radius = mCircleX;
            radius = (int) (mCircleX- strokeRadiusVariable * radius);
            mArcPaint.setStrokeWidth((float) (strokeRadiusVariable * radius));
        }
        mArcRectF = new RectF(mCircleX- radius, mCircleY- radius, mCircleX+ radius, mCircleY+ radius);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCircleX, mCircleY, radius, mCirclePaint);
        canvas.drawCircle(mCircleX, mCircleY, radius, shadowColorPaint);
        mArcPaint.setStrokeWidth((float) (strokeRadiusVariable * radius));
        canvas.drawArc(mArcRectF, -90 ,strokeAngle, false, mArcPaint);
    }

    public void setVoteCircleParameters(Integer radius, Integer percentage){
        this.radius = radius;
        Double stokeTest = ((double)percentage / 100) * 360;
        this.strokeAngle =  Math.round(stokeTest);
        mCircleX = getMeasuredWidth()/2;
        mCircleY = getMeasuredHeight()/2;
    }

    public void setColours(Integer StrokeColour, Integer innerCircleColour){
        this.mCircleBackground = innerCircleColour;
        this.mRingColor = StrokeColour;
        mCirclePaint.setColor(mCircleBackground);
        mArcPaint.setColor(mRingColor);
    }
}