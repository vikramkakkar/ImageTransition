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
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Provides functionality for {@link ImageTransition}
 * and {@link ImageTransitionCompat}.
 */
class ImageTransitionCompatHelper {

    private static final String PROPNAME_ROUNDING_PROGRESS = "itl:changeBounds:roundingProgress";

    static String[] getTransitionProperties(String[] parentTransitionProperties) {
        if (parentTransitionProperties == null || parentTransitionProperties.length == 0) {
            return new String[] {PROPNAME_ROUNDING_PROGRESS};
        }

        // ...and tack our own at the end
        String[] transitionProperties = Arrays.copyOf(parentTransitionProperties, parentTransitionProperties.length + 1);
        transitionProperties[transitionProperties.length - 1] = PROPNAME_ROUNDING_PROGRESS;

        return transitionProperties;
    }

    static void captureValues(View view, Map<String, Object> values) {
        if (view instanceof TransitionImageView) {
            // Values were set in the SharedElementCallback.
            // See ImageTransitionUtils for more info.
            values.put(PROPNAME_ROUNDING_PROGRESS,
                    ((TransitionImageView)view).getRoundingProgress());
        }
    }

    static Animator createAnimator(Animator parentAnimator, ViewGroup sceneRoot,
                                           View endValuesView,
                                           Map<String, Object> startValues,
                                           Map<String, Object> endValues) {
        if (parentAnimator == null) {
            return null;
        }

        // retrieve start & end rounding values
        float startRoundingProgress = (float) startValues.get(PROPNAME_ROUNDING_PROGRESS);
        float endRoundingProgress = (float) endValues.get(PROPNAME_ROUNDING_PROGRESS);

        // our animator
        final ObjectAnimator roundingProgressAnimator = ObjectAnimator.ofFloat(endValuesView,
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
        } else {
            // parentAnimator is not an AnimatorSet
            AnimatorSet set = new AnimatorSet();
            set.playTogether(parentAnimator, roundingProgressAnimator);
            return set;
        }

        return parentAnimator;
    }
}
