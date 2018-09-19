/*******************************************************************************
 * Created by o.drachuk on 14/06/2014.
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
package com.softsandr.man.ui.page.section;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.softsandr.man.app.AppContext;
import com.softsandr.man.db.ManSQLiteOpenHelper;
import com.softsandr.man.db.ResponseListener;
import com.softsandr.man.db.table.ManTable;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.db.utils.ManSQLiteUtil;
import com.softsandr.man.ui.ManBrowserActivity;
import com.softsandr.man.ui.ManMainActivity;
import com.softsandr.man.ui.page.ManDataFragment;
import com.softsandr.man.util.PrefHistoryUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This class represent abstract Fragment on that we show data content is started by specific symbol
 */
public abstract class ManSymbolFragment extends ListFragment implements ResponseListener<ManTableRecord> {
    private static final String LOG_TAG = ManSymbolFragment.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);
    protected ManSectionInfo manSectionInfo;
    protected ManSectionAdapter adapter;
    private boolean uiReady;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ManDataFragment.SEARCH_TEXT_CHANGED_ACTION)) {
                String startingWith = intent.getStringExtra(ManDataFragment.SEARCH_TEXT_EXTRA);
                if (startingWith != null) {
                    if (adapter != null) {
                        adapter.getFilter().filter(startingWith);
                    } else {
                        // Sticky broadcasting situation
                        try {
                            getActivity().removeStickyBroadcast(intent);
                            new WaitingUiTask().execute(startingWith);
                        } catch (Exception e) {
                            LOGGER.severe(e.getMessage());
                        }
                    }
                }
            }
        }
    };

    /**
     * All children should determining specific symbol
     */
    abstract void determineSymbol();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        determineSymbol();
        ManSQLiteUtil commandDataSource = new ManSQLiteUtil((ManSQLiteOpenHelper) getActivity().getApplication().
                getSystemService(AppContext.MAN_DATABASE_HELPER_SERVICE));
        commandDataSource.getRecordsBySymbolAsync(this,
                ManTable.getBySection(((ManMainActivity) getActivity()).getSelectedSection()),
                manSectionInfo.getSectionSymbol().toLowerCase());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("No commands starting with \'" + manSectionInfo.getSectionSymbol() + "\'");
        // Setup divider
        getListView().setDivider(getResources().getDrawable(android.R.color.transparent));
        getListView().setDividerHeight(25);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<ManTableRecord> adapter = (ArrayAdapter<ManTableRecord>) getListAdapter();
//        Toast.makeText(getActivity(), tc.toString(), Toast.LENGTH_SHORT).show();
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
    public void onDbResponse(List<ManTableRecord> responseData) {
        if (responseData.isEmpty()) {
            LOGGER.severe("Data is not found in db");
        }
        adapter = new ManSectionAdapter(getActivity(), responseData);
        setListAdapter(adapter);
        uiReady = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(ManDataFragment.SEARCH_TEXT_CHANGED_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        uiReady = false;
    }

    /**
     * Used for waiting ready ui state if sticky broadcast received but ui not instantiated yet
     */
    private final class WaitingUiTask extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object[] params) {
            String savedText = (String) params[0];
            while (!uiReady) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
            return savedText;
        }

        @Override
        protected void onPostExecute(String savedText) {
            adapter.getFilter().filter(savedText);
        }
    }
}
