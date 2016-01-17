package cz.kns.uome.model.parcelable;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import cz.kns.uome.model.Transaction;
import cz.kns.uome.model.Transaction.Direction;

public class ParcelableTransaction implements Parcelable {

	private final Transaction transaction;

	private ParcelableTransaction(Parcel parcel) {
		this.transaction = new Transaction(
				parcel.readLong(),
				parcel.readLong(),
				parcel.readLong(),
				parcel.readString(),
				Boolean.parseBoolean(parcel.readString()),
				Direction.valueOf(parcel.readString()),
				parcel.readString(),
				new Date(parcel.readLong()));
	}

	public ParcelableTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(transaction.getId());
		parcel.writeLong(transaction.getPersonId());
		parcel.writeLong(transaction.getGroupId());
		parcel.writeString(transaction.getValue());
		parcel.writeString(String.valueOf(transaction.isFinancial()));
		parcel.writeString(transaction.getDirection().toString());
		parcel.writeString(transaction.getDescription());
		parcel.writeLong(transaction.getDateTime().getTime());
	}

	public static final Creator<ParcelableTransaction> CREATOR = new Creator<ParcelableTransaction>() {
		@Override
		public ParcelableTransaction createFromParcel(Parcel parcel) {
			return new ParcelableTransaction(parcel);
		}

		@Override
		public ParcelableTransaction[] newArray(int size) {
			return new ParcelableTransaction[size];
		}
	};

}
