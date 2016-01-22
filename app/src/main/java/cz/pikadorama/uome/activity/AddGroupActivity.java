package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.ActivityPurpose;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.util.SnackbarHelper;
import cz.pikadorama.uome.common.util.Toaster;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.GroupDao;
import cz.pikadorama.uome.model.parcelable.ParcelableGroup;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddGroupActivity extends UomeActivity {

    private GroupDao groupDao;

    private Toaster toaster;
    private SnackbarHelper snackbarHelper;

    private Group editedGroup;
    private int purpose;

    private EditText nameEditText;
    private EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupDao = new GroupDao(getApplicationContext());
        toaster = new Toaster(this);
        snackbarHelper = new SnackbarHelper(this);

        nameEditText = findView(R.id.nameEditText);
        descriptionEditText = findView(R.id.descriptionEditText);

        readIntent();
        getSupportActionBar().setTitle(actionBarTitle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_group;
    }

    private void readIntent() {
        purpose = requireIntentExtra(ActivityPurpose.TAG);
        if (purpose == ActivityPurpose.EDIT_EXISTING) {
            ParcelableGroup parcelableGroup = requireIntentExtra(Constants.SELECTED_GROUP);
            editedGroup = checkNotNull(parcelableGroup.getGroup());

            nameEditText.setText(editedGroup.getName());
            descriptionEditText.setText(editedGroup.getDescription());
        }
    }

    private int actionBarTitle() {
        switch (purpose) {
            case ActivityPurpose.ADD_NEW_EMPTY:
                return R.string.title_add_group;
            case ActivityPurpose.EDIT_EXISTING:
                return R.string.title_edit_group;
            default:
                throw new IllegalStateException("Invalid purpose: " + purpose);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_entity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGroup() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            snackbarHelper.warn(R.string.error_no_name);
            return;
        }

        String description = descriptionEditText.getText().toString().trim();

		/* Edit an existing group */
        if (purpose == ActivityPurpose.EDIT_EXISTING) {
            Group group = groupDao.getByName(name);
            if (group != null && !group.equals(editedGroup)) {
                snackbarHelper.warn(R.string.error_group_exists);
                return;
            }

            editedGroup.setName(name);
            editedGroup.setDescription(description);
            groupDao.update(editedGroup);

            toaster.show(R.string.toast_group_updated);
        }
        /* Create a new group */
        else {
            if (groupDao.getByName(name) != null) {
                snackbarHelper.warn(R.string.error_group_exists);
                return;
            }
            Group group = new Group(name, description);
            long groupId = groupDao.create(group);

            Intent intent = new Intent();
            intent.putExtra(Constants.GROUP_ID, groupId);
            setResult(RESULT_OK, intent);

            toaster.show(R.string.toast_group_added);
        }

        finish();
    }

}
