package cz.kns.uome.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import cz.kns.uome.model.Group;

public class ParcelableGroup implements Parcelable {

    private final Group group;

    private ParcelableGroup(Parcel parcel) {
        this.group = new Group(parcel.readLong(), parcel.readString(), parcel.readString());
    }

    public ParcelableGroup(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(group.getId());
        parcel.writeString(group.getName());
        parcel.writeString(group.getDescription());
    }

    public static final Creator<ParcelableGroup> CREATOR = new Creator<ParcelableGroup>() {
        @Override
        public ParcelableGroup createFromParcel(Parcel parcel) {
            return new ParcelableGroup(parcel);
        }

        @Override
        public ParcelableGroup[] newArray(int size) {
            return new ParcelableGroup[size];
        }
    };

}
