package cz.kns.uome.activity;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

import cz.kns.uome.R;
import cz.kns.uome.adapter.viewholder.TransactionViewHolder;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.activity.UomeListActivity;
import cz.kns.uome.common.util.Parcelables;
import cz.kns.uome.model.Transaction;
import cz.kns.uome.model.parcelable.ParcelableTransaction;

public class ListDebtorsActivity extends UomeListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readIntent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list;
    }

    private void readIntent() {
        List<ParcelableTransaction> parcelableTransactions = requireIntentExtra(Constants.SELECTED_TRANSACTIONS);
        List<Transaction> transactions = Parcelables.toTransactions(parcelableTransactions);

        setListAdapter(TransactionViewHolder.forSimpleDebts(this, transactions));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // override the default behaviour when application was closed on home button
        if (item.getItemId() == android.R.id.home) {
            startActivity(StartupActivity.class);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
