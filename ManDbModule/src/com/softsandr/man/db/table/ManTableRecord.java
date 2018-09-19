/*******************************************************************************
 * Created by o.drachuk on 15/05/2014. 
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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represent specific table's one record from SQLite database
 */
public class ManTableRecord implements Parcelable {
    private long id;
    private String name;
    private String synopsis;
    private String file;
    private int section;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }


    public ManTableRecord() {}

    // Parcelling part
    public ManTableRecord(Parcel in){
        this.id = in.readLong();
        this.name = in.readString();
        this.synopsis = in.readString();
        this.file = in.readString();
        this.section = in.readInt();
    }

    public static final Parcelable.Creator<ManTableRecord> CREATOR = new Parcelable.Creator<ManTableRecord>() {

        @Override
        public ManTableRecord createFromParcel(Parcel source) {
            return new ManTableRecord(source);
        }

        @Override
        public ManTableRecord[] newArray(int size) {
            return new ManTableRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(synopsis);
        dest.writeString(file);
        dest.writeInt(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManTableRecord that = (ManTableRecord) o;

        return id == that.id && section == that.section &&
                !(name != null ? !name.equals(that.name) : that.name != null) &&
                !(synopsis != null ? !synopsis.equals(that.synopsis) : that.synopsis != null) &&
                !(file != null ? !file.equals(that.file) : that.file != null);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (synopsis != null ? synopsis.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + section;
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", file='" + file + '\'' +
                ", section='" + section + '\'' +
                '}';
    }
}
