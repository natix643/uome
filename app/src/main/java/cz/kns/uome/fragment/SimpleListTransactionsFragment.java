package cz.kns.uome.fragment;

import static cz.kns.uome.common.util.TransactionFilter.*;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import cz.kns.uome.R;
import cz.kns.uome.adapter.viewholder.TransactionViewHolder;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.util.TransactionFilter;
import cz.kns.uome.model.Transaction;

public class SimpleListTransactionsFragment extends ListTransactionsFragment {

	private static final String KEY_FILTER = "filter";

	private TransactionFilter filter = TransactionFilter.ALL;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			this.filter = (TransactionFilter) savedInstanceState.getSerializable(KEY_FILTER);
			getBaseActivity().invalidateOptionsMenu();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(KEY_FILTER, filter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		filter.prepareOptionsMenu(menu);
		if (getOtherFragment() != null && getOtherFragment().isAdapterEmpty()) {
			menu.removeItem(R.id.menu_filter);
		}
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
		getBaseActivity().invalidateOptionsMenu();
		refreshAdapter();
	}

	@Override
	protected List<Transaction> filter(List<Transaction> transactions) {
		return filter.doFilter(transactions);
	}

	@Override
	protected long getGroupId() {
		return Constants.SIMPLE_GROUP_ID;
	}

	@Override
	protected ViewHoldingListAdapter<Transaction> createAdapter() {
		return TransactionViewHolder.forSimpleDebts(getBaseActivity());
	}

	@Override
	protected boolean canEditTransactions() {
		return true;
	}
}