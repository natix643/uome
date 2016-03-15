package cz.pikadorama.uome.activity;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.viewholder.TransactionViewHolder;
import cz.pikadorama.uome.common.activity.UomeListActivity;
import cz.pikadorama.uome.model.Transaction;

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
        List<Transaction> transactions = requireIntentExtra(Transaction.KEY);
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
