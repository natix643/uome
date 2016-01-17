package cz.kns.uome.activity;

import android.view.Menu;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.math.BigDecimal;

import cz.kns.uome.R;
import cz.kns.uome.adapter.viewholder.PersonTransactionViewHolder;
import cz.kns.uome.common.format.MoneyFormatter;
import cz.kns.uome.model.Transaction;

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
