/*
 * Copyright 2014 - 2016 Henning Dodenhof
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

package com.appeaser.imagetransitionlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.widget.ImageView;

/**
 * This is an edited version of 'Henning Dodenhof's' library project
 * that can be found here: https://github.com/hdodenhof/CircleImageView.
 *
 * Many thanks to 'Henning Dodenhof' for open-sourcing their work.
 *
 * Modifications:
 *
 * For transition support, a 'rounding' variable has been
 * introduced. This value is bounded in [0f,1f] and provides
 * varying degrees of 'rounding'. At '0f', no rounding is applied.
 * A value of `1f` provides perfect rounding -> if (w == h) ==> this view
 * is circular.
 *
 * Several features have been removed:
 *
 *  - No support for drawing borders
 *  - No support of fill color
 *  - No 'disable' option - use {@link TransitionImageView#setRoundingProgress(float)}
 *    with {@link RoundingProgress#MIN#progressValue()} instead
 */
public class TransitionImageView extends ImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    private final RectF mDrawableRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private ColorFilter mColorFilter;

    private boolean mReady;
    private boolean mSetupPending;

    // Changes

    // Common use cases
    public enum RoundingProgress {

        // No rounding
        MIN(0f),

        // Perfect rounding
        MAX(1f);

        RoundingProgress(float progressValue) {
            mProgressValue = progressValue;
        }

        /**
         * Rounding value for this enum.
         *
         * @return the assigned rounding value
         */
        public float progressValue() {
            return mProgressValue;
        }

        // Rounding value for this enum.
        private float mProgressValue;
    }

    // Amount of rounding to apply
    private float mRoundingProgress;

    // Used while drawing to the canvas
    private float mRoundedRadius;

    // Exposed property that is animated by ObjectAnimator
    public static final Property<View, Float> ROUNDING_PROGRESS_PROPERTY
            = new Property<View, Float>(Float.class, "roundingProgress") {
        @Override
        public Float get(View object) {
            return null;
        }

        @Override
        public void set(View object, Float value) {
            if (object instanceof TransitionImageView) {
                ((TransitionImageView)object).setRoundingProgress(value);
            }
        }
    };

    public TransitionImageView(Context context) {
        super(context);
        mRoundingProgress = RoundingProgress.MAX.progressValue();
        init();
    }

    public TransitionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransitionImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TransitionImageView, defStyle, 0);

        mRoundingProgress = constrain(a.getFloat(R.styleable.TransitionImageView_tiv_rounding,
                RoundingProgress.MAX.progressValue()), RoundingProgress.MIN.progressValue(),
                RoundingProgress.MAX.progressValue());

        a.recycle();

        init();
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }

        canvas.drawRoundRect(mDrawableRect, mRoundedRadius, mRoundedRadius, mBitmapPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        applyColorFilter();
        invalidate();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    private void applyColorFilter() {
        if (mBitmapPaint != null) {
            mBitmapPaint.setColorFilter(mColorFilter);
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                    return null;
                }

                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeBitmap() {
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mRoundedRadius = getWidth()/2f * mRoundingProgress;

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mDrawableRect.set(calculateBounds());

        applyColorFilter();
        updateShaderMatrix();
        invalidate();
    }

    private RectF calculateBounds() {
        int availableWidth  = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLengthHor = (int)(availableWidth
                + mRoundingProgress * (Math.min(availableWidth, availableHeight) - availableWidth));
        int sideLengthVer = (int)(availableHeight
                + mRoundingProgress * (Math.min(availableWidth, availableHeight) - availableHeight));

        float left = getPaddingLeft() + (availableWidth - sideLengthHor) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLengthVer) / 2f;

        return new RectF(left, top, left + sideLengthHor, top + sideLengthVer);
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    /********************* Changes *********************/

    /**
     * Governs the rounding aspect of this ImageView.
     * Value of `0f` signifies no rounding at all.
     * Value of `1f` signifies perfectly rounded corners.
     * Values in-between are interpolated to provide the
     * required rounding.
     *
     * @param roundingProgress rounding value to apply; constrained in range [0f,1f]
     */
    public void setRoundingProgress(float roundingProgress) {
        // constrain value in range [0f,1f]
        roundingProgress = constrain(roundingProgress, RoundingProgress.MIN.progressValue(),
                RoundingProgress.MAX.progressValue());

        if (mRoundingProgress == roundingProgress) {
            // no change
            return;
        }

        // apply changes
        mRoundingProgress = roundingProgress;
        setup();
    }

    /**
     * Returns the current amount of rounding.
     *
     * @return current rounding amount
     */
    public float getRoundingProgress() {
        return mRoundingProgress;
    }

    /**
     * Constrains the given `amount` within `low` & `high`.
     *
     * @param amount the amount to work with
     * @param low lower bound for `amount`
     * @param high upper bound for `amount`
     * @return constrained `amount`
     */
    private float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
