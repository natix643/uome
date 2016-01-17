package cz.kns.uome.common.util;

import android.view.Menu;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.List;

import cz.kns.uome.R;
import cz.kns.uome.model.Transaction;

public enum TransactionFilter implements Predicate<Transaction> {

    ALL {
        @Override
        public boolean apply(Transaction transaction) {
            return true;
        }

        @Override
        public List<Transaction> doFilter(List<Transaction> transactions) {
            return transactions;
        }

        @Override
        public void prepareOptionsMenu(Menu menu) {
            menu.findItem(R.id.menu_filter_all).setChecked(true);
        }
    },
    FINANCIAL {
        @Override
        public boolean apply(Transaction transaction) {
            return transaction.isFinancial();
        }

        @Override
        public void prepareOptionsMenu(Menu menu) {
            menu.findItem(R.id.menu_filter_financial).setChecked(true);
            menu.findItem(R.id.menu_filter).setIcon(R.drawable.ic_action_filter_on).setChecked(true);
        }
    },
    NON_FINANCIAL {
        @Override
        public boolean apply(Transaction transaction) {
            return !transaction.isFinancial();
        }

        @Override
        public void prepareOptionsMenu(Menu menu) {
            menu.findItem(R.id.menu_filter_non_financial).setChecked(true);
            menu.findItem(R.id.menu_filter).setIcon(R.drawable.ic_action_filter_on).setChecked(true);
        }
    };

    public List<Transaction> doFilter(List<Transaction> transactions) {
        return new ArrayList<>(Collections2.filter(transactions, this));
    }

    public abstract void prepareOptionsMenu(Menu menu);
}
