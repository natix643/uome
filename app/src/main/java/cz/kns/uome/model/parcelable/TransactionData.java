package cz.kns.uome.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import cz.kns.uome.model.Transaction;
import cz.kns.uome.model.Transaction.Direction;

public class TransactionData implements Parcelable {

    public static final String TAG = TransactionData.class.getSimpleName();

    private Long id;
    private Long personId;
    private Long groupId;
    private String value;
    private Boolean financial;
    private Direction direction;
    private String description;
    private Date dateTime;

    public static TransactionData from(Transaction transaction) {
        return new TransactionData(transaction.getId(), transaction.getPersonId(),
                transaction.getGroupId(), transaction.getValue(), transaction.isFinancial(),
                transaction.getDirection(), transaction.getDescription(), transaction.getDateTime());
    }

    public TransactionData() {}

    public TransactionData(Long id, Long personId, Long groupId, String value,
            Boolean financial, Direction direction, String description, Date dateTime) {
        this.id = id;
        this.personId = personId;
        this.groupId = groupId;
        this.value = value;
        this.financial = financial;
        this.direction = direction;
        this.description = description;
        this.dateTime = dateTime;
    }

    private TransactionData(Parcel source) {
        this.id = (Long) source.readValue(null);
        this.personId = (Long) source.readValue(null);
        this.groupId = (Long) source.readValue(null);
        this.value = source.readString();
        this.financial = (Boolean) source.readValue(null);
        String directionString = source.readString();
        this.direction = directionString != null ? Direction.valueOf(directionString) : null;
        this.description = source.readString();
        Long millis = (Long) source.readValue(null);
        this.dateTime = millis != null ? new Date(millis) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeValue(id);
        destination.writeValue(personId);
        destination.writeValue(groupId);
        destination.writeString(value);
        destination.writeValue(financial);
        destination.writeString(direction != null ? direction.toString() : null);
        destination.writeString(description);
        destination.writeValue(dateTime != null ? dateTime.getTime() : null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isFinancial() {
        return financial;
    }

    public void setFinancial(Boolean financial) {
        this.financial = financial;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public static final Creator<TransactionData> CREATOR = new Creator<TransactionData>() {
        @Override
        public TransactionData createFromParcel(Parcel source) {
            return new TransactionData(source);
        }

        @Override
        public TransactionData[] newArray(int size) {
            return new TransactionData[size];
        }
    };
}
