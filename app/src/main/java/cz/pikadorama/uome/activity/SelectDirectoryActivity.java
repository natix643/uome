package cz.pikadorama.uome.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.File;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.activity.BaseActivity;
import cz.pikadorama.uome.fragment.ListFilesFragment;

import static com.google.common.base.Preconditions.checkArgument;

public class SelectDirectoryActivity extends BaseActivity {

    public static final String KEY_SELECTED_DIRECTORY = "selectedDirectory";

    private TextView currentDirectoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = requireView(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentDirectoryText = requireView(R.id.current_directory);

        if (savedInstanceState == null) {
            File directory = getDirectoryFromIntent();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, ListFilesFragment.of(directory), ListFilesFragment.TAG)
                    .commit();
        }
    }

    private File getDirectoryFromIntent() {
        String directoryPath = requireIntentExtra(KEY_SELECTED_DIRECTORY);
        File directory = new File(directoryPath);
        checkArgument(directory.isDirectory(), "%s is not a directory", directory);
        return directory;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.select_directory;
    }

    public void setCurrentDirectory(File directory) {
        currentDirectoryText.setText(directory.getPath());
    }

    @Override
    public void onBackPressed() {
        // XXX This hack is needed because ActionBarActivity
        // immediately closes the activity instead of popping the stack.
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
