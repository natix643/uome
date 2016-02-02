package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.GroupMultichoiceAdapter;
import cz.pikadorama.uome.common.activity.UomeListActivity;
import cz.pikadorama.uome.common.view.SnackbarHelper;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.io.CsvExport;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.GroupDao;

public class ExportActivity extends UomeListActivity {

    private static final String TAG = ExportActivity.class.getName();

    private static final String PREFERENCE_EXPORT_DIRECTORY = "exportDirectory";

    private static final int REQUEST_SELECT_DIRECTORY = 1;

    private GroupDao groupDao;

    private SnackbarHelper snackbarHelper;

    private CsvExport csvExport;

    private File selectedDirectory;

    private TextView directoryPicker;

    private Button flipSelectionButton;
    private Button exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        snackbarHelper = new SnackbarHelper(this);

        groupDao = new GroupDao(this);
        csvExport = new CsvExport(this);

        initAdapter();
        initHeader();
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
            snackbarHelper.warn(R.string.error_cannot_write_directory);
            return;
        }

        List<Group> selectedGroups = getSelectedGroups();

        try {
            csvExport.export(selectedGroups, selectedDirectory);
            snackbarHelper.info(R.string.export_success);
        } catch (IOException ex) {
            Log.e(TAG, "CSV export failed", ex);
            snackbarHelper.error(R.string.error_export);
        }
    }

    private void initAdapter() {
        List<Group> groups = groupDao.getAllWithSimpleFirst();
        GroupMultichoiceAdapter adapter = new GroupMultichoiceAdapter(this, groups);
        setListAdapter(adapter);
    }

    private void initHeader() {
        View header = LayoutInflater.from(this).inflate(R.layout.export_header, getListView(), false);
        getListView().addHeaderView(header, null, false);

        directoryPicker = Views.require(header, R.id.directoryPicker);
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
        int selectedCount = getListView().getCheckedItemCount();

        exportButton.setEnabled(selectedCount > 0);
        flipSelectionButton.setText(selectedCount < getRealItemCount()
                ? R.string.button_select_all : R.string.button_select_none);
    }

    private void flipSelection() {
        setAllItems(getListView().getCheckedItemCount() < getRealItemCount());
        refreshButtons();
    }

    private void setAllItems(boolean selected) {
        for (int i = 1; i <= getRealItemCount(); i++) { // skip header view
            getListView().setItemChecked(i, selected);
        }
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
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

        for (int i = 1; i <= getRealItemCount(); i++) { // skip header view
            if (listView.isItemChecked(i)) {
                Group group = (Group) listView.getItemAtPosition(i);
                groups.add(group);
            }
        }
        return groups;
    }

    private int getRealItemCount() {
        return getListView().getCount() - 1; // exclude header view
    }

}
