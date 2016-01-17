package cz.pikadorama.uome.activity;

import android.view.Menu;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.math.BigDecimal;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.viewholder.PersonTransactionViewHolder;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.model.Transaction;

public class GroupListPersonTransactionsActivity extends ListPersonTransactionsActivity {

    private final MoneyFormatter moneyFormatter = MoneyFormatter.withPlusPrefix();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.removeItem(R.id.menu_filter);
        return true;
    }

    @Override
    protected ViewHoldingListAdapter<Transaction> createAdapter() {
        return PersonTransactionViewHolder.forGroupDebts(this);
    }

    @Override
    protected void refreshTransactionsSummary(BigDecimal amount) {
        getValueTextView().setText(moneyFormatter.format(amount));
    }

    @Override
    protected boolean canEditTransactions() {
        return false;
    }

}
