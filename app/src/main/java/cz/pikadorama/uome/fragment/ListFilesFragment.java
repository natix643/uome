package cz.pikadorama.uome.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.activity.SelectDirectoryActivity;
import cz.pikadorama.uome.adapter.FileAdapter;
import cz.pikadorama.uome.common.fragment.BaseListFragment;
import cz.pikadorama.uome.common.view.SnackbarHelper;
import cz.pikadorama.uome.dialog.CreateDirectoryDialog;

public class ListFilesFragment extends BaseListFragment implements CreateDirectoryDialog.Callback {

    public static final String TAG = ListFilesFragment.class.getName();

    private static final String ARG_DIRECTORY = "directory";

    private SelectDirectoryActivity activity;

    private SnackbarHelper snackbarHelper;

    private FileAdapter adapter;
    private File currentDirectory;

    public static ListFilesFragment of(File directory) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DIRECTORY, directory);

        ListFilesFragment fragment = new ListFilesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);

        activity = (SelectDirectoryActivity) a;

        adapter = new FileAdapter(activity);
        setListAdapter(adapter);

        currentDirectory = requireArgument(ARG_DIRECTORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_avatars, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        snackbarHelper = new SnackbarHelper(getBaseActivity());

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        currentDirectory = getParentIfCannotRead(currentDirectory);
        adapter.setDirectory(currentDirectory);
        activity.setCurrentDirectory(currentDirectory);
    }

    private static File getParentIfCannotRead(File directory) {
        File result = directory;
        while (!result.isDirectory() || !result.canRead()) {
            result = result.getParentFile();
        }
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_files, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_directory:
                if (!currentDirectory.canWrite()) {
                    snackbarHelper.warn(R.string.error_cannot_write_directory);
                } else {
                    new CreateDirectoryDialog().show(this);
                }
                return true;
            case R.id.menu_done:
                if (!currentDirectory.canWrite()) {
                    snackbarHelper.warn(R.string.error_cannot_write_directory);
                } else {
                    Intent intent = new Intent().putExtra(
                            SelectDirectoryActivity.KEY_SELECTED_DIRECTORY,
                            currentDirectory.getPath());
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCreateDirectory(String name, CreateDirectoryDialog dialog) {
        File child = new File(currentDirectory, name);
        if (child.exists()) {
            dialog.showError(R.string.error_directory_exists);
            return;
        }

        dialog.dismiss();

        if (child.mkdir()) {
            adapter.setDirectory(currentDirectory);
            snackbarHelper.info(R.string.toast_directory_created);
        } else {
            snackbarHelper.warn(R.string.error_create_directory);
        }
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        File item = adapter.getItem(position);

        if (item.isDirectory() && item.canRead()) {
            if (isOnTop(item)) {
                getFragmentManager().popBackStack();
            } else {
                push(item);
            }
        }
    }

    private boolean isOnTop(File directory) {
        FragmentManager manager = getFragmentManager();
        int count = manager.getBackStackEntryCount();

        if (count > 0) {
            BackStackEntry top = manager.getBackStackEntryAt(count - 1);
            return top.getName().equals(directory.getPath());
        } else {
            return false;
        }
    }

    private void push(File newDirectory) {
        getFragmentManager().beginTransaction()
                .addToBackStack(currentDirectory.getPath())
                .replace(R.id.container, ListFilesFragment.of(newDirectory), TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

}