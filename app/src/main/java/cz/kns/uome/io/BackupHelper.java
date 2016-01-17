package cz.kns.uome.io;

import java.io.File;
import java.io.IOException;

import android.content.Context;

import com.google.common.io.Files;

public class BackupHelper {

	private final Context context;
	private final String databaseFileName;
	private final String backupFileName;

	public BackupHelper(Context context, String databaseFileName, String backupFileName) {
		this.context = context;
		this.databaseFileName = databaseFileName;
		this.backupFileName = backupFileName;
	}

	public boolean isBackupFilePresent(File directory) {
		File file = getBackupFile(directory);
		return file.exists() && file.isFile() && file.canRead();
	}

	public void backupTo(File backupDirectory) throws IOException {
		Files.copy(getDatabaseFile(), getBackupFile(backupDirectory));
	}

	public void restoreFrom(File backupDirectory) throws IOException {
		Files.copy(getBackupFile(backupDirectory), getDatabaseFile());
	}

	private File getDatabaseFile() {
		return context.getDatabasePath(databaseFileName);
	}

	private File getBackupFile(File backupDirectory) {
		return new File(backupDirectory, backupFileName);
	}

}
