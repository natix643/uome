package cz.kns.uome.activity;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;

import android.os.Bundle;
import android.widget.TextView;

import cz.kns.uome.R;
import cz.kns.uome.common.activity.UomeActivity;
import cz.kns.uome.fragment.ListFilesFragment;

public class SelectDirectoryActivity extends UomeActivity {

	public static final String KEY_SELECTED_DIRECTORY = "selectedDirectory";

	private TextView currentDirectoryText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setElevation(0);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);

		currentDirectoryText = findView(R.id.current_directory);

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
