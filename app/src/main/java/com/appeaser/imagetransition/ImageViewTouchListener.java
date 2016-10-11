/*
 * Copyright 2016 Vikram Kakkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appeaser.imagetransition;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Provides dragging support for demo purposes.
 */
class ImageViewTouchListener implements View.OnTouchListener {

    private View mView;
    private ViewGroup mParent;

    private float mDownX;
    private float mDownY;

    private float mDraggingStartX;
    private float mDraggingStartY;
    private int mStartingMarginLeft;
    private int mStartingMarginTop;

    private float mTouchSlop;
    private boolean mDragging;

    private int mMaxMarginLeft = -1;
    private int mMaxMarginTop = -1;

    private Runnable mClickRunnable = new Runnable() {
        @Override
        public void run() {
            mView.performClick();
        }
    };

    ImageViewTouchListener(View view, ViewGroup parent) {
        mView = view;
        mParent = parent;
        mTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float evX = event.getRawX();
        float evY = event.getRawY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = evX;
                mDownY = evY;
                break;
            case MotionEvent.ACTION_MOVE:
                float distMoved = (float)Math.sqrt((evX - mDownX)*(evX - mDownX) + (evY - mDownY)*(evY - mDownY));

                if (mDragging) {
                    moveViewBy(evX - mDraggingStartX, evY - mDraggingStartY);
                } else if (distMoved > mTouchSlop) {
                    mDraggingStartX = evX;
                    mDraggingStartY = evY;

                    mStartingMarginLeft = ((ViewGroup.MarginLayoutParams)mView.getLayoutParams()).leftMargin;
                    mStartingMarginTop = ((ViewGroup.MarginLayoutParams)mView.getLayoutParams()).topMargin;

                    mDragging = true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                break;
            case MotionEvent.ACTION_UP:
                if (!mDragging) {
                    if (!mView.post(mClickRunnable)) {
                        mView.performClick();
                    }
                }

                mDragging = false;
                break;
        }

        return true;
    }

    private void moveViewBy(float dx, float dy) {
        if (mMaxMarginLeft == -1) {
            mMaxMarginLeft = mParent.getWidth() - mView.getWidth() - mView.getPaddingLeft() - mView.getPaddingRight();
            mMaxMarginTop = mParent.getHeight() - mView.getHeight() - mView.getPaddingTop() - mView.getPaddingBottom();
        }

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();

        int newLeftMargin = constrain((int)(mStartingMarginLeft + dx), 0, mMaxMarginLeft);
        int newTopMargin = constrain((int)(mStartingMarginTop + dy), 0, mMaxMarginTop);

        params.leftMargin = newLeftMargin;
        params.topMargin = newTopMargin;
        mView.setLayoutParams(params);
    }

    private int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
