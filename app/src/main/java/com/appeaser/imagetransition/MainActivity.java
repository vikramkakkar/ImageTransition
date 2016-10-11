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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * This sample is based on:
 *
 * http://stackoverflow.com/q/39749404
 *
 * Sample code was provided by Simon:
 *
 * https://github.com/Winghin2517/TransitionTest
 *
 * First Activity
 */
public class MainActivity extends AppCompatActivity {

    FrameLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fl = (FrameLayout) findViewById(R.id.fragment_container);

        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_rate_this_app) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.appeaser.imagetransition")));
            } catch (ActivityNotFoundException anfe1) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://market.android.com/details?id=com.appeaser.imagetransition")));
                } catch (ActivityNotFoundException anfe2) {
                    Toast.makeText(this, "You need a browser app to view this link",
                            Toast.LENGTH_LONG).show();
                }
            }
            return true;
        } else if (id == R.id.action_github_link) {
            String data = "https://github.com/vikramkakkar/ImageTransition";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data)));
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(this, "You need a browser app to view this link",
                        Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.action_about_this_app) {
            startActivity(new Intent(this, AboutThisApp.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
