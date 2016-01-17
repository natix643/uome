package cz.kns.uome.common.util;

import android.database.Cursor;

/**
 * @deprecated replace with {@link com.google.common.io.Closeables} when API level is >= 16
 */
@Deprecated
public class Closeables {

	private Closeables() {}

	/**
	 * Closes the given {@link Cursor}. If it is null, this method does nothing.
	 * 
	 * @param cursor
	 */
	public static void close(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

}
