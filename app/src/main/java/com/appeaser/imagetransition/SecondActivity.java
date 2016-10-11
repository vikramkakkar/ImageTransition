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

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.appeaser.imagetransitionlibrary.ImageTransitionUtil;
import com.appeaser.imagetransitionlibrary.TransitionImageView;
import com.squareup.picasso.Picasso;

/**
 * This sample is based on:
 *
 * http://stackoverflow.com/q/39749404
 *
 * Sample code was provided by Simon:
 *
 * https://github.com/Winghin2517/TransitionTest
 *
 * Second Activity
 */
public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    TransitionImageView backdrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        backdrop = (TransitionImageView) findViewById(R.id.picture);
        Picasso.with(this).load(R.drawable.sample).into(backdrop);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // SharedElementCallback needs be set in the second Activity.
        // See ImageTrainsitionUtil for more info.
        setEnterSharedElementCallback(ImageTransitionUtil.DEFAULT_SHARED_ELEMENT_CALLBACK);

        // Note: The above statement is equivalent to calling:
        // setEnterSharedElementCallback(ImageTransitionUtil.prepareSharedElementCallbackFor(1f, 0f));
        // or:
        // setEnterSharedElementCallback(
        //      ImageTransitionUtil.prepareSharedElementCallbackFor(
        //          TransitionImageView.RoundingProgress.MAX.progressValue(),
        //          TransitionImageView.RoundingProgress.MIN.progressValue()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }
}
