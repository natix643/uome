package cz.pikadorama.uome.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;

import static cz.pikadorama.uome.model.Transaction.Column.*;

public class Transaction implements Entity {

    static final String TABLE_NAME = "transaction_table";
    static final String TEMP_TABLE_NAME = "transaction_table_temp";

    static class Column {
        static final String ID = "_id";
        static final String PERSON_ID = "personId";
        static final String GROUP_ID = "groupId";
        static final String VALUE = "value";
        static final String FINANCIAL = "financial";
        static final String DIRECTION = "direction";
        static final String DESCRIPTION = "description";
        static final String DATE_TIME = "dateTime";

        private static final List<String> ALL = ImmutableList.of(
                ID, PERSON_ID, GROUP_ID, VALUE, FINANCIAL, DIRECTION, DESCRIPTION, DATE_TIME);

        static int indexOf(String column) {
            return ALL.indexOf(column);
        }
    }

    static class Sql {
        static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement, "
                + PERSON_ID + " integer not null, "
                + GROUP_ID + " integer not null, "
                + VALUE + " text not null, "
                + FINANCIAL + " integer not null, "
                + DIRECTION + " text not null, "
                + DESCRIPTION + " text, "
                + DATE_TIME + " integer not null, "
                + "foreign key(" + PERSON_ID + ") references " + Person.TABLE_NAME + "(" + Person.Column.ID
                + ") on delete cascade, "
                + "foreign key(" + GROUP_ID + ") references " + Group.TABLE_NAME + "(" + Group.Column.ID
                + ") on delete cascade"
                + ");";

        static final String RENAME_TABLE_TO_TEMP = "alter table " + TABLE_NAME + " rename to " + TEMP_TABLE_NAME + ";";
        static final String COPY_FROM_TEMP_TABLE = "insert into " + TABLE_NAME
                + " select * from " + TEMP_TABLE_NAME + ";";
        static final String DROP_TEMP_TABLE = "drop table " + TEMP_TABLE_NAME + ";";
    }

    public enum Direction {
        WITHDRAWAL, DEPOSIT;
    }

    private Long id;
    private long personId;
    private long groupId;
    private String value;
    private boolean financial;
    private Direction direction;
    private String description;
    private Date dateTime;

    public Transaction(long personId, long groupId, String value, boolean financial,
            Direction direction, String description, Date dateTime) {
        this.personId = personId;
        this.groupId = groupId;
        this.value = value;
        this.financial = financial;
        this.direction = direction;
        this.description = description;
        this.dateTime = dateTime;
    }

    public Transaction(Long id, long personId, long groupId, String value, boolean financial,
            Direction direction, String description, Date dateTime) {
        this.id = id;
        this.personId = personId;
        this.groupId = groupId;
        this.value = value;
        this.financial = financial;
        this.direction = direction;
        this.description = description;
        this.dateTime = dateTime;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFinancial() {
        return financial;
    }

    public void setFinancial(boolean financial) {
        this.financial = financial;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("personId", personId)
                .add("groupId", groupId)
                .add("value", value)
                .add("financial", financial)
                .add("direction", direction)
                .add("description", description)
                .add("dateTime", dateTime)
                .toString();
    }

}