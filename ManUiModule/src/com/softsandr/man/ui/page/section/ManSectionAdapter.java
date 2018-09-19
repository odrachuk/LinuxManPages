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
package com.softsandr.man.ui.page.section;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.softsandr.man.R;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.ui.page.ManPageAdapter;
import com.softsandr.man.util.PrefFavoritesUtils;

import java.util.List;
import java.util.Map;

/**
 * This class used for...
 */
public class ManSectionAdapter extends ManPageAdapter {

    public ManSectionAdapter(Context context, List<ManTableRecord> values) {
        super(context, R.layout.man_table_record, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.man_table_record, null);
            holder.nameView = (TextView) convertView.findViewById(R.id.list_row_command_name);
            holder.synopsisView = (TextView) convertView.findViewById(R.id.list_row_command_synopsis);
            holder.favButton = (CheckBox) convertView.findViewById(R.id.favorite_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ManTableRecord value = contentValues.get(position);
        // Name
        holder.nameView.setText(Html.fromHtml(value.getName()));
        // Synopsis
        holder.synopsisView.setText(Html.fromHtml(value.getSynopsis()));
        // Favorite
        holder.favButton.setChecked(PrefFavoritesUtils.getFavorites(getContext()).contains(value));
        holder.favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        holder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    PrefFavoritesUtils.addFavorite(getContext(), value);
                } else {
                    PrefFavoritesUtils.removeFavorite(getContext(), value);
                }
            }
        });
        return convertView;
    }
}
