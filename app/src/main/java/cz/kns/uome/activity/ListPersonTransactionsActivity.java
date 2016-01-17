package cz.kns.uome.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.TextView;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import cz.kns.uome.R;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.activity.UomeListActivity;
import cz.kns.uome.common.util.Intents;
import cz.kns.uome.common.util.ListViewUtil;
import cz.kns.uome.common.util.Toaster;
import cz.kns.uome.common.view.AvatarView;
import cz.kns.uome.dialog.ConfirmationDialog;
import cz.kns.uome.model.Balance;
import cz.kns.uome.model.Person;
import cz.kns.uome.model.PersonDao;
import cz.kns.uome.model.Transaction;
import cz.kns.uome.model.TransactionDao;

public abstract class ListPersonTransactionsActivity extends UomeListActivity implements ConfirmationDialog.Callback {

	private static final String REQUEST_DELETE_PERSON = "deletePerson";
	private static final String REQUEST_DELETE_TRANSACTIONS = "deleteTransactions";

	private TransactionDao transactionDao;
	private PersonDao personDao;

	private Toaster toaster;

	private ViewHoldingListAdapter<Transaction> adapter;

	private AvatarView avatarView;
	private TextView nameTextView;
	private TextView emailTextView;
	private TextView valueTextView;

	private TextView descriptionTextView;

	private ActionMode actionMode;

	private Person person;

	// initialized to avoid NPE in onCreateOptionsMenu() which is called before onResume()
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		toaster = new Toaster(this);

		transactionDao = new TransactionDao(this);
		personDao = new PersonDao(this);

		initPersonBar();

		adapter = createAdapter();
		setListAdapter(adapter);
		getListView().setMultiChoiceModeListener(multiChoiceModeListener);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.list_person_bar;
	}

	protected abstract ViewHoldingListAdapter<Transaction> createAdapter();

	private void initPersonBar() {
		avatarView = requireView(R.id.avatar);
		nameTextView = requireView(R.id.nameTextView);
		emailTextView = requireView(R.id.emailTextView);
		valueTextView = requireView(R.id.valueTextView);

		descriptionTextView = findView(R.id.descriptionTextView);
	}

	private void refreshPerson() {
		nameTextView.setText(person.getName());
		emailTextView.setText(person.getEmail());
		avatarView.setPerson(person);

		if (descriptionTextView != null) {
			descriptionTextView.setText(person.getDescription());
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_person_transactions, menu);
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
				startActivity(Intents.settleDebt(this, getBalance()));
				return true;
			case R.id.menu_add_transaction:
				startActivity(Intents.addTransaction(this, person));
				return true;
			case R.id.menu_send_email_with_debt:
				startActivity(Intents.shareViaEmail(getBalance(), this));
				return true;
			case R.id.menu_edit_person:
				startActivity(Intents.editPerson(this, person));
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

	protected TextView getValueTextView() {
		return valueTextView;
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
		finish();
		toaster.show(R.string.toast_person_deleted);
	}

	private void deleteSelectedTransactions() {
		transactionDao.deleteAll(getSelection());
		actionMode.finish();
		refreshTransactions();
		toaster.show(R.string.toast_transactions_deleted);
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
		valueTextView.setTextColor(ListViewUtil.getAmountColor(this, totalAmount));
		refreshTransactionsSummary(totalAmount);
		invalidateOptionsMenu();
	}

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
					startActivity(Intents.settleTransaction(self, transaction));
					mode.finish();
					return true;
				case R.id.menu_edit:
					startActivity(Intents.editTransaction(self, transaction));
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

	protected abstract void refreshTransactionsSummary(BigDecimal amount);

	protected abstract boolean canEditTransactions();
}
