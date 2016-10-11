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
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionValues;
import android.view.ViewGroup;

/**
 * A {@link android.support.transition.Transition} based
 * on {@link android.support.transition.ChangeBounds}
 * that provides animation support between a circular
 * and rectangular ImageView (implemented as {@link TransitionImageView}).
 * Note that this can only be used with components from
 * package `android.support.transition`.
 * Functionality is provided by {@link ImageTransitionCompatHelper}.
 *
 * Under development
 */
class ImageTransitionCompat extends ChangeBounds {

    @Override
    public String[] getTransitionProperties() {
        // pass parent's properties...
        return ImageTransitionCompatHelper.getTransitionProperties(super.getTransitionProperties());
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        ImageTransitionCompatHelper.captureValues(transitionValues.view, transitionValues.values);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        ImageTransitionCompatHelper.captureValues(transitionValues.view, transitionValues.values);
    }

    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @NonNull TransitionValues startValues,
                                   @NonNull TransitionValues endValues) {
        // pass parent's Animator
        return ImageTransitionCompatHelper.createAnimator(super.createAnimator(sceneRoot, startValues, endValues),
                sceneRoot, endValues.view, startValues.values, endValues.values);
    }
}
