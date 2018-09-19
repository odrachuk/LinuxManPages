/*******************************************************************************
 * Created by o.drachuk on 27/05/2014. 
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
package com.softsandr.man.ui.page.search;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.softsandr.man.R;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.ui.ManBrowserActivity;
import com.softsandr.man.util.PrefHistoryUtils;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class used for...
 */
public class ManSearchFragment extends ListFragment {
    private static final String LOG_TAG = ManSearchFragment.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);
    private static final String SEARCH_RESULTS = ManSearchFragment.class.getName() + ".SEARCH_RESULTS";
    private ArrayList<ManTableRecord> searchResults;

    /**
     * Create a new instance of ManBrowserActivity, initialized to
     * show the file name.
     */
    public static ManSearchFragment newInstance(ArrayList<ManTableRecord> searchResults) {
        LOGGER.info("newInstance");
        ManSearchFragment f = new ManSearchFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(SEARCH_RESULTS, searchResults);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResults = getArguments().getParcelableArrayList(SEARCH_RESULTS);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setTitle(getString(R.string.search_results));
        setEmptyText(getString(R.string.no_search_results));
        setListAdapter(new ManSearchAdapter(getActivity(), searchResults));
        // Setup divider
        getListView().setDivider(getResources().getDrawable(android.R.color.transparent));
        getListView().setDividerHeight(25);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<ManTableRecord> adapter = (ArrayAdapter<ManTableRecord>) getListAdapter();
        selectItem(adapter.getItem(position));
    }

    private void selectItem(ManTableRecord record) {
        // add command to history
        PrefHistoryUtils.addHistoryCommand(getActivity(), record);
        // show command in browser
        Intent startIntent = new Intent(getActivity(), ManBrowserActivity.class);
        startIntent.putExtra(ManBrowserActivity.MAN_TABLE_RECORD, record);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(startIntent);
    }

    @Override
    public void onDestroyView() {
        LOGGER.info("onDestroyView");
        super.onDestroyView();
    }
}
