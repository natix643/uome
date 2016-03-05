package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.TextView;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.Event;
import cz.pikadorama.uome.common.activity.UomeListActivity;
import cz.pikadorama.uome.common.util.Intents;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.common.view.Animations;
import cz.pikadorama.uome.common.view.AvatarView;
import cz.pikadorama.uome.common.view.SnackbarHelper;
import cz.pikadorama.uome.dialog.ConfirmationDialog;
import cz.pikadorama.uome.model.Balance;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.TransactionDao;

public abstract class PersonDetailActivity extends UomeListActivity implements ConfirmationDialog.Callback {

    private static final String REQUEST_DELETE_PERSON = "deletePerson";
    private static final String REQUEST_DELETE_TRANSACTIONS = "deleteTransactions";

    private TransactionDao transactionDao;
    private PersonDao personDao;

    private SnackbarHelper snackbarHelper;

    private ViewHoldingListAdapter<Transaction> adapter;

    private FloatingActionButton addTransactionButton;

    private AvatarView avatarView;
    private TextView nameTextView;
    private TextView emailTextView;

    protected TextView bottomHintText;
    protected TextView bottomAmountText;

    private TextView descriptionTextView;

    private ActionMode actionMode;

    private Person person;

    // initialized to avoid NPE in onCreateOptionsMenu() which is called before onResume()
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        snackbarHelper = new SnackbarHelper(this);

        transactionDao = new TransactionDao(this);
        personDao = new PersonDao(this);

        initViews();

        adapter = createAdapter();
        setListAdapter(adapter);
        getListView().setMultiChoiceModeListener(actionModeCallback);

        addTransactionButton = requireView(R.id.floatingButton);
        addTransactionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intents.addTransaction(self, person), ActivityRequest.ADD_TRANSACTION);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.person_detail;
    }

    protected abstract ViewHoldingListAdapter<Transaction> createAdapter();

    private void initViews() {
        avatarView = requireView(R.id.avatar);
        nameTextView = requireView(R.id.nameTextView);
        emailTextView = requireView(R.id.emailTextView);
        descriptionTextView = requireView(R.id.descriptionTextView);

        bottomHintText = requireView(R.id.bottomHintText);
        bottomAmountText = requireView(R.id.bottomAmountText);
    }

    private void refreshPerson() {
        avatarView.setPerson(person);
        nameTextView.setText(person.getName());
        emailTextView.setText(person.getEmail());
        descriptionTextView.setText(person.getDescription());
    }

    @Override
    protected void onResume() {
        super.onResume();

        long personId = requireIntentExtra(Constants.PERSON_ID);
        person = personDao.getById(personId);
        refreshPerson();
        refreshTransactions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActivityRequest.EDIT_PERSON:
                    snackbarHelper.info(R.string.toast_person_updated);
                    break;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.person_detail, menu);
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            menu.removeItem(R.id.menu_settle_debt);
            menu.removeItem(R.id.menu_send_email_with_debt);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settle_debt:
                startActivityForResult(Intents.settleDebt(this, getBalance()), ActivityRequest.ADD_TRANSACTION);
                return true;
            case R.id.menu_send_email_with_debt:
                startActivity(Intents.shareViaEmail(getBalance(), this));
                return true;
            case R.id.menu_edit_person:
                startActivityForResult(Intents.editPerson(this, person), ActivityRequest.EDIT_PERSON);
                return true;
            case R.id.menu_delete_person:
                ConfirmationDialog.of(REQUEST_DELETE_PERSON)
                        .setTitle(R.string.dialog_delete_person_title)
                        .setMessage(R.string.dialog_delete_person_message)
                        .setPositiveButton(R.string.delete)
                        .show(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Balance getBalance() {
        return new Balance(person, totalAmount);
    }

    protected ViewHoldingListAdapter<Transaction> getAdapter() {
        return adapter;
    }

    @Override
    public void onConfirmed(String requestCode) {
        switch (requestCode) {
            case REQUEST_DELETE_PERSON:
                deletePerson();
                break;
            case REQUEST_DELETE_TRANSACTIONS:
                deleteSelectedTransactions();
                break;
        }
    }

    private void deletePerson() {
        personDao.delete(person);

        setResult(RESULT_OK, new Intent().putExtra(Event.PERSON_DELETED, true));
        finish();
    }

    private void deleteSelectedTransactions() {
        transactionDao.deleteAll(getSelection());
        actionMode.finish();
        refreshTransactions();

        snackbarHelper.info(R.string.toast_transactions_deleted);
    }

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

    protected void refreshTransactions() {
        List<Transaction> transactions = loadTransactions();
        adapter.setList(transactions);
        refreshTransactionsSummary(transactions);
    }

    protected List<Transaction> loadTransactions() {
        return transactionDao.getAllForPerson(person);
    }

    protected void refreshTransactionsSummary(List<Transaction> transactions) {
        totalAmount = ListViewUtil.sumTransactions(transactions);
        bottomAmountText.setTextColor(ListViewUtil.getAmountColor(this, totalAmount));
        refreshTransactionsSummary(totalAmount);
        invalidateOptionsMenu();
    }

    private final MultiChoiceModeListener actionModeCallback = new MultiChoiceModeListener() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            mode.getMenuInflater().inflate(R.menu.context_list_transactions, menu);
            Animations.collapse(addTransactionButton);
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
                            Intents.settleTransaction(self, transaction),
                            ActivityRequest.ADD_TRANSACTION);
                    mode.finish();
                    return true;
                case R.id.menu_edit:
                    startActivityForResult(
                            Intents.editTransaction(self, transaction),
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
            Animations.expand(addTransactionButton);
        }
    };

    protected abstract void refreshTransactionsSummary(BigDecimal amount);

    protected abstract boolean canEditTransactions();
}
