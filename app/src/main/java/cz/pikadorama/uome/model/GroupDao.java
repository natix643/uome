package cz.pikadorama.uome.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.model.Group.Column;

import static cz.pikadorama.uome.model.Group.Column.indexOf;

public class GroupDao extends Dao<Group> {

    public GroupDao(Context context) {
        super(context);
    }

    public Group getByName(String name) {
        String whereClause = Column.NAME + " = ?";
        String[] whereArgs = { name };
        return getWhere(whereClause, whereArgs);
    }

    public List<Group> getAllWithSimpleFirst() {
        Group simpleDebts = getById(Constants.SIMPLE_GROUP_ID);

        List<Group> all = getAllWithoutSimple();
        all.add(0, simpleDebts);
        return all;
    }

    public List<Group> getAllWithoutSimple() {
        String whereClause = Column.ID + " != ?";
        String[] whereArgs = { Long.toString(Constants.SIMPLE_GROUP_ID) };
        String orderBy = Column.NAME + " collate localized asc";

        return getAllWhere(whereClause, whereArgs, orderBy);
    }

    @Override
    protected String getTableName() {
        return Group.TABLE_NAME;
    }

    @Override
    protected String getIdColumn() {
        return Group.Column.ID;
    }

    @Override
    protected Group cursorToEntity(Cursor cursor) {
        long id = cursor.getLong(indexOf(Column.ID));
        String name = cursor.getString(indexOf(Column.NAME));
        String description = cursor.getString(indexOf(Column.DESCRIPTION));

        return new Group(id, name, description);
    }

    @Override
    protected ContentValues entityToContentValues(Group group) {
        ContentValues values = new ContentValues();
        values.put(Column.NAME, group.getName());
        values.put(Column.DESCRIPTION, group.getDescription());
        return values;
    }

}
