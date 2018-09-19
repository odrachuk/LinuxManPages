/*******************************************************************************
 * Created by o.drachuk on 19/01/2014. 
 *
 * Copyright Oleksandr Drachuk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.softsandr.man.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.softsandr.man.R;
import com.softsandr.man.util.PrefFavoritesUtils;
import com.softsandr.man.util.PrefHistoryUtils;
import com.softsandr.man.util.ThemeUtils;

import java.util.logging.Logger;

/**
 * This class used for display application setting screen
 */

public class ManPreferenceActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = ManPreferenceActivity.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);

    public static final String THEME_CHANGED_EVENT = LOG_TAG + ".THEME_CHANGED_EVENT";

    private final BroadcastReceiver activityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(THEME_CHANGED_EVENT)) {
                ThemeUtils.changeTheme(ManPreferenceActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        // Display home button on action bar
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.action_app_settings));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ManPreferenceActivity.THEME_CHANGED_EVENT);
        registerReceiver(activityBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        unregisterReceiver(activityBroadcastReceiver);
    }

    @Override
    public void finish() {
        LOGGER.info("finish");
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        LOGGER.info("onSharedPreferenceChanged: Key " + key);
    }

    /**
     * The extends of {@link android.preference.PreferenceFragment} used as maine Settings screen
     */
    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.application_preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            // Version preference configuration
            Preference versionPref = findPreference(getString(R.string.pref_app_version_key));
            if (versionPref != null) {
                String appVersion = "";
                // read app version
                PackageInfo pInfo;
                try {
                    PackageManager packageManager = getActivity().getPackageManager();
                    if (packageManager != null) {
                        pInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
                        appVersion = pInfo.versionName;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // ignored
                }
                versionPref.setSummary(appVersion);
            }
            // Theme preference configuration
            Preference themePref = findPreference(getString(R.string.pref_app_select_theme_key));
            if (themePref != null) {
                themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        LOGGER.info("Selected theme: " + newValue);


                        getActivity().sendBroadcast(new Intent(THEME_CHANGED_EVENT));
                        return true;
                    }
                });
            }
            // Clear history configuration
            Preference clearHistoryPref = findPreference(getString(R.string.pref_app_clear_history_key));
            if (clearHistoryPref != null) {
                clearHistoryPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PrefHistoryUtils.clearHistoryCommands(getActivity());
                        return true;
                    }
                });
            }
            // Clear favorites configuration
            Preference clearFavoritesPref = findPreference(getString(R.string.pref_app_clear_favorite_key));
            if (clearFavoritesPref != null) {
                clearFavoritesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PrefFavoritesUtils.clearFavorites(getActivity());
                        return true;
                    }
                });
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            super.onPreferenceTreeClick(preferenceScreen, preference);
            // If the user has clicked on a preference screen, set up the action bar
            if (preference instanceof PreferenceScreen) {
                initializeActionBar((PreferenceScreen) preference);
            } else {
                /*if (preference.getKey().equals(getString(R.string.pref_clear_locations_key))) {
                    // send clear_man history broadcast
                    getActivity().sendBroadcast(new Intent(TerminalActivityImpl.CLEAR_HISTORY_INTENT));
                } else if (preference.getKey().equals(getString(R.string.pref_back_def_colorize_key))) {
                    // reset to default colorize settings
                    PreferenceController.resetToDefaultColorize(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
                    getActivity().sendBroadcast(new Intent(TerminalActivityImpl.SETTING_CHANGED_INTENT));
                }*/
            }
            return false;
        }

        /**
         * Sets up the action bar for an {@link android.preference.PreferenceScreen}
         */
        public void initializeActionBar(PreferenceScreen preferenceScreen) {
            final Dialog dialog = preferenceScreen.getDialog();

            if (dialog != null) {
                // Init the action bar
                dialog.getActionBar().setDisplayHomeAsUpEnabled(true);
                // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
                // events instead of passing to the activity
                // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
                View homeBtn = dialog.findViewById(android.R.id.home);
                if (homeBtn != null) {
                    View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    };
                    // Prepare yourselves for some programming hack
                    ViewParent homeBtnContainer = homeBtn.getParent();
                    // The home button is an ImageView inside a FrameLayout
                    if (homeBtnContainer instanceof FrameLayout) {
                        ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();
                        if (containerParent instanceof LinearLayout) {
                            // This view also contains the title text, set the whole view as clickable
                            ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
                        } else {
                            // Just set it on the home button
                            ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
                        }
                    } else {
                        // The 'If all else fails' default case_man
                        homeBtn.setOnClickListener(dismissDialogClickListener);
                    }
                }
            }
        }
    }
}
