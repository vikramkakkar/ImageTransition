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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
 * Fragment inside first Activity
 */
public class MainFragment extends Fragment {

    private TransitionImageView dot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container,false);

        dot = (TransitionImageView) view.findViewById(R.id.image_circle);
        FrameLayout flParent = (FrameLayout) view.findViewById(R.id.fl_parent);

        Picasso.with(getContext()).load(R.drawable.sample).into(dot);

        // Launch second Activity
        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SecondActivity.class);
                String transitionName = getString(R.string.blue_name);
                ActivityOptionsCompat transitionActivityOptions
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), dot, transitionName);

                startActivity(i, transitionActivityOptions.toBundle());
            }
        });

        // Provides dragging support for demo purposes
        dot.setOnTouchListener(new ImageViewTouchListener(dot, flParent));
        return view;
    }
}
