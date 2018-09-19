/*******************************************************************************
 * Created by Oleksandr Drachuk on 6/5/15.
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

/**
 * The class used for
 */
public enum ManSectionInfo {
    SECTION_A("A", "a_section", ManAFragment.class, 0),
    SECTION_B("B", "b_section", ManBFragment.class, 1),
    SECTION_C("C", "c_section", ManCFragment.class, 2),
    SECTION_D("D", "d_section", ManDFragment.class, 3),
    SECTION_E("E", "e_section", ManEFragment.class, 4),
    SECTION_F("F", "f_section", ManFFragment.class, 5),
    SECTION_G("G", "g_section", ManGFragment.class, 6),
    SECTION_H("H", "h_section", ManHFragment.class, 7),
    SECTION_I("I", "i_section", ManIFragment.class, 8),
    SECTION_J("J", "j_section", ManJFragment.class, 9),
    SECTION_K("K", "k_section", ManKFragment.class, 10),
    SECTION_L("L", "l_section", ManLFragment.class, 11),
    SECTION_M("M", "m_section", ManMFragment.class, 12),
    SECTION_N("N", "n_section", ManNFragment.class, 13),
    SECTION_O("O", "o_section", ManOFragment.class, 14),
    SECTION_P("P", "p_section", ManPFragment.class, 15),
    SECTION_Q("Q", "q_section", ManQFragment.class, 16),
    SECTION_R("R", "r_section", ManRFragment.class, 17),
    SECTION_S("S", "s_section", ManSFragment.class, 18),
    SECTION_T("T", "t_section", ManTFragment.class, 19),
    SECTION_U("U", "u_section", ManUFragment.class, 20),
    SECTION_V("V", "v_section", ManVFragment.class, 21),
    SECTION_W("W", "w_section", ManWFragment.class, 22),
    SECTION_X("X", "x_section", ManXFragment.class, 23),
    SECTION_Y("Y", "y_section", ManYFragment.class, 24),
    SECTION_Z("Z", "z_section", ManZFragment.class, 25);

    private final String sectionSymbol;
    private final String sectionId;
    private final Class<? extends ManSymbolFragment> sectionClass;
    private final int index;

    ManSectionInfo(String sectionSymbol, String sectionId, Class<? extends ManSymbolFragment> sectionClass, int index) {
        this.sectionSymbol = sectionSymbol;
        this.sectionId = sectionId;
        this.sectionClass = sectionClass;
        this.index = index;
    }

    public String getSectionSymbol() {
        return sectionSymbol;
    }

    public String getSectionId() {
        return sectionId;
    }

    public int getIndex() {
        return index;
    }

    public Class<? extends ManSymbolFragment> getSectionClass() {
        return sectionClass;
    }
}