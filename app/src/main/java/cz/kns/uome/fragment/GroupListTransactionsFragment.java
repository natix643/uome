package cz.kns.uome.fragment;

import java.util.List;

import android.view.Menu;
import android.view.MenuInflater;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import cz.kns.uome.R;
import cz.kns.uome.adapter.viewholder.TransactionViewHolder;
import cz.kns.uome.common.Constants;
import cz.kns.uome.model.Transaction;

public class GroupListTransactionsFragment extends ListTransactionsFragment {

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.removeItem(R.id.menu_filter);
	}

	@Override
	protected List<Transaction> filter(List<Transaction> transactions) {
		return transactions;
	}

	@Override
	protected long getGroupId() {
		return requireArgument(Constants.GROUP_ID);
	}

	@Override
	protected ViewHoldingListAdapter<Transaction> createAdapter() {
		return TransactionViewHolder.forGroupDebts(getBaseActivity());
	}

	@Override
	protected boolean canEditTransactions() {
		return false;
	}
}
