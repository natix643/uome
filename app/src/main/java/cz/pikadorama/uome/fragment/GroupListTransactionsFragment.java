package cz.pikadorama.uome.fragment;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.util.List;

import cz.pikadorama.uome.adapter.viewholder.TransactionViewHolder;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.model.Transaction;

public class GroupListTransactionsFragment extends ListTransactionsFragment {

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
