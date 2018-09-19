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
package com.softsandr.man.ui.page.search;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.softsandr.man.R;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.ui.page.ManPageAdapter;

import java.util.List;

/**
 * This class used for...
 */
public class ManSearchAdapter extends ManPageAdapter {

    public ManSearchAdapter(Context context, List<ManTableRecord> values) {
        super(context, R.layout.man_search_record, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.man_search_record, null);
            holder.section = (TextView) convertView.findViewById(R.id.search_result_section);
            holder.nameView = (TextView) convertView.findViewById(R.id.search_result_command_name);
            holder.synopsisView = (TextView) convertView.findViewById(R.id.search_result_command_synopsis);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ManTableRecord value = contentValues.get(position);
        holder.section.setText(convertView.getResources().getString(R.string.section) + value.getSection());
        holder.nameView.setText(Html.fromHtml(value.getName()));
        holder.synopsisView.setText(Html.fromHtml(value.getSynopsis()));
        return convertView;
    }
}
