package cz.kns.uome.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import cz.kns.uome.model.Person.Column;

import static cz.kns.uome.model.Person.Column.indexOf;

public class PersonDao extends Dao<Person> {

    public PersonDao(Context context) {
        super(new SQLiteHelper(context));
    }

    public Person getByNameForGroup(String name, long groupId) {
        String whereClause = Column.NAME + " = ? and " + Column.GROUP_ID + " = ?";
        String[] whereArgs = {name, Long.toString(groupId)};
        return getWhere(whereClause, whereArgs);
    }

    public List<Person> getAllForGroup(Group group) {
        return getAllForGroup(group.getId());
    }

    public List<Person> getAllForGroup(long groupId) {
        String whereClause = Column.GROUP_ID + " = ?";
        String[] whereArgs = {Long.toString(groupId)};
        String orderBy = Column.NAME + " collate localized asc";

        return getAllWhere(whereClause, whereArgs, orderBy);
    }

    public int deleteAllForGroup(Group group) {
        return deleteAllForGroup(group.getId());
    }

    public int deleteAllForGroup(long groupId) {
        String whereClause = Person.Column.GROUP_ID + " = ?";
        String[] whereArgs = {Long.toString(groupId)};
        return deleteWhere(whereClause, whereArgs);
    }

    @Override
    protected String getTableName() {
        return Person.TABLE_NAME;
    }

    @Override
    protected String getIdColumn() {
        return Person.Column.ID;
    }

    @Override
    protected Person cursorToEntity(Cursor cursor) {
        long id = cursor.getLong(indexOf(Column.ID));
        long groupId = cursor.getLong(indexOf(Column.GROUP_ID));
        String name = cursor.getString(indexOf(Column.NAME));
        String email = cursor.getString(indexOf(Column.EMAIL));
        String description = cursor.getString(indexOf(Column.DESCRIPTION));
        String imageUri = cursor.getString(indexOf(Column.IMAGE_URI));

        return new Person(id, groupId, name, email, imageUri, description);
    }

    @Override
    protected ContentValues entityToContentValues(Person person) {
        ContentValues values = new ContentValues();
        values.put(Column.GROUP_ID, person.getGroupId());
        values.put(Column.NAME, person.getName());
        values.put(Column.EMAIL, person.getEmail());
        values.put(Column.DESCRIPTION, person.getDescription());
        values.put(Column.IMAGE_URI, person.getImageUri());
        return values;
    }

}
