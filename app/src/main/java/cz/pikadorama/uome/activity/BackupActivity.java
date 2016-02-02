package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.format.MessageFormatter;
import cz.pikadorama.uome.common.view.SnackbarHelper;
import cz.pikadorama.uome.dialog.ConfirmationDialog;
import cz.pikadorama.uome.io.BackupHelper;
import cz.pikadorama.uome.model.SQLiteHelper;

public class BackupActivity extends UomeActivity implements ConfirmationDialog.Callback {

    // TODO refactor common code with ExportActivity

    private static final String TAG = BackupActivity.class.getName();

    private static final String BACKUP_FILE_NAME = "uome.backup";

    private static final String PREFERENCE_BACKUP_DIRECTORY = "backupDirectory";

    private static final int REQUEST_SELECT_DIRECTORY = 1;

    private static final String REQUEST_OVERWRITE_BACKUP = "overwriteBackup";
    private static final String REQUEST_RESTORE_FROM_BACKUP = "restoreFromBackup";

    private final BackupHelper backupHelper = new BackupHelper(this, SQLiteHelper.DATABASE_NAME, BACKUP_FILE_NAME);

    private final MessageFormatter messageFormatter = new MessageFormatter(this);

    private SnackbarHelper snackbarHelper;

    private TextView directoryPicker;

    private File selectedDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        snackbarHelper = new SnackbarHelper(this);

        initTexts();
        initPicker();
        initButtons();

        String directoryPath = getPreferences(MODE_PRIVATE).getString(PREFERENCE_BACKUP_DIRECTORY, null);
        selectDirectoryForPath(directoryPath);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.backup;
    }

    private void initTexts() {
        TextView backupDescriptionText = requireView(R.id.backupDescriptionText);
        backupDescriptionText.setText(
                messageFormatter.formatHtml(R.string.backup_description_backup, BACKUP_FILE_NAME));

        TextView restoreDescriptionText = requireView(R.id.restoreDescriptionText);
        restoreDescriptionText.setText(
                messageFormatter.formatHtml(R.string.backup_description_restore, BACKUP_FILE_NAME));
    }

    private void initPicker() {
        directoryPicker = requireView(R.id.directoryPicker);
        directoryPicker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, SelectDirectoryActivity.class)
                        .putExtra(SelectDirectoryActivity.KEY_SELECTED_DIRECTORY, selectedDirectory.getPath());
                startActivityForResult(intent, REQUEST_SELECT_DIRECTORY);
            }
        });
    }

    private void initButtons() {
        Button backupButton = requireView(R.id.backupButton);
        backupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backup();
            }
        });

        Button restoreButton = requireView(R.id.restoreButton);
        restoreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                restore();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDirectory(selectedDirectory);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECT_DIRECTORY:
                    String directoryPath = data.getStringExtra(SelectDirectoryActivity.KEY_SELECTED_DIRECTORY);
                    selectDirectoryForPath(directoryPath);
                    break;
                default:
                    throw new IllegalStateException("Illegal request code: " + requestCode);
            }
        }
    }

    private void selectDirectory(File directory) {
        selectedDirectory = getDefaultDirectoryIfCannotWrite(directory);
        directoryPicker.setText(selectedDirectory.getPath());
    }

    private void selectDirectoryForPath(String path) {
        selectDirectory(path != null ? new File(path) : null);
    }

    public static File getDefaultDirectoryIfCannotWrite(File directory) {
        if (directory != null && directory.isDirectory() && directory.canWrite()) {
            return directory;
        } else {
            return Environment.getExternalStorageDirectory();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREFERENCE_BACKUP_DIRECTORY, selectedDirectory.getPath())
                .commit();
    }

    @Override
    public void onConfirmed(String requestCode) {
        switch (requestCode) {
            case REQUEST_OVERWRITE_BACKUP:
                forceBackup();
                break;
            case REQUEST_RESTORE_FROM_BACKUP:
                forceRestore();
                break;
        }
    }

    private void backup() {
        if (!(selectedDirectory.isDirectory() && selectedDirectory.canWrite())) {
            snackbarHelper.warn(R.string.error_cannot_write_directory);
            return;
        }

        if (backupHelper.isBackupFilePresent(selectedDirectory)) {
            ConfirmationDialog.of(REQUEST_OVERWRITE_BACKUP)
                    .setTitle(R.string.dialog_overwrite_backup_title)
                    .setMessage(R.string.dialog_overwrite_backup_message)
                    .setPositiveButton(R.string.dialog_overwrite_backup_button)
                    .show(this);
        } else {
            forceBackup();
        }
    }

    private void forceBackup() {
        try {
            backupHelper.backupTo(selectedDirectory);
            snackbarHelper.info(R.string.toast_backup_successful);
        } catch (IOException e) {
            Log.e(TAG, "Backup failed", e);
            snackbarHelper.error(R.string.error_backup_failed);
        }
    }

    private void restore() {
        if (!backupHelper.isBackupFilePresent(selectedDirectory)) {
            snackbarHelper.warn(R.string.error_missing_backup_file);
            return;
        }

        ConfirmationDialog.of(REQUEST_RESTORE_FROM_BACKUP)
                .setTitle(R.string.dialog_restore_from_backup_title)
                .setMessage(R.string.dialog_restore_from_backup_message)
                .setPositiveButton(R.string.dialog_restore_from_backup_button)
                .show(this);
    }

    private void forceRestore() {
        try {
            backupHelper.restoreFrom(selectedDirectory);
            snackbarHelper.info(R.string.toast_restore_successful);
        } catch (IOException e) {
            // TODO maybe we could show dialog with something like "backup failed, your data may be corrupted"
            throw new RuntimeException(e);
        }
    }

}
