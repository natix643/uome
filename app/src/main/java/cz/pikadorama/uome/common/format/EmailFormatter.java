package cz.pikadorama.uome.common.format;

import android.content.Context;

import java.math.BigDecimal;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.model.Balance;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.Transaction.Direction;
import cz.pikadorama.uome.model.TransactionDao;

public class EmailFormatter {

    private final MoneyFormatter moneyFormatter = MoneyFormatter.withoutPlusPrefix();

    private final Context context;

    private final DateTimeFormatter dateTimeFormatter;
    private final MessageFormatter messageFormatter;

    public EmailFormatter(Context activity) {
        this.context = activity;
        this.dateTimeFormatter = DateTimeFormatter.showDateTime(activity).showWeekDay();
        this.messageFormatter = new MessageFormatter(activity);
    }

    public String formatEmail(Balance balance) {
        StringBuilder sb = new StringBuilder();
        List<Transaction> transactions = new TransactionDao(context).getAllForPerson(balance.getPerson());

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            sb.append("#").append(i + 1).append("\n");
            sb.append(formatTransaction(transaction));
        }

        String totalAmount = moneyFormatter.format(balance.getAmount());
        sb.append(messageFormatter.format(R.string.email_total, totalAmount)).append("\n\n");

        sb.append(context.getText(R.string.email_footer));
        return sb.toString();
    }

    private StringBuilder formatTransaction(Transaction transaction) {
        StringBuilder sb = new StringBuilder();

        int resourceId = transaction.isFinancial() ? R.string.email_amount : R.string.email_item;
        sb.append(messageFormatter.format(resourceId, formatValue(transaction))).append("\n");

        String dateTime = dateTimeFormatter.format(transaction.getDateTime());
        sb.append(messageFormatter.format(R.string.email_datetime, dateTime)).append("\n");

        String description = transaction.getDescription();
        if (!description.isEmpty()) {
            sb.append(messageFormatter.format(R.string.email_description, description)).append("\n");
        }
        sb.append("\n");
        return sb;
    }

    private String formatValue(Transaction transaction) {
        String value = transaction.isFinancial()
                ? moneyFormatter.format(new BigDecimal(transaction.getValue()))
                : transaction.getValue();
        return transaction.getDirection() == Direction.WITHDRAWAL ? "-" + value : value;
    }

}
