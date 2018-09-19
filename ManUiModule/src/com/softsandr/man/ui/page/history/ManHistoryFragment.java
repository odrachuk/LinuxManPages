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
package com.softsandr.man.ui.page.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.softsandr.man.R;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.ui.ManBrowserActivity;
import com.softsandr.man.ui.page.ManHistFavAdapter;
import com.softsandr.man.util.PrefHistoryUtils;

/**
 * This class used for...
 */
public class ManHistoryFragment extends ListFragment {
    /**
     * Create a new instance of ManBrowserActivity, initialized to
     * show the file name.
     */
    public static ManHistoryFragment newInstance() {
        return new ManHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getString(R.string.no_history_items));
        // Setup divider
        getListView().setDivider(getResources().getDrawable(android.R.color.transparent));
        getListView().setDividerHeight(25);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(new ManHistFavAdapter(getActivity(), PrefHistoryUtils.getHistoryCommands(getActivity())));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<ManTableRecord> adapter = (ArrayAdapter<ManTableRecord>) getListAdapter();
        selectItem(adapter.getItem(position));
    }

    private void selectItem(ManTableRecord record) {
        Intent startIntent = new Intent(getActivity(), ManBrowserActivity.class);
        startIntent.putExtra(ManBrowserActivity.MAN_TABLE_RECORD, record);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(startIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
