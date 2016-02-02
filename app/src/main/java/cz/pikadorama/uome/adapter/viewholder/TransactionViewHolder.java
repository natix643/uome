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
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;

import static cz.pikadorama.uome.model.Transaction.Direction.WITHDRAWAL;

public abstract class TransactionViewHolder implements ViewHolder<Transaction> {

    public static final ViewHoldingListAdapter<Transaction> forSimpleDebts(Context context) {
        return forSimpleDebts(context, new ArrayList<Transaction>());
    }

    public static final ViewHoldingListAdapter<Transaction> forSimpleDebts(Context context,
            List<Transaction> transactions) {
        return new ViewHoldingListAdapter<>(
                transactions,
                ViewInflator.viewInflatorFor(context, R.layout.item_simple_transaction),
                new SimpleTransactionViewHolderFactory());
    }

    public static final ViewHoldingListAdapter<Transaction> forGroupDebts(Context context) {
        return forGroupDebts(context, new ArrayList<Transaction>());
    }

    public static final ViewHoldingListAdapter<Transaction> forGroupDebts(Context context,
            List<Transaction> transactions) {
        return new ViewHoldingListAdapter<>(
                transactions,
                ViewInflator.viewInflatorFor(context, R.layout.item_group_transaction),
                new GroupTransactionViewHolderFactory());
    }

    private final TextView titleTextView;
    private final TextView valueTextView;
    private final TextView descriptionTextView;
    private final TextView dateTextView;
    private final PersonDao personDao;
    private final Context context;
    private final DateTimeFormatter dateTimeFormatter;

    private TransactionViewHolder(View root) {
        this.titleTextView = Views.find(root, R.id.titleTextView);
        this.valueTextView = Views.find(root, R.id.valueTextView);
        this.descriptionTextView = Views.find(root, R.id.descriptionTextView);
        this.dateTextView = Views.find(root, R.id.dateTextView);
        this.context = root.getContext();
        this.personDao = new PersonDao(this.context);
        this.dateTimeFormatter = DateTimeFormatter.showDateTime(this.context).showWeekDay();
    }

    @Override
    public void updateViewFor(Transaction transaction) {
        Person person = personDao.getById(transaction.getPersonId());
        titleTextView.setText(person.getName());

        valueTextView.setText(formatValue(transaction));
        valueTextView.setTextColor(ListViewUtil.getAmountColor(context, getAmountForColoring(transaction)));

        descriptionTextView.setText(transaction.getDescription());

        dateTextView.setText(dateTimeFormatter.format(transaction.getDateTime()));
    }

    protected abstract String formatValue(Transaction transaction);

    protected abstract BigDecimal getAmountForColoring(Transaction transaction);

	/*
     * Concrete subclasses
	 */

    private static final class SimpleTransactionViewHolder extends TransactionViewHolder {

        private final MoneyFormatter moneyFormatter = MoneyFormatter.withoutPlusPrefix();
        private final TextView hintTextView;

        private SimpleTransactionViewHolder(View root) {
            super(root);
            this.hintTextView = Views.find(root, R.id.hintTextView);
        }

        @Override
        public void updateViewFor(Transaction transaction) {
            super.updateViewFor(transaction);
            hintTextView.setText(ListViewUtil.getHint(transaction.getDirection()));
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

    private static final class GroupTransactionViewHolder extends TransactionViewHolder {

        private final MoneyFormatter moneyFormatter = MoneyFormatter.withPlusPrefix();

        private GroupTransactionViewHolder(View root) {
            super(root);
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

    private static final class SimpleTransactionViewHolderFactory implements ViewHolderFactory<Transaction> {

        @Override
        public ViewHolder<Transaction> createViewHolderFor(View view) {
            return new SimpleTransactionViewHolder(view);
        }

        @Override
        public Class<? extends ViewHolder<Transaction>> getHolderClass() {
            return SimpleTransactionViewHolder.class;
        }

    }

    private static final class GroupTransactionViewHolderFactory implements ViewHolderFactory<Transaction> {

        @Override
        public ViewHolder<Transaction> createViewHolderFor(View view) {
            return new GroupTransactionViewHolder(view);
        }

        @Override
        public Class<? extends ViewHolder<Transaction>> getHolderClass() {
            return GroupTransactionViewHolder.class;
        }

    }

}
