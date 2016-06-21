package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 6/16/2016.
 */
public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";
    public static final String ARG_BOXEN = "boxes";
    public static final String ARG_SUPER_STATE = "superState";
    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    private float angleBetweenLunes(float fx, float fy, float sx, float sy, float nfx, float nfy, float nsx, float nsy) {
        float angle1 = (float) Math.atan2(sy-fy, sx-fx);
        float angle2 = (float) Math.atan2(nsy-nfy, nsx-nfx);

        float angle = (float) Math.toDegrees(angle2 - angle1) % 360;

        if(angle < 0f) { angle += 360f; }
        return angle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF start = new PointF();
        PointF current;
        int pointerId;
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        for(int i=0; i<pointerCount; i++){
            current = new PointF(event.getX(i), event.getY(i));
            pointerId = event.getPointerId(i);

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    if(pointerId == 0) {
                        if(mCurrentBox == null) {
                            mCurrentBox = new Box(current);
                            mBoxen.add(mCurrentBox);
                        }
                        break;
                    } else if(pointerId == 1 && mCurrentBox != null) {
                        start = current;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if(pointerId == 0) {
                        mCurrentBox = null;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(mCurrentBox != null) {
                        if(pointerId == 0) {
                            mCurrentBox.setCurrent(current);
                            invalidate();
                        } else if(pointerId == 1) {
                            mCurrentBox.setAngle(angleBetweenLunes(
                                    mCurrentBox.getCurrent().x,
                                    mCurrentBox.getCurrent().y,
                                    start.x,
                                    start.y,
                                    mCurrentBox.getCurrent().x,
                                    mCurrentBox.getCurrent().y,
                                    current.x,
                                    current.y));
                            Log.d(TAG, "Angle = " + mCurrentBox.getAngle());
                            invalidate();
                        }
                    }
                    break;
            }

        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Fill the background
        canvas.drawPaint(mBackgroundPaint);

        for(Box box: mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.rotate(box.getAngle());
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.rotate(-box.getAngle());
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(ARG_SUPER_STATE, super.onSaveInstanceState());
        state.putParcelableArrayList(ARG_BOXEN, (ArrayList<? extends Parcelable>) mBoxen);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle currentState = (Bundle) state;
            mBoxen = currentState.getParcelableArrayList(ARG_BOXEN);
            state = currentState.getParcelable(ARG_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }
}
