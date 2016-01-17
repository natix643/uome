package cz.kns.uome.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cz.kns.uome.common.util.Closeables;

public abstract class Dao<E extends Entity> {

    private static final String[] ALL_COLUMNS = null;

    private final Function<E, String> getIdString = new Function<E, String>() {
        @Override
        public String apply(E entity) {
            return entity.getId().toString();
        }
    };

    private final SQLiteOpenHelper databaseHelper;

    protected Dao(SQLiteOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    protected SQLiteOpenHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public E getById(long id) {
        String whereClause = getIdColumn() + " = ?";
        String[] whereArgs = {Long.toString(id)};
        return getWhere(whereClause, whereArgs);
    }

    @SuppressWarnings("resource")
    protected E getWhere(String whereClause, String[] whereArgs) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getWritableDatabase()
                    .query(getTableName(), ALL_COLUMNS, whereClause, whereArgs, null, null, null);
            return cursor.moveToFirst() ? cursorToEntity(cursor) : null;
        } finally {
            Closeables.close(cursor);
            databaseHelper.close();
        }
    }

    public List<E> getAll() {
        String orderBy = getIdColumn() + " asc";
        return getAllOrderedBy(orderBy);
    }

    protected List<E> getAllOrderedBy(String orderBy) {
        return getAllWhere(null, null, orderBy);
    }

    @SuppressWarnings("resource")
    protected List<E> getAllWhere(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getWritableDatabase()
                    .query(getTableName(), ALL_COLUMNS, whereClause, whereArgs, null, null, orderBy);

            List<E> entities = new ArrayList<>(cursor.getCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                entities.add(cursorToEntity(cursor));
            }
            return entities;
        } finally {
            Closeables.close(cursor);
            databaseHelper.close();
        }
    }

    public long create(E entity) {
        try {
            ContentValues values = entityToContentValues(entity);
            return databaseHelper.getWritableDatabase().insert(getTableName(), null, values);
        } finally {
            databaseHelper.close();
        }
    }

    public void update(E entity) {
        try {
            ContentValues values = entityToContentValues(entity);
            String whereClause = getIdColumn() + " = ?";
            String[] whereArgs = {entity.getId().toString()};

            databaseHelper.getWritableDatabase().update(getTableName(), values, whereClause, whereArgs);
        } finally {
            databaseHelper.close();
        }
    }

    public void delete(E entity) {
        String whereClause = getIdColumn() + " = ?";
        String[] whereArgs = {entity.getId().toString()};
        deleteWhere(whereClause, whereArgs);
    }

    public int deleteAll() {
        return deleteWhere(null, null);
    }

    public int deleteAll(Collection<E> entities) {
        Collection<String> questionMarks = Collections.nCopies(entities.size(), "?");
        StringBuilder whereClause = new StringBuilder(getIdColumn()).append(" IN (");
        Joiner.on(",").appendTo(whereClause, questionMarks).append(")");

        Collection<String> ids = Collections2.transform(entities, getIdString);
        String[] whereArgs = ids.toArray(new String[ids.size()]);

        int deletedCount = deleteWhere(whereClause.toString(), whereArgs);
        Preconditions.checkState(entities.size() == deletedCount,
                "wrong number of deleted rows. expected: %s, actual: %s", entities.size(), deletedCount);
        return deletedCount;
    }

    protected int deleteWhere(String whereClause, String[] whereArgs) {
        try {
            return databaseHelper.getWritableDatabase().delete(getTableName(), whereClause, whereArgs);
        } finally {
            databaseHelper.close();
        }
    }

    protected abstract String getTableName();

    protected abstract String getIdColumn();

    protected abstract E cursorToEntity(Cursor cursor);

    protected abstract ContentValues entityToContentValues(E entity);
}
