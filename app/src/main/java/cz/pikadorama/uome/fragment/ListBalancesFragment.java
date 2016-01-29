package cz.pikadorama.uome.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.viewholder.BalanceViewHolder;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.util.Intents;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.common.util.SnackbarHelper;
import cz.pikadorama.uome.dialog.ConfirmationDialog;
import cz.pikadorama.uome.model.Balance;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.TransactionDao;

public abstract class ListBalancesFragment extends OverviewFragment implements ConfirmationDialog.Callback {

    private static final String REQUEST_DELETE_PERSONS = "deletePersons";

    private PersonDao personDao;
    private TransactionDao transactionDao;

    private SnackbarHelper snackbarHelper;

    private ViewHoldingListAdapter<Balance> adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        personDao = new PersonDao(activity);
        transactionDao = new TransactionDao(activity);

        snackbarHelper = new SnackbarHelper(activity);

        adapter = createAdapter();
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_avatars, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(multiChoiceModeListener);
    }

    @Override
    protected Integer getEmptyTextId() {
        return R.string.empty_list_people;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
        getBaseActivity().invalidateOptionsMenu();
    }

    void refreshAdapter() {
        List<Balance> balances = loadBalances();
        adapter.setList(balances);
    }

    boolean isAdapterEmpty() {
        return adapter.isEmpty();
    }

    private List<Balance> loadBalances() {
        List<Person> persons = personDao.getAllForGroup(getGroupId());
        List<Balance> balances = new ArrayList<>(persons.size());

        for (Person person : persons) {
            List<Transaction> transactions = transactionDao.getAllForPerson(person);
            BigDecimal amount = ListViewUtil.sumTransactions(transactions);
            balances.add(new Balance(person, amount));
        }
        return balances;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Balance balance = adapter.getItem(position);
        startActivity(Intents.listPersonTransactions(getBaseActivity(), balance.getPerson()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_balances, menu);
        if (adapter.isEmpty()) {
            menu.removeItem(R.id.menu_add_transaction);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_person:
                startActivityForResult(Intents.addPerson(getBaseActivity(), getGroupId()), ActivityRequest.ADD_PERSON);
                return true;
            case R.id.menu_add_transaction:
                startActivity(Intents.addTransaction(getBaseActivity(), getGroupId()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ActivityRequest.ADD_PERSON:
                    snackbarHelper.info(R.string.toast_person_added);
                    break;
                case ActivityRequest.EDIT_PERSON:
                    snackbarHelper.info(R.string.toast_person_updated);
                    break;
                case ActivityRequest.ADD_TRANSACTION:
                    snackbarHelper.info(R.string.toast_transaction_added);
                    break;
            }
        }
    }

    // TODO partially duplicate with ListTransactionsFragment => extract somewhere
    private List<Person> getSelection() {
        List<Person> balances = new ArrayList<>();
        SparseBooleanArray positions = getListView().getCheckedItemPositions();

        for (int i = 0; i < positions.size(); i++) {
            if (positions.valueAt(i)) {
                int index = positions.keyAt(i);
                balances.add(adapter.getItem(index).getPerson());
            }
        }
        return balances;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && actionMode != null) {
            actionMode.finish();
        }
    }

    private final MultiChoiceModeListener multiChoiceModeListener = new MultiChoiceModeListener() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            mode.getMenuInflater().inflate(R.menu.context_list_balances, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            boolean single = getListView().getCheckedItemCount() == 1;
            boolean hasDebt = false;

            if (single && !adapter.isEmpty()) {
                SparseBooleanArray positions = getListView().getCheckedItemPositions();
                int index = positions.indexOfValue(true);
                // XXX index can be -1 even if getCheckedItemCount was 1 (doesn't really make sense)
                if (index >= 0) {
                    int position = positions.keyAt(index);
                    Balance balance = adapter.getItem(position);
                    hasDebt = balance.getAmount().compareTo(BigDecimal.ZERO) != 0;
                }
            }

            menu.findItem(R.id.menu_add_transaction).setVisible(single);
            menu.findItem(R.id.menu_settle_debt).setVisible(single && hasDebt);
            menu.findItem(R.id.menu_send_email_with_debt).setVisible(single && hasDebt);
            menu.findItem(R.id.menu_edit).setVisible(single);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            SparseBooleanArray positions = getListView().getCheckedItemPositions();
            int selected = positions.keyAt(positions.indexOfValue(true));
            Balance balance = adapter.getItem(selected);

            switch (item.getItemId()) {
                case R.id.menu_add_transaction:
                    startActivityForResult(
                            Intents.addTransaction(getBaseActivity(), balance.getPerson()),
                            ActivityRequest.ADD_TRANSACTION);
                    mode.finish();
                    return true;
                case R.id.menu_settle_debt:
                    startActivityForResult(
                            Intents.settleDebt(getBaseActivity(), balance),
                            ActivityRequest.ADD_TRANSACTION);
                    mode.finish();
                    return true;
                case R.id.menu_send_email_with_debt:
                    startActivity(Intents.shareViaEmail(balance, getBaseActivity()));
                    return true;
                case R.id.menu_edit:
                    startActivityForResult(
                            Intents.editPerson(getBaseActivity(), balance.getPerson()),
                            ActivityRequest.EDIT_PERSON);
                    mode.finish();
                    return true;
                case R.id.menu_delete:
                    ConfirmationDialog.of(REQUEST_DELETE_PERSONS)
                            .setTitle(R.string.dialog_delete_persons_title)
                            .setMessage(R.string.dialog_delete_persons_message)
                            .setPositiveButton(R.string.delete)
                            .show(self);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int count = getListView().getCheckedItemCount();
            mode.setTitle(Integer.toString(count));
            mode.invalidate();
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    @Override
    public void onConfirmed(String requestCode) {
        if (requestCode.equals(REQUEST_DELETE_PERSONS)) {
            personDao.deleteAll(getSelection());
            actionMode.finish();
            refreshAdapter();
            snackbarHelper.info(R.string.toast_persons_deleted);

            getBaseActivity().invalidateOptionsMenu();

            // TODO remove this hack and use MVC instead
            ListTransactionsFragment other =
                    (ListTransactionsFragment) getBaseActivity().getPagerAdapter().getFragment(1);
            other.refreshAdapter();
        }
    }

    protected abstract long getGroupId();

    protected abstract ViewHoldingListAdapter<Balance> createAdapter();

	/*
     * Concrete subclasses
	 */

    public static class SimpleListBalancesFragment extends ListBalancesFragment {

        @Override
        protected long getGroupId() {
            return Constants.SIMPLE_GROUP_ID;
        }

        @Override
        protected ViewHoldingListAdapter<Balance> createAdapter() {
            return BalanceViewHolder.forSimpleDebts(getBaseActivity());
        }
    }

    public static class GroupListBalancesFragment extends ListBalancesFragment {

        @Override
        protected long getGroupId() {
            return requireArgument(Constants.GROUP_ID);
        }

        @Override
        protected ViewHoldingListAdapter<Balance> createAdapter() {
            return BalanceViewHolder.forGroupDebts(getBaseActivity());
        }
    }

}
