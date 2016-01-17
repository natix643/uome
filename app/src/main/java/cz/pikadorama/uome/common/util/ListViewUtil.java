package cz.pikadorama.uome.common.util;

import android.content.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            if (transaction.isFinancial()) {
                switch (transaction.getDirection()) {
                    case WITHDRAWAL:
                        sum = sum.subtract(new BigDecimal(transaction.getValue()));
                        break;
                    case DEPOSIT:
                        sum = sum.add(new BigDecimal(transaction.getValue()));
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        return sum.setScale(MoneyFormatter.DECIMAL_PLACES, RoundingMode.DOWN);
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
}
