package cz.pikadorama.uome.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.SortedSet;
import java.util.TreeSet;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.util.Closeables;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.dialog.SelectEmailDialog;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.parcelable.ParcelablePerson;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddPersonActivity extends UomeActivity implements SelectEmailDialog.Callback {

    private static final int SELECT_CONTACT = 0;

    private PersonDao personDao;

    private Person editedPerson;

    private long groupId;
    private int purpose;

    private EditText nameEditText;
    private TextInputLayout nameTextLayout;

    private EditText emailEditText;
    private EditText descriptionEditText;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personDao = new PersonDao(getApplicationContext());

        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable(Constants.IMAGE_URI);
        }

        initViews();
        readIntent();
        getSupportActionBar().setTitle(actionBarTitle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_person;
    }

    private void initViews() {
        nameEditText = requireView(R.id.nameEditText);
        nameTextLayout = requireView(R.id.nameTextLayout);
        nameTextLayout.setHint(null);
        Views.autoClearError(nameTextLayout);

        emailEditText = requireView(R.id.emailEditText);
        descriptionEditText = requireView(R.id.descriptionEditText);
    }

    private void readIntent() {
        purpose = requireIntentExtra(ActivityRequest.KEY);
        switch (purpose) {
            case ActivityRequest.EDIT_PERSON: {
                ParcelablePerson parcelablePerson = requireIntentExtra(Constants.SELECTED_PERSON);
                editedPerson = checkNotNull(parcelablePerson.getPerson());

                nameEditText.setText(editedPerson.getName());
                emailEditText.setText(editedPerson.getEmail());
                descriptionEditText.setText(editedPerson.getDescription());
                if (editedPerson.getImageUri() != null) {
                    imageUri = Uri.parse(editedPerson.getImageUri());
                }
                break;
            }
            case ActivityRequest.ADD_PERSON: {
                groupId = requireIntentExtra(Constants.GROUP_ID);
                break;
            }
            default:
                throw new IllegalStateException("Illegal purpose: " + purpose);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    searchForContact();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, SELECT_CONTACT);
                }
                return true;
            case R.id.menu_save:
                savePerson();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            searchForContact();
        }
    }

    private void searchForContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        startActivityForResult(intent, SELECT_CONTACT);
    }

    private void savePerson() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            nameTextLayout.setError(getString(R.string.error_no_name));
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

		/* Edit an existing person */
        if (purpose == ActivityRequest.EDIT_PERSON) {
            Person person = personDao.getByNameForGroup(name, editedPerson.getGroupId());
            if (person != null && !person.equals(editedPerson)) {
                nameTextLayout.setError(getString(R.string.error_person_exists_in_group));
                return;
            }
            editedPerson.setName(name);
            editedPerson.setEmail(email);
            editedPerson.setDescription(description);
            editedPerson.setImageUri(imageUri == null ? "" : imageUri.toString());
            personDao.update(editedPerson);
        }
        /* Create a new person */
        else {
            if (personDao.getByNameForGroup(name, groupId) != null) {
                nameTextLayout.setError(getString(R.string.error_person_exists_in_group));
                return;
            }
            Person person = new Person(
                    groupId, name, email, (imageUri == null ? "" : imageUri.toString()), description);
            personDao.create(person);
        }

        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_CONTACT:
                    Uri uri = data.getData();
                    imageUri = getImageUri(uri);
                    nameEditText.setText(getName(uri));

                    SortedSet<String> emails = getEmails(uri);
                    switch (emails.size()) {
                        case 0:
                            emailEditText.setText("");
                            break;
                        case 1:
                            emailEditText.setText(emails.first());
                            break;
                        default:
                            SelectEmailDialog.with(emails).show(this);
                            break;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown request code: " + requestCode);
            }
        }
    }

    private SortedSet<String> getEmails(Uri uri) {
        SortedSet<String> emails = new TreeSet<>();
        Cursor cursor = null;
        try {
            String whereClause = Email.CONTACT_ID + " = ?";
            String[] whereArgs = {uri.getLastPathSegment()};
            cursor = getContentResolver().query(Email.CONTENT_URI, null, whereClause, whereArgs,
                    null);

            int emailColumn = cursor.getColumnIndex(Email.DATA);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                emails.add(cursor.getString(emailColumn));
            }
        } finally {
            Closeables.close(cursor);
        }
        return emails;
    }

    private String getName(Uri uri) {
        String name = "";
        Cursor cursor = managedQuery(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
        }
        return name;
    }

    private Uri getImageUri(Uri uri) {
        Cursor cursor = null;
        try {
            String whereClause = Data.CONTACT_ID + " = ? and " + Data.MIMETYPE + " = '"
                    + Photo.CONTENT_ITEM_TYPE + "'";
            String[] whereArgs = {uri.getLastPathSegment()};
            cursor = getContentResolver().query(Data.CONTENT_URI, null, whereClause, whereArgs,
                    null);
            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null;
            }
        } finally {
            Closeables.close(cursor);
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                Long.parseLong(uri.getLastPathSegment()));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    @Override
    public void onEmailSelected(String item) {
        emailEditText.setText(item);
    }

    private int actionBarTitle() {
        switch (purpose) {
            case ActivityRequest.ADD_PERSON:
                return R.string.title_add_person;
            case ActivityRequest.EDIT_PERSON:
                return R.string.title_edit_person;
            default:
                throw new IllegalStateException("Invalid purpose:" + purpose);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.IMAGE_URI, imageUri);
    }
}
