/*******************************************************************************
 * Created by o.drachuk on 04/06/2014. 
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
package com.softsandr.man.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.softsandr.man.db.table.ManTableRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * This class used for...
 */
public abstract class ManPageAdapter extends ArrayAdapter<ManTableRecord> implements Filterable {
    protected final LayoutInflater inflater;
    protected List<ManTableRecord> contentValues;
    protected List<ManTableRecord> originalContentValues;

    public ManPageAdapter(Context context, int resource, List<ManTableRecord> values) {
        super(context, resource, values);
        this.contentValues = values;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contentValues.size();
    }

    @Override
    public ManTableRecord getItem(int position) {
        return contentValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView section;
        public TextView nameView;
        public TextView synopsisView;
        public CheckBox favButton;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contentValues = (List<ManTableRecord>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the results of a filtering operation in values
                List<ManTableRecord> filteredArray = new ArrayList<ManTableRecord>();

                // saves the original data
                if (originalContentValues == null) {
                    originalContentValues = new ArrayList<ManTableRecord>(contentValues);
                }

                // If constraint(CharSequence that is received) is null returns the original values
                // else does the Filtering and returns filteredArray(Filtered)
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = originalContentValues.size();
                    results.values = originalContentValues;
                } else {
                    constraint = constraint.toString();
                    for (ManTableRecord data : originalContentValues) {
                        if (data.getName().startsWith(constraint.toString())) {
                            filteredArray.add(data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredArray.size();
                    results.values = filteredArray;
                }
                return results;
            }
        };
    }
}
