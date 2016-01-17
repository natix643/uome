package cz.kns.uome.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import cz.kns.uome.model.Person;

public class ParcelablePerson implements Parcelable {

	private final Person person;

	private ParcelablePerson(Parcel parcel) {
		this.person = new Person(
				parcel.readLong(),
				parcel.readLong(),
				parcel.readString(),
				parcel.readString(),
				parcel.readString(),
				parcel.readString());
	}

	public ParcelablePerson(Person person) {
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(person.getId());
		parcel.writeLong(person.getGroupId());
		parcel.writeString(person.getName());
		parcel.writeString(person.getEmail());
		parcel.writeString(person.getImageUri());
		parcel.writeString(person.getDescription());
	}

	public static final Creator<ParcelablePerson> CREATOR = new Creator<ParcelablePerson>() {
		@Override
		public ParcelablePerson createFromParcel(Parcel parcel) {
			return new ParcelablePerson(parcel);
		}

		@Override
		public ParcelablePerson[] newArray(int size) {
			return new ParcelablePerson[size];
		}
	};

}
