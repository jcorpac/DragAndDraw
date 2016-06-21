package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jeff on 6/16/2016.
 */
public class Box implements Parcelable{
    private PointF mOrigin;
    private PointF mCurrent;
    private float angle = 0;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(mOrigin);
        parcel.writeValue(mCurrent);
    }
}
