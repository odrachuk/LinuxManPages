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
package com.softsandr.man.ui.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TabHost;
import com.google.android.gms.ads.AdView;
import com.softsandr.man.R;
import com.softsandr.man.app.AppContext;
import com.softsandr.man.db.ManSQLiteOpenHelper;
import com.softsandr.man.db.ResponseListener;
import com.softsandr.man.db.utils.ManSQLiteUtil;
import com.softsandr.man.db.table.ManTable;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.ui.ManMainActivity;
import com.softsandr.man.ui.page.section.ManSectionInfo;
import com.softsandr.man.util.AdMobUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * This class used for...
 */
public class ManDataFragment extends Fragment {
    private static final String LOG_TAG = ManDataFragment.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);

    public static final String SEARCH_TEXT_CHANGED_ACTION = ManDataFragment.class.getCanonicalName() + ".SEARCH_TEXT_CHANGED_ACTION";
    public static final String SEARCH_TEXT_EXTRA = ManDataFragment.class.getCanonicalName() + ".SEARCH_TEXT_EXTRA";
    public static final String SEARCH_TEXT_SAVED_INSTANCE = ManDataFragment.class.getCanonicalName() + ".SEARCH_TEXT_SAVED_INSTANCE";

    private FragmentTabHost tabHost;
    private EditText searchField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.man_section_layout, container, false);
        initSearchField(view);
        initTabHost(view);
        AdMobUtils.initAdView((AdView) view.findViewById(R.id.adViewOnMain));
        return view;
    }

    private void initSearchField(View view) {
        searchField = (EditText) view.findViewById(R.id.section_search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignored
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignored
            }

            @Override
            public void afterTextChanged(Editable s) {
                Intent intent = new Intent();
                intent.setAction(SEARCH_TEXT_CHANGED_ACTION);
                intent.putExtra(SEARCH_TEXT_EXTRA, searchField.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });
    }

    private void initTabHost(View view) {
        tabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        for (final ManSectionInfo sectionInfo : ManSectionInfo.values()) {
            TabHost.TabSpec tabSpec = tabHost.
                    newTabSpec(sectionInfo.getSectionId()).
                    setIndicator(sectionInfo.getSectionSymbol());
            tabHost.addTab(tabSpec, sectionInfo.getSectionClass(), null);

            // hide empty tab
            final ResponseListener<ManTableRecord> dbListener = new ResponseListener<ManTableRecord>() {
                @Override
                public void onDbResponse(List<ManTableRecord> responseData) {
                    if (responseData == null || responseData.isEmpty()) {
                        tabHost.getTabWidget().getChildAt(sectionInfo.getIndex()).setVisibility(View.GONE);
                    }

                    // make selected first tab with data
                    if (sectionInfo == ManSectionInfo.SECTION_Z) {
                        for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
                            if (tabHost.getTabWidget().getChildAt(i).getVisibility() == View.VISIBLE) {
                                tabHost.setCurrentTab(i);
                                break;
                            }
                        }
                    }
                }
            };
            final ManSQLiteUtil commandDataSource = new ManSQLiteUtil((ManSQLiteOpenHelper) getActivity().getApplication().
                    getSystemService(AppContext.MAN_DATABASE_HELPER_SERVICE));
            commandDataSource.getRecordsBySymbolAsync(dbListener,
                    ManTable.getBySection(((ManMainActivity) getActivity()).getSelectedSection()),
                    sectionInfo.getSectionSymbol().toLowerCase());
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                searchField.setText("");
                hideSoftKeyboard();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        LOGGER.info("hideSoftKeyboard");
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchField.setText("");
        tabHost = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (searchField != null) {
            outState.putString(SEARCH_TEXT_SAVED_INSTANCE, searchField.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // send search broad cast if present search text - after screen rotations for example
        if (savedInstanceState != null) {
            String savedSearchText = savedInstanceState.getString(SEARCH_TEXT_SAVED_INSTANCE);
            if (!savedInstanceState.isEmpty()) {
                Intent intent = new Intent();
                intent.setAction(SEARCH_TEXT_CHANGED_ACTION);
                intent.putExtra(SEARCH_TEXT_EXTRA, savedSearchText);
                getActivity().sendStickyBroadcast(intent);
            }
        }
    }
}
