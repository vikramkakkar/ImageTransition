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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A {@link android.transition.Transition} based on {@link ChangeBounds}
 * that provides animation support between a circular
 * and rectangular ImageView (implemented as {@link TransitionImageView})
 * residing in two different activities.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ImageTransition extends ChangeBounds {

    private static final String PROPNAME_ROUNDING_PROGRESS = "itl:changeBounds:roundingProgress";

    public ImageTransition() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        // fetch parent's properties...
        String[] parentTransitionProperties = super.getTransitionProperties();

        // ...and tack our own at the end
        String[] transitionProperties = Arrays.copyOf(parentTransitionProperties, parentTransitionProperties.length + 1);
        transitionProperties[transitionProperties.length - 1] = PROPNAME_ROUNDING_PROGRESS;

        return transitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;

        if (view instanceof TransitionImageView) {
            // Values were set in the SharedElementCallback.
            // See ImageTransitionUtils for more info.
            values.values.put(PROPNAME_ROUNDING_PROGRESS,
                    ((TransitionImageView)view).getRoundingProgress());
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        // get the Animator that ChangeBounds prepared
        Animator parentAnimator = super.createAnimator(sceneRoot, startValues, endValues);

        if (parentAnimator == null) {
            return null;
        }

        // retrieve start & end rounding values
        float startRoundingProgress = (float) startValues.values.get(PROPNAME_ROUNDING_PROGRESS);
        float endRoundingProgress = (float) endValues.values.get(PROPNAME_ROUNDING_PROGRESS);

        // our animator
        final ObjectAnimator roundingProgressAnimator = ObjectAnimator.ofFloat(endValues.view,
                TransitionImageView.ROUNDING_PROGRESS_PROPERTY,
                startRoundingProgress, endRoundingProgress);

        if (parentAnimator instanceof AnimatorSet) {
            // Get all child Animators from the parent's set
            ArrayList<Animator> parentAnimators = ((AnimatorSet)parentAnimator).getChildAnimations();

            // add our own Animator
            parentAnimators.add(roundingProgressAnimator);

            // Create a new AnimatorSet and play all Animators together.
            // ChangeBounds uses AnimatorSet#playTogether(...) - so, the
            // following should be OK.
            parentAnimator = new AnimatorSet();
            ((AnimatorSet)parentAnimator).playTogether(parentAnimators);
        } else if (parentAnimator != null) {
            // parentAnimator is not an AnimatorSet
            AnimatorSet set = new AnimatorSet();
            set.playTogether(parentAnimator, roundingProgressAnimator);
            return set;
        }

        return parentAnimator;
    }
}
