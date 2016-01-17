package cz.kns.uome.activity;

import static cz.kns.uome.common.util.TransactionFilter.ALL;
import static cz.kns.uome.common.util.TransactionFilter.FINANCIAL;
import static cz.kns.uome.common.util.TransactionFilter.NON_FINANCIAL;

import java.math.BigDecimal;
import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import cz.kns.uome.R;
import cz.kns.uome.adapter.viewholder.PersonTransactionViewHolder;
import cz.kns.uome.common.format.MoneyFormatter;
import cz.kns.uome.common.util.ListViewUtil;
import cz.kns.uome.common.util.TransactionFilter;
import cz.kns.uome.model.Transaction;

public class SimpleListPersonTransactionsActivity extends ListPersonTransactionsActivity {

	private static final String KEY_FILTER = "filter";

	private final MoneyFormatter moneyFormatter = MoneyFormatter.withoutPlusPrefix();

	private TransactionFilter filter = TransactionFilter.ALL;

	private TextView hintTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hintTextView = findView(R.id.hintTextView);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		this.filter = (TransactionFilter) savedInstanceState.getSerializable(KEY_FILTER);
		invalidateOptionsMenu();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(KEY_FILTER, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		filter.prepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_filter_all:
				selectFilter(ALL);
				return true;
			case R.id.menu_filter_financial:
				selectFilter(FINANCIAL);
				return true;
			case R.id.menu_filter_non_financial:
				selectFilter(NON_FINANCIAL);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void selectFilter(TransactionFilter newFilter) {
		this.filter = newFilter;
		invalidateOptionsMenu();
		refreshTransactions();
	}

	@Override
	protected ViewHoldingListAdapter<Transaction> createAdapter() {
		return PersonTransactionViewHolder.forSimpleDebts(this);
	}

	@Override
	protected void refreshTransactions() {
		List<Transaction> allTransactions = loadTransactions();
		getAdapter().setList(filter.doFilter(allTransactions));
		refreshTransactionsSummary(allTransactions);
	}

	@Override
	protected void refreshTransactionsSummary(BigDecimal amount) {
		getValueTextView().setText(moneyFormatter.format(amount.abs()));
		hintTextView.setText(ListViewUtil.getHintForBalance(amount));
	}

	@Override
	protected boolean canEditTransactions() {
		return true;
	}

}
