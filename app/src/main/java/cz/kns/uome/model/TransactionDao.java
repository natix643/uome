package cz.kns.uome.model;

import static cz.kns.uome.model.Transaction.Column.indexOf;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cz.kns.uome.model.Transaction.Column;
import cz.kns.uome.model.Transaction.Direction;

public class TransactionDao extends Dao<Transaction> {

	public TransactionDao(Context context) {
		super(new SQLiteHelper(context));
	}

	public List<Transaction> getAllForGroup(Group group) {
		return getAllForGroup(group.getId());
	}

	public List<Transaction> getAllForGroup(long groupId) {
		String where = Column.GROUP_ID + " = ?";
		String[] whereArgs = { Long.toString(groupId) };
		return getAllWhereOrderedByDate(where, whereArgs);
	}

	public List<Transaction> getAllForPerson(Person person) {
		return getAllForPerson(person.getId());
	}

	public List<Transaction> getAllForPerson(long personId) {
		String where = Column.PERSON_ID + " = ?";
		String[] whereArgs = { Long.toString(personId) };
		return getAllWhereOrderedByDate(where, whereArgs);
	}

	private List<Transaction> getAllWhereOrderedByDate(String where, String[] whereArgs) {
		String orderBy = Column.DATE_TIME + " desc";
		return getAllWhere(where, whereArgs, orderBy);
	}

	@Override
	protected String getTableName() {
		return Transaction.TABLE_NAME;
	}

	@Override
	protected String getIdColumn() {
		return Transaction.Column.ID;
	}

	@Override
	protected Transaction cursorToEntity(Cursor cursor) {
		Long id = cursor.getLong(indexOf(Column.ID));
		long personId = cursor.getLong(indexOf(Column.PERSON_ID));
		long groupId = cursor.getLong(indexOf(Column.GROUP_ID));
		String value = cursor.getString(indexOf(Column.VALUE));
		boolean financial = cursor.getInt(indexOf(Column.FINANCIAL)) == 1;
		Direction direction = Direction.valueOf(cursor.getString(indexOf(Column.DIRECTION)));
		String description = cursor.getString(indexOf(Column.DESCRIPTION));
		Date dateTime = new Date(cursor.getLong(indexOf(Column.DATE_TIME)));

		return new Transaction(id, personId, groupId, value, financial, direction, description, dateTime);
	}

	@Override
	protected ContentValues entityToContentValues(Transaction transaction) {
		ContentValues values = new ContentValues();
		values.put(Column.PERSON_ID, transaction.getPersonId());
		values.put(Column.GROUP_ID, transaction.getGroupId());
		values.put(Column.VALUE, transaction.getValue());
		values.put(Column.FINANCIAL, transaction.isFinancial() ? 1 : 0);
		values.put(Column.DIRECTION, transaction.getDirection().toString());
		values.put(Column.DESCRIPTION, transaction.getDescription());
		values.put(Column.DATE_TIME, transaction.getDateTime().getTime());
		return values;
	}

}
