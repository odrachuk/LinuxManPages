/*******************************************************************************
 * Created by o.drachuk on 14/06/2014. 
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
package com.softsandr.man.ui.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.softsandr.man.R;

/**
 * This class used for...
 */
public class DrawerMenuItem implements DrawerItem {
    private final String str;

    public DrawerMenuItem(String text) {
        this.str = text;
    }

    @Override
    public int getViewType() {
        return DrawerListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.drawer_menu_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }
        TextView text = (TextView) view.findViewById(R.id.menu_item);
        text.setText(str);
        return view;
    }

}
