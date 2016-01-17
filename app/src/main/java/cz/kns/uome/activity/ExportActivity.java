package cz.kns.uome.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import cz.kns.uome.R;
import cz.kns.uome.adapter.GroupMultichoiceAdapter;
import cz.kns.uome.common.activity.UomeListActivity;
import cz.kns.uome.common.util.Toaster;
import cz.kns.uome.dialog.MessageDialog;
import cz.kns.uome.io.CsvExport;
import cz.kns.uome.model.Group;
import cz.kns.uome.model.GroupDao;

public class ExportActivity extends UomeListActivity {

	private static final String TAG = ExportActivity.class.getName();

	private static final String PREFERENCE_EXPORT_DIRECTORY = "exportDirectory";

	private static final int REQUEST_SELECT_DIRECTORY = 1;

	private GroupDao groupDao;
	private GroupMultichoiceAdapter adapter;

	private Toaster toaster;
	private CsvExport csvExport;

	private File selectedDirectory;

	private TextView directoryPicker;

	private Button flipSelectionButton;
	private Button exportButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		toaster = new Toaster(this);
		groupDao = new GroupDao(this);
		csvExport = new CsvExport(this);

		initAdapter();
		initPicker();
		initButtons();

		String directoryPath = getPreferences(MODE_PRIVATE).getString(PREFERENCE_EXPORT_DIRECTORY, null);
		selectDirectoryForPath(directoryPath);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.export;
	}

	private void export() {
		if (!(selectedDirectory.isDirectory() && selectedDirectory.canWrite())) {
			toaster.show(R.string.error_cannot_write_directory);
			return;
		}

		List<Group> selectedGroups = getSelectedGroups();

		try {
			csvExport.export(selectedGroups, selectedDirectory);
			toaster.show(R.string.export_success);
		} catch (IOException ex) {
			Log.e(TAG, "CSV export failed", ex);
			MessageDialog.create(R.string.error_export, R.string.error, R.drawable.ic_dialog_error).show(this);
		}
	}

	private void initAdapter() {
		List<Group> groups = groupDao.getAllWithSimpleFirst();
		adapter = new GroupMultichoiceAdapter(this, groups);
		setListAdapter(adapter);
	}

	private void initPicker() {
		directoryPicker = findView(R.id.directoryPicker);
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
		flipSelectionButton = requireView(R.id.flipSelectionButton);
		flipSelectionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flipSelection();
			}
		});

		exportButton = requireView(R.id.exportButton);
		exportButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				export();
			}
		});
	}

	private void refreshButtons() {
		exportButton.setEnabled(getListView().getCheckedItemCount() > 0);

		if (getListView().getCheckedItemCount() < getListView().getCount()) {
			flipSelectionButton.setText(R.string.button_select_all);
		} else {
			flipSelectionButton.setText(R.string.button_select_none);
		}
	}

	private void flipSelection() {
		if (getListView().getCheckedItemCount() < getListView().getCount()) {
			checkAll(true);
		} else {
			checkAll(false);
		}
		refreshButtons();
	}

	private void checkAll(boolean checked) {
		ListView listView = getListView();
		for (int i = 0; i < listView.getCount(); i++) {
			listView.setItemChecked(i, checked);
		}
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		refreshButtons();
	}

	@Override
	protected void onResume() {
		super.onResume();
		selectDirectory(selectedDirectory);
		refreshButtons();
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
				.putString(PREFERENCE_EXPORT_DIRECTORY, selectedDirectory.getPath())
				.commit();
	}

	private List<Group> getSelectedGroups() {
		List<Group> groups = new ArrayList<>();
		ListView listView = getListView();

		for (int i = 0; i < listView.getCount(); i++) {
			if (listView.isItemChecked(i)) {
				groups.add(adapter.getItem(i));
			}
		}
		return groups;
	}

}
