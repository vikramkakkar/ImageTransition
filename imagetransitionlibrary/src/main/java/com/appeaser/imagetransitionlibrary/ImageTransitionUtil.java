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

import android.support.v4.app.SharedElementCallback;
import android.view.View;

import java.util.List;

public class ImageTransitionUtil {

    // Start & end values that are captured in `ImageTransition` are set here.
    //
    // This is the default implementation of `SharedElementCallback`
    // that assumes that the `TransitionImageView` in the first `Activity` is
    // perfectly rounded, and that the `TransitionImageView` in the
    // second `Activity` is not rounded at all.
    //
    // If you are using different values,
    // use `prepareSharedElementCallbackFor(float, float)`, or provide your own
    // implementation of `SharedElementCallback`.
    public static SharedElementCallback DEFAULT_SHARED_ELEMENT_CALLBACK
            = new SharedElementCallback() {

        @Override
        public void onSharedElementStart(List<String> sharedElementNames,
                                         List<View> sharedElements, List<View> sharedElementSnapshots) {
            super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);

            // find TransitionImageView in the list of shared elements
            for (View sharedElement: sharedElements) {
                if (sharedElement instanceof TransitionImageView) {
                    // this value is retrieved in `ImageTransition#captureStartValues(TransitionValues)`
                    // when entering the second `Activity`.

                    // while exiting from from second `Activity`, this value is retrieved
                    // in `ImageTransition#captureEndValues(TransitionValues)`.
                    ((TransitionImageView)sharedElement)
                            .setRoundingProgress(TransitionImageView.RoundingProgress.MAX.progressValue());
                }
            }
        }

        @Override
        public void onSharedElementEnd(List<String> sharedElementNames,
                                       List<View> sharedElements, List<View> sharedElementSnapshots) {
            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

            // find TransitionImageView in the list of shared elements
            for (View sharedElement: sharedElements) {
                if (sharedElement instanceof TransitionImageView) {
                    TransitionImageView tiv = (TransitionImageView) sharedElement;

                    // this value is retrieved in `ImageTransition#captureEndValues(TransitionValues)`
                    // when entering the second `Activity`.

                    // while exiting from from second `Activity`, this value is retrieved
                    // in `ImageTransition#captureStartValues(TransitionValues)`.

                    // Note that we only set this value when entering (checked by the if-condition below)
                    // the second `Activity`.
                    // In case the user exits the second `Activity` before the transition completes,
                    // we would like to transition from the current amount of rounding, rather
                    // than the MIN value.
                    if (tiv.getRoundingProgress() == TransitionImageView.RoundingProgress.MAX.progressValue()) {
                        tiv.setRoundingProgress(TransitionImageView.RoundingProgress.MIN.progressValue());
                    }
                }
            }
        }
    };

    /**
     * Returns a {@link SharedElementCallback} that works with the given `rounding` amounts.
     * A `startValue` of `1f` (or RoundingProgress.MAX.progressValue())
     * & an `endValue` of `0f` (or RoundingProgress.MIN.progressValue())
     * will give you same functionality as {@link ImageTransitionUtil#DEFAULT_SHARED_ELEMENT_CALLBACK}.
     *
     * @param startValue rounding applied to TransitionImageView in the first Activity
     * @param endValue rounding applied to TransitionImageView in the second Activity
     * @return SharedElementCallback that works with the given `rounding` amounts
     */
    public static SharedElementCallback prepareSharedElementCallbackFor(final float startValue, final float endValue) {
        return new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);

                // find TransitionImageView in the list of shared elements
                for (View sharedElement: sharedElements) {
                    if (sharedElement instanceof TransitionImageView) {
                        // this value is retrieved in `ImageTransition#captureStartValues(TransitionValues)`
                        // when entering the second `Activity`.

                        // while exiting from from second `Activity`, this value is retrieved
                        // in `ImageTransition#captureEndValues(TransitionValues)`.
                        ((TransitionImageView)sharedElement)
                                .setRoundingProgress(startValue);
                    }
                }
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                // find TransitionImageView in the list of shared elements
                for (View sharedElement: sharedElements) {
                    if (sharedElement instanceof TransitionImageView) {
                        TransitionImageView tiv = (TransitionImageView) sharedElement;

                        // this value is retrieved in `ImageTransition#captureEndValues(TransitionValues)`
                        // when entering the second `Activity`.

                        // while exiting from from second `Activity`, this value is retrieved
                        // in `ImageTransition#captureStartValues(TransitionValues)`.

                        // Note that we only set this value when entering (checked by the if-condition below)
                        // the second `Activity`.
                        // In case the user exits the second `Activity` before the transition completes,
                        // we would like to transition from the current amount of rounding, rather
                        // than the MIN value.
                        if (tiv.getRoundingProgress() == startValue) {
                            tiv.setRoundingProgress(endValue);
                        }
                    }
                }
            }
        };
    }
}
