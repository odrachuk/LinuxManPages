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
package com.softsandr.man.db.table;

import java.util.HashMap;
import java.util.Map;

/**
 * This class used for...
 */
public enum ManTable {
    MAN1("man1", 1),
    MAN2("man2", 2),
    MAN3("man3", 3),
    MAN4("man4", 4),
    MAN5("man5", 5),
    MAN6("man6", 6),
    MAN7("man7", 7),
    MAN8("man8", 8),
    MAN9("man9", 9);

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SYNOPSIS = "synopsis";
    public static final String COLUMN_FILE = "file";

    private final static Map<Integer, ManTable> MAP_BY_VALUE = new HashMap<Integer, ManTable>();

    static {
        for (ManTable mt : values()) {
            MAP_BY_VALUE.put(mt.getSection(), mt);
        }
    }

    private final String name;
    private final int section;

    ManTable(String name, int section) {
        this.name = name;
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public int getSection() {
        return section;
    }

    public static ManTable getBySection(int section) {
        return MAP_BY_VALUE.get(section);
    }
}
