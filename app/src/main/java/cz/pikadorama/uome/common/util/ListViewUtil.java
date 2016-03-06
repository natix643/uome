package cz.pikadorama.uome.common.util;

import android.content.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.Transaction.Direction;

public class ListViewUtil {

    private ListViewUtil() {}

    public static int getAmountColor(Context context, BigDecimal amount) {
        int colorId = getAmountColorId(amount);
        return context.getResources().getColor(colorId);
    }

    private static int getAmountColorId(BigDecimal amount) {
        switch (amount.compareTo(BigDecimal.ZERO)) {
            case -1:
                return R.color.negative_amount;
            case 0:
                return R.color.zero_amount;
            case 1:
                return R.color.positive_amount;
            default:
                throw new AssertionError();
        }
    }

    public static BigDecimal sumTransactions(List<Transaction> transactions) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            sum = addTransactionAmountToSum(sum, transaction);
        }
        return sum.setScale(MoneyFormatter.DECIMAL_PLACES, RoundingMode.DOWN);
    }

    private static BigDecimal addTransactionAmountToSum(BigDecimal sum, Transaction transaction) {
        if (transaction.isFinancial()) {
            switch (transaction.getDirection()) {
                case WITHDRAWAL:
                    return sum.subtract(new BigDecimal(transaction.getValue()));
                case DEPOSIT:
                    return sum.add(new BigDecimal(transaction.getValue()));
                default:
                    throw new AssertionError();
            }
        }
        return sum;
    }

    public static int getHintForBalance(BigDecimal value) {
        switch (value.compareTo(BigDecimal.ZERO)) {
            case -1:
                return R.string.amount_you_lent;
            case 0:
                return R.string.amount_no_debt;
            case 1:
                return R.string.amount_you_owe;
            default:
                throw new AssertionError();
        }
    }

    public static int getHint(Direction direction) {
        switch (direction) {
            case DEPOSIT:
                return R.string.amount_gave;
            case WITHDRAWAL:
                return R.string.amount_received;
            default:
                throw new AssertionError();
        }
    }

    /**
     * Get the last known date where balance was zero or it moved between negative and positive numbers.
     *
     * @param transactions list of all transactions sorted by date (newest first)
     * @param category list view category - decides which kind of debts we want to show
     * @return last known date where balance was zero or it moved between negative and positive numbers
     */
    public static Date getLastSettleDate(List<Transaction> transactions, BalanceCategory category) {
        if (transactions.size() == 1) {
            return transactions.get(0).getDateTime();
        }

        Date lastSettleDate = new Date(0L);
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            BigDecimal newBalance = addTransactionAmountToSum(balance, transaction);
            if (newBalance.equals(BigDecimal.ZERO)) {
                lastSettleDate = transaction.getDateTime();
            } else {
                switch (category) {
                    case I_OWE_LONGEST_TIME:
                    case I_OWE_MOST:
                        // we count settle date when going from 'I owe' to 'they owe'
                        if (balance.compareTo(BigDecimal.ZERO) > 0 && newBalance.compareTo(BigDecimal.ZERO) < 0) {
                            lastSettleDate = transaction.getDateTime();
                        }
                        break;
                    case OWES_ME_LONGEST_TIME:
                    case OWES_ME_MOST:
                        // the other way around in comparison to the above
                        if (balance.compareTo(BigDecimal.ZERO) < 0 && newBalance.compareTo(BigDecimal.ZERO) > 0) {
                            lastSettleDate = transaction.getDateTime();
                        }
                        break;
                }
            }
            balance = newBalance;
        }
        return lastSettleDate;
    }
}
