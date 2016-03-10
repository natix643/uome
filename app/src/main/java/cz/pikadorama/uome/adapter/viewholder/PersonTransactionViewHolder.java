package cz.pikadorama.uome.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.format.DateTimeFormatter;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.model.Transaction;

import static cz.pikadorama.uome.model.Transaction.Direction.WITHDRAWAL;

public abstract class PersonTransactionViewHolder implements ViewHolder<Transaction> {

    public static ViewHoldingListAdapter<Transaction> forSimpleDebts(Context context) {
        return forSimpleDebts(context, new ArrayList<Transaction>());
    }

    public static ViewHoldingListAdapter<Transaction> forSimpleDebts(Context context, List<Transaction> transactions) {
        return new ViewHoldingListAdapter<>(
                transactions,
                ViewInflator.viewInflatorFor(context, R.layout.item_person_transaction),
                new SimplePersonTransactionViewHolderFactory());
    }

    public static ViewHoldingListAdapter<Transaction> forGroupDebts(Context context) {
        return forGroupDebts(context, new ArrayList<Transaction>());
    }

    public static ViewHoldingListAdapter<Transaction> forGroupDebts(Context context, List<Transaction> transactions) {
        return new ViewHoldingListAdapter<>(
                transactions,
                ViewInflator.viewInflatorFor(context, R.layout.item_person_transaction),
                new GroupPersonTransactionViewHolderFactory());
    }

    private final TextView titleTextView;
    private final TextView valueTextView;
    private final TextView descriptionTextView;
    private final TextView dateTextView;
    private final Context context;
    private final DateTimeFormatter dateTimeFormatter;

    private PersonTransactionViewHolder(View root) {
        this.titleTextView = Views.find(root, R.id.titleTextView);
        this.valueTextView = Views.find(root, R.id.valueTextView);
        this.descriptionTextView = Views.find(root, R.id.descriptionTextView);
        this.dateTextView = Views.find(root, R.id.dateTextView);
        this.context = root.getContext();
        this.dateTimeFormatter = DateTimeFormatter.showDateTime(this.context).showWeekDay();
    }

    @Override
    public void updateViewFor(Transaction transaction) {
        titleTextView.setText(getTitleId(transaction));

        valueTextView.setText(formatValue(transaction));
        valueTextView.setTextColor(ListViewUtil.getAmountColor(context, getAmountForColoring(transaction)));

        descriptionTextView.setText(transaction.getDescription());

        dateTextView.setText(dateTimeFormatter.format(transaction.getDateTime()));

    }

    protected abstract int getTitleId(Transaction transaction);

    protected abstract String formatValue(Transaction transaction);

    protected abstract BigDecimal getAmountForColoring(Transaction transaction);

	/*
     * Concrete subclasses
	 */

    private static class SimplePersonTransactionViewHolder extends PersonTransactionViewHolder {

        private final MoneyFormatter moneyFormatter = MoneyFormatter.withoutPlusPrefix();

        private SimplePersonTransactionViewHolder(View root) {
            super(root);
        }

        @Override
        protected int getTitleId(Transaction transaction) {
            return transaction.getDirection() == WITHDRAWAL ? R.string.amount_received : R.string.amount_gave;
        }

        @Override
        protected String formatValue(Transaction transaction) {
            if (transaction.isFinancial()) {
                return moneyFormatter.format(new BigDecimal(transaction.getValue()));
            } else {
                return transaction.getValue();
            }
        }

        @Override
        protected BigDecimal getAmountForColoring(Transaction transaction) {
            return transaction.getDirection() == WITHDRAWAL ? Constants.MINUS_ONE : BigDecimal.ONE;
        }
    }

    private static class GroupPersonTransactionViewHolder extends PersonTransactionViewHolder {

        private final MoneyFormatter moneyFormatter = MoneyFormatter.withPlusPrefix();

        private GroupPersonTransactionViewHolder(View root) {
            super(root);
        }

        @Override
        protected int getTitleId(Transaction transaction) {
            return transaction.getDirection() == WITHDRAWAL ? R.string.withdrawal : R.string.deposit;
        }

        @Override
        protected String formatValue(Transaction transaction) {
            return moneyFormatter.format(getAmountForColoring(transaction));
        }

        @Override
        protected BigDecimal getAmountForColoring(Transaction transaction) {
            BigDecimal absAmount = new BigDecimal(transaction.getValue());
            return transaction.getDirection() == WITHDRAWAL ? absAmount.negate() : absAmount;
        }
    }

    private static final class SimplePersonTransactionViewHolderFactory implements ViewHolderFactory<Transaction> {

        @Override
        public ViewHolder<Transaction> createViewHolderFor(View view) {
            return new SimplePersonTransactionViewHolder(view);
        }

        @Override
        public Class<? extends ViewHolder<Transaction>> getHolderClass() {
            return SimplePersonTransactionViewHolder.class;
        }

    }

    private static final class GroupPersonTransactionViewHolderFactory implements ViewHolderFactory<Transaction> {

        @Override
        public ViewHolder<Transaction> createViewHolderFor(View view) {
            return new GroupPersonTransactionViewHolder(view);
        }

        @Override
        public Class<? extends ViewHolder<Transaction>> getHolderClass() {
            return GroupPersonTransactionViewHolder.class;
        }

    }

}
