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

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.util.Intents;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.common.util.SnackbarHelper;
import cz.pikadorama.uome.dialog.ConfirmationDialog;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.TransactionDao;

public abstract class ListTransactionsFragment extends OverviewFragment implements ConfirmationDialog.Callback {

    private static final String REQUEST_DELETE_TRANSACTIONS = "deleteTransactions";

    private TransactionDao transactionDao;

    private SnackbarHelper snackbarHelper;

    private ViewHoldingListAdapter<Transaction> adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        transactionDao = new TransactionDao(activity);
        snackbarHelper = new SnackbarHelper(activity);

        adapter = createAdapter();
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
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
        return R.string.empty_list_transactions;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdapter();
    }

    void refreshAdapter() {
        List<Transaction> allTransactions = transactionDao.getAllForGroup(getGroupId());
        adapter.setList(filter(allTransactions));

        BigDecimal totalAmount = ListViewUtil.sumTransactions(allTransactions);
        getBaseActivity().refreshTotalAmount(totalAmount);
    }

    protected abstract List<Transaction> filter(List<Transaction> transactions);

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_transactions, menu);
        if (getOtherFragment() != null && getOtherFragment().isAdapterEmpty()) {
            menu.removeItem(R.id.menu_add_transaction);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                case ActivityRequest.ADD_TRANSACTION:
                    snackbarHelper.info(R.string.toast_transaction_added);
                    break;
                case ActivityRequest.EDIT_TRANSACTION:
                    snackbarHelper.info(R.string.toast_transaction_updated);
                    break;
            }
        }
    }

    @Override
    public void onConfirmed(String requestCode) {
        if (requestCode.equals(REQUEST_DELETE_TRANSACTIONS)) {
            transactionDao.deleteAll(getSelection());
            actionMode.finish();
            refreshAdapter();
            snackbarHelper.info(R.string.toast_transactions_deleted);

            getOtherFragment().refreshAdapter();
        }
    }

    // TODO duplicate with ListPersonTransactionsActivity => extract somewhere
    private List<Transaction> getSelection() {
        List<Transaction> transactions = new ArrayList<>();
        SparseBooleanArray positions = getListView().getCheckedItemPositions();

        for (int i = 0; i < positions.size(); i++) {
            if (positions.valueAt(i)) {
                int index = positions.keyAt(i);
                transactions.add(adapter.getItem(index));
            }
        }
        return transactions;
    }

    protected ListBalancesFragment getOtherFragment() {
        // TODO remove this hack and use MVC instead
        return (ListBalancesFragment) getBaseActivity().getPagerAdapter().getFragment(0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && actionMode != null) {
            actionMode.finish();
        }
    }

    // TODO duplicate with ListPersonTransactionsActivity => extract into a separate class
    private final MultiChoiceModeListener multiChoiceModeListener = new MultiChoiceModeListener() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            mode.getMenuInflater().inflate(R.menu.context_list_transactions, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            boolean single = getListView().getCheckedItemCount() == 1;
            menu.findItem(R.id.menu_settle_transaction).setVisible(single);
            menu.findItem(R.id.menu_edit).setVisible(single && canEditTransactions());
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            SparseBooleanArray positions = getListView().getCheckedItemPositions();
            int selected = positions.keyAt(positions.indexOfValue(true));
            Transaction transaction = adapter.getItem(selected);

            switch (item.getItemId()) {
                case R.id.menu_settle_transaction:
                    startActivityForResult(
                            Intents.settleTransaction(getActivity(), transaction),
                            ActivityRequest.ADD_TRANSACTION);
                    mode.finish();
                    return true;
                case R.id.menu_edit:
                    startActivityForResult(
                            Intents.editTransaction(getActivity(), transaction),
                            ActivityRequest.EDIT_TRANSACTION);
                    mode.finish();
                    return true;
                case R.id.menu_delete:
                    ConfirmationDialog.of(REQUEST_DELETE_TRANSACTIONS)
                            .setMessage(R.string.dialog_delete_transactions_message)
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

    protected abstract long getGroupId();

    protected abstract ViewHoldingListAdapter<Transaction> createAdapter();

    protected abstract boolean canEditTransactions();
}
