package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.GroupDao;

public class AddGroupActivity extends UomeActivity {

    private GroupDao groupDao;

    private Group editedGroup;

    private EditText nameEditText;
    private TextInputLayout nameTextLayout;
    private EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupDao = new GroupDao(this);
        initViews();

        getSupportActionBar().setTitle(actionBarTitle());
        readEditedGroup();
    }

    private void initViews() {
        nameEditText = requireView(R.id.nameEditText);
        nameTextLayout = requireView(R.id.nameTextLayout);
        nameTextLayout.setHint(null);
        Views.autoClearError(nameTextLayout);

        descriptionEditText = requireView(R.id.descriptionEditText);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_group;
    }

    private void readEditedGroup() {
        if (getRequest() == ActivityRequest.EDIT_GROUP) {
            editedGroup = requireIntentExtra(Group.KEY);
            nameEditText.setText(editedGroup.getName());
            descriptionEditText.setText(editedGroup.getDescription());
        }
    }

    private int actionBarTitle() {
        switch (getRequest()) {
            case ActivityRequest.ADD_GROUP:
                return R.string.title_add_group;
            case ActivityRequest.EDIT_GROUP:
                return R.string.title_edit_group;
            default:
                throw new IllegalStateException("Invalid request: " + getRequest());
        }
    }

    private int getRequest() {
        return requireIntentExtra(ActivityRequest.KEY);
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
            nameTextLayout.setError(getString(R.string.error_no_name));
            return;
        }

        String description = descriptionEditText.getText().toString().trim();

        switch (getRequest()) {
            case ActivityRequest.EDIT_GROUP: {
                Group group = groupDao.getByName(name);
                if (group != null && !group.equals(editedGroup)) {
                    nameTextLayout.setError(getString(R.string.error_group_exists));
                    return;
                }

                editedGroup.setName(name);
                editedGroup.setDescription(description);
                groupDao.update(editedGroup);

                setResult(RESULT_OK);
                break;
            }
            case ActivityRequest.ADD_GROUP: {
                if (groupDao.getByName(name) != null) {
                    nameTextLayout.setError(getString(R.string.error_group_exists));
                    return;
                }

                Group group = new Group(name, description);
                long groupId = groupDao.create(group);

                Intent intent = new Intent().putExtra(Constants.GROUP_ID, groupId);
                setResult(RESULT_OK, intent);
                break;
            }
        }

        finish();
    }

}
