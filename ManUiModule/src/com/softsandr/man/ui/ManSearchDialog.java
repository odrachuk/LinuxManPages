/*******************************************************************************
 * Created by o.drachuk on 15/06/2014. 
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

import android.app.*;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.softsandr.man.R;
import com.softsandr.man.app.AppContext;
import com.softsandr.man.db.ManSQLiteOpenHelper;
import com.softsandr.man.db.ResponseListener;
import com.softsandr.man.db.utils.ManSQLiteUtil;
import com.softsandr.man.db.table.ManTable;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.ui.page.search.ManSearchFragment;

import java.util.List;
import java.util.logging.Logger;

/**
 * This class used for...
 */
public class ManSearchDialog extends DialogFragment implements AdapterView.OnItemSelectedListener, ResponseListener<ManTableRecord> {
    private static final String LOG_TAG = ManSearchDialog.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);
    private static final String MAN_FILTER_DLG_TAG = LOG_TAG + ".MAN_FILTER_DLG_TAG";

    private EditText searchField;
    private int selectedSection;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.man_filter_dlg_btn_ok) {
                if (!searchField.getText().toString().isEmpty()) {
                    execSearch();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_search_criteria), Toast.LENGTH_LONG).show();
                }
            } else if (viewId == R.id.man_filter_dlg_btn_cancel) {
                closeDialog();
            }
        }
    };

    public static void showFilterDialog(Activity activity) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(MAN_FILTER_DLG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = new ManSearchDialog();
        newFragment.show(ft, MAN_FILTER_DLG_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        dialog.setTitle(getResources().getString(android.R.string.search_go));
        Window window;
        window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View v = inflater.inflate(R.layout.man_filter_dlg_layout, container, false);
        if (v != null) {
            // spinner
            Spinner spinner = (Spinner) v.findViewById(R.id.man_filter_dlg_sections_spin);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.man_filter_dlg_sections_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            // buttons
            v.findViewById(R.id.man_filter_dlg_btn_ok).setOnClickListener(mOnClickListener);
            v.findViewById(R.id.man_filter_dlg_btn_cancel).setOnClickListener(mOnClickListener);
            // search field
            searchField = (EditText) v.findViewById(R.id.man_filter_dlg_input1);
        }
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSection = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // ignored
    }

    private void execSearch() {
        LOGGER.entering(LOG_TAG, "execSearch");
        ManSQLiteUtil commandDataSource = new ManSQLiteUtil((ManSQLiteOpenHelper) getActivity().getApplication().
                getSystemService(AppContext.MAN_DATABASE_HELPER_SERVICE));
        if (selectedSection != 0) { // search in specific man section
            commandDataSource.getRecordsBySymbolAsync(this,
                    ManTable.getBySection(selectedSection),
                    searchField.getText().toString());
        } else { // search in all database
            commandDataSource.searchRecordsBySymbolAsync(this, searchField.getText().toString());
        }
    }

    private void showSearchResults(List<ManTableRecord> searchResults) {
        LOGGER.entering(LOG_TAG, "showSearchResults");
        closeDialog();
        android.support.v4.app.FragmentManager fragmentManager = ((ManMainActivity) getActivity()).getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        android.support.v4.app.Fragment fragment = ManSearchFragment.newInstance((java.util.ArrayList<ManTableRecord>) searchResults);
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDbResponse(List<ManTableRecord> responseData) {
        if (!responseData.isEmpty()) {
            showSearchResults(responseData);
        } else {
            closeDialog();
            Toast.makeText(getActivity(), getString(R.string.no_search_results), Toast.LENGTH_LONG).show();
        }
    }

    private void closeDialog() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
        getDialog().cancel();
    }
}
