package cz.pikadorama.uome.common.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.model.Balance;

public enum BalanceCategory {

    // TODO: think about both directions at once
    OWES_ME_MOST {
        @Override
        protected Comparator<Balance> getSortingStrategy() {
            return new HighestDebtsFirst();
        }

        @Override
        protected Predicate<Balance> getFilterStrategy() {
            return new MyDebtorsOnly();
        }

        @Override
        public int widgetTitleResId() {
            return R.string.widget_who_owes_most;
        }

        @Override
        public int widgetItemLayoutResId() {
            return R.layout.item_widget;
        }
    },
    OWES_ME_LONGEST_TIME {
        @Override
        protected Comparator<Balance> getSortingStrategy() {
            return new LongLastingDebtsFirst();
        }

        @Override
        protected Predicate<Balance> getFilterStrategy() {
            return new MyDebtorsOnly();
        }

        @Override
        public int widgetTitleResId() {
            return R.string.widget_who_owes_longest_time;
        }

        @Override
        public int widgetItemLayoutResId() {
            return R.layout.item_widget_with_time;
        }
    },
    I_OWE_MOST {
        @Override
        protected Comparator<Balance> getSortingStrategy() {
            return new HighestDebtsFirst();
        }

        @Override
        protected Predicate<Balance> getFilterStrategy() {
            return new MyDebtsOnly();
        }

        @Override
        public int widgetTitleResId() {
            return R.string.widget_whom_i_owe_most;
        }

        @Override
        public int widgetItemLayoutResId() {
            return R.layout.item_widget;
        }
    },
    I_OWE_LONGEST_TIME {
        @Override
        protected Comparator<Balance> getSortingStrategy() {
            return new LongLastingDebtsFirst();
        }

        @Override
        protected Predicate<Balance> getFilterStrategy() {
            return new MyDebtsOnly();
        }

        @Override
        public int widgetTitleResId() {
            return R.string.widget_whom_i_owe_longest_time;
        }

        @Override
        public int widgetItemLayoutResId() {
            return R.layout.item_widget_with_time;
        }
    };

    /**
     * Return filtered and sorted list of people's debts. The selection is based on the category and size.
     * The list is sorted by the balance amount, highest first.
     *
     * @param balances original list of all balances
     * @param numberOfItemsToShow limit number of items to show in the widget
     * @return filtered and sorted list
     */
    public List<Balance> filterAndSlice(List<Balance> balances, int numberOfItemsToShow) {
        if (balances.isEmpty()) {
            return Collections.emptyList();
        }
        List<Balance> balancesToShow = Lists.newArrayList(Iterables.transform(Iterables.filter(balances, getFilterStrategy()),
                new Absolutize()));
        Collections.sort(balancesToShow, getSortingStrategy());
        return slice(balancesToShow, numberOfItemsToShow);
    }

    private List<Balance> slice(List<Balance> balances, int numberOfItemsToShow) {
        if (numberOfItemsToShow < 0 || balances.size() < numberOfItemsToShow) {
            return balances;
        }
        return balances.subList(0, numberOfItemsToShow);
    }

    public abstract int widgetTitleResId();
    public abstract int widgetItemLayoutResId();

    protected abstract Comparator<Balance> getSortingStrategy();

    protected abstract Predicate<Balance> getFilterStrategy();


    private static final class Absolutize implements Function<Balance, Balance> {

        @Override
        public Balance apply(Balance balance) {
            return Balance.cloneWithAbsoluteAmount(balance);
        }
    }

    private static final class MyDebtsOnly implements Predicate<Balance> {

        @Override
        public boolean apply(Balance balance) {
            return balance.getAmount().compareTo(BigDecimal.ZERO) > 0;
        }
    }

    private static final class MyDebtorsOnly implements Predicate<Balance> {

        @Override
        public boolean apply(Balance balance) {
            return balance.getAmount().compareTo(BigDecimal.ZERO) < 0;
        }
    }

    private static final class HighestDebtsFirst implements Comparator<Balance> {

        @Override
        public int compare(Balance balance1, Balance balance2) {
            return balance2.getAmount().compareTo(balance1.getAmount());
        }
    }

    private static final class LongLastingDebtsFirst implements Comparator<Balance> {

        @Override
        public int compare(Balance balance1, Balance balance2) {
            return balance1.getLastSettleDate().before(balance2.getLastSettleDate()) ? -1 : 1;
        }
    }
}