/*******************************************************************************
 * Created by o.drachuk on 27/05/2014.
 * <p/>
 * Copyright Oleksandr Drachuk.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.softsandr.man.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ShareActionProvider;
import com.google.android.gms.ads.AdView;
import com.softsandr.man.R;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.util.AdMobUtils;
import com.softsandr.man.util.ThemeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * This class used for...
 */
public class ManBrowserActivity extends Activity {
    public static final String MAN_TABLE_RECORD = ManBrowserActivity.class.getName() + "MAN_TABLE_RECORD";

    private static final String LOG_TAG = ManBrowserActivity.class.getName();
    private static final String SAVED_MAN_TABLE_RECORD = LOG_TAG + ".SAVED_MAN_TABLE_RECORD";
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);

    private final BroadcastReceiver activityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ManPreferenceActivity.THEME_CHANGED_EVENT)) {
                ThemeUtils.changeTheme(ManBrowserActivity.this);
            }
        }
    };

    private ManTableRecord manTableRecord;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOGGER.info("onCreate");
        ThemeUtils.onActivityCreateSetTheme(this);
        if (savedInstanceState != null) {
            manTableRecord = savedInstanceState.getParcelable(SAVED_MAN_TABLE_RECORD);
        } else {
            if (getIntent().getExtras() != null) {
                manTableRecord = getIntent().getExtras().getParcelable(MAN_TABLE_RECORD);
            } else {
                LOGGER.info("Information about command was lost when theme was change");
                finish();
            }
        }
        setContentView(R.layout.man_browser_layout);
        WebView webView = (WebView) findViewById(R.id.man_web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        if (manTableRecord != null) {
            fileName = "man" + manTableRecord.getSection() + "/" + manTableRecord.getFile();
            webView.loadUrl("file:///android_asset/" + fileName);
            // Display home button on action bar
            initActionBar(manTableRecord.getName());
        } else {
            // can be some stub html loaded
            fileName = "";
            // Display home button on action bar
            initActionBar(getString(R.string.app_name));
        }
        AdMobUtils.initAdView((AdView) findViewById(R.id.adViewOnBrowser));
    }

//    private void applyActualTheme(WebView view) {
//        String[] themes = getResources().getStringArray(R.array.theme_pref_list_values);
//        String themeFromPref = PrefUtils.loadAppTheme(this, PreferenceManager.getDefaultSharedPreferences(this));
//        if (themeFromPref.equals(themes[0])) {
//            // todo invert color scheme of WebView
//        }
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LOGGER.info("onSaveInstanceState");
        outState.putParcelable(SAVED_MAN_TABLE_RECORD, manTableRecord);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem searchMenu = menu.findItem(R.id.action_command_search);
        if (searchMenu != null) {
            searchMenu.setVisible(false);
        }

        MenuItem shareMenuNew = menu.findItem(R.id.action_share);
        if (shareMenuNew != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                try {
                    ShareActionProvider shareProvider = (android.widget.ShareActionProvider) shareMenuNew.getActionProvider();
                    shareProvider.setShareIntent(prepareShareIntent());
                    shareMenuNew.setVisible(true);
                } catch (Exception e) {
                    LOGGER.throwing(LOG_TAG, "setShareIntent", e);
                }
            } else {
                shareMenuNew.setVisible(false);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    private Intent prepareShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/html");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, manTableRecord.getName() + " command description");
        shareIntent.putExtra(Intent.EXTRA_TEXT, readAssetsFile());
        return shareIntent;
    }

    private String readAssetsFile() {
        AssetManager assetManager = getAssets();
        InputStream input;
        StringBuilder text = new StringBuilder();
        try {
            input = assetManager.open(fileName);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            text.append(new String(buffer));
        } catch (IOException e) {
            LOGGER.throwing(LOG_TAG, "readAssetsFile", e);
        }
        return text.toString();
    }

    private void initActionBar(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, ManPreferenceActivity.class));
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ManPreferenceActivity.THEME_CHANGED_EVENT);
        registerReceiver(activityBroadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        LOGGER.info("onDestroy");
        try {
            unregisterReceiver(activityBroadcastReceiver);
        } catch (Exception e) {
            // ingored for now
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        LOGGER.info("finish");
        super.finish();
        overridePendingTransition(0, 0);
    }
}
