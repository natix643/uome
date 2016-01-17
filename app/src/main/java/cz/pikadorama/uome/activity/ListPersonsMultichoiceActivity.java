package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.viewholder.PersonMultichoiceViewHolder;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeListActivity;
import cz.pikadorama.uome.common.util.Parcelables;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.parcelable.ParcelablePerson;

public class ListPersonsMultichoiceActivity extends UomeListActivity {

    private ViewHoldingListAdapter<Person> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PersonDao personDao = new PersonDao(this);

        long groupId = requireIntentExtra(Constants.GROUP_ID);
        List<Person> persons = personDao.getAllForGroup(groupId);
        adapter = PersonMultichoiceViewHolder.createAdapter(this, persons);
        setListAdapter(adapter);

        loadSelection();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_persons_multichoice;
    }

    @Override
    protected Integer getEmptyTextId() {
        return R.string.empty_list_people;
    }

    private void loadSelection() {
        List<ParcelablePerson> parcelables = getIntent().getParcelableArrayListExtra(Constants.SELECTED_PERSONS);
        if (parcelables != null) {
            Set<Person> selection = new HashSet<>(Parcelables.toPersons(parcelables));
            ListView listView = getListView();
            for (int i = 0; i < listView.getCount(); i++) {
                if (selection.contains(adapter.getItem(i))) {
                    listView.setItemChecked(i, true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_persons_multichoice, menu);
        MenuItem flipSelection = menu.findItem(R.id.menu_flip_selection);

        int checkedCount = getListView().getCheckedItemCount();

        flipSelection.setTitle(checkedCount < getListView().getCount()
                ? R.string.menu_select_all : R.string.menu_select_none);
        getSupportActionBar().setTitle(Integer.toString(checkedCount));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_flip_selection:
                flipSelection();
                return true;
            case R.id.menu_done:
                selectionDone();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void flipSelection() {
        if (getListView().getCheckedItemCount() < getListView().getCount()) {
            checkAll(true);
        } else {
            checkAll(false);
        }
        invalidateOptionsMenu();
    }

    private void checkAll(boolean checked) {
        ListView listView = getListView();
        for (int i = 0; i < listView.getCount(); i++) {
            listView.setItemChecked(i, checked);
        }
    }

    private void selectionDone() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constants.SELECTED_PERSONS, getSelectedPersons());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        invalidateOptionsMenu();
    }

    private ArrayList<ParcelablePerson> getSelectedPersons() {
        ArrayList<ParcelablePerson> persons = new ArrayList<>();
        ListView listView = getListView();

        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                persons.add(new ParcelablePerson(adapter.getItem(i)));
            }
        }
        return persons;
    }
}
