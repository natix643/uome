package cz.pikadorama.uome.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.activity.ListDebtorsActivity;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.util.TransactionFilter;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.Transaction.Direction;
import cz.pikadorama.uome.model.TransactionDao;

public class NotificationScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int debtAge = intent.getIntExtra(Constants.PREF_DEBT_AGE, Constants.MISSING_EXTRA);
        if (debtAge == Constants.MISSING_EXTRA) {
            return;
        }
        PersonDao personDao = new PersonDao(context);
        TransactionDao transactionDao = new TransactionDao(context);

        ArrayList<Transaction> transactionsToNotify = new ArrayList<>();

        List<Person> persons = personDao.getAllForGroup(Constants.SIMPLE_GROUP_ID);
        for (Person person : persons) {
            List<Transaction> allTransactions = transactionDao.getAllForPerson(person.getId());
            processNonfinancialTransactions(debtAge, transactionsToNotify,
                    TransactionFilter.NON_FINANCIAL.doFilter(allTransactions));
            processFinancialTransactions(debtAge, transactionsToNotify, person,
                    TransactionFilter.FINANCIAL.doFilter(allTransactions));
        }

        if (!transactionsToNotify.isEmpty()) {
            createNotification(context, transactionsToNotify);
        }
    }

    private void processFinancialTransactions(
            int debtAge,
            List<Transaction> transactionsToNotify,
            Person person,
            List<Transaction> financialTransactions) {
        Collections.reverse(financialTransactions);
        Date lastSettledDate = null;
        BigDecimal amount = BigDecimal.ZERO;
        boolean belowZero = false;
        for (Transaction transaction : financialTransactions) {
            if (lastSettledDate == null) {
                lastSettledDate = transaction.getDateTime();
            }
            switch (transaction.getDirection()) {
                case WITHDRAWAL:
                    amount = amount.subtract(new BigDecimal(transaction.getValue()));
                    break;
                case DEPOSIT:
                    amount = amount.add(new BigDecimal(transaction.getValue()));
                    break;
                default:
                    throw new AssertionError();
            }
            if (amount.compareTo(BigDecimal.ZERO) == -1) {
                if (!belowZero) {
                    lastSettledDate = transaction.getDateTime();
                }
                belowZero = true;
            } else {
                if (belowZero) {
                    lastSettledDate = transaction.getDateTime();
                }
                belowZero = false;
            }
        }
        if (lastSettledDate != null && isOldEnoughToNotify(lastSettledDate, debtAge)
                && amount.compareTo(BigDecimal.ZERO) != 0) {
            Transaction transaction = getSummaryTransaction(person, lastSettledDate, amount);
            transactionsToNotify.add(transaction);
        }
    }

    private void processNonfinancialTransactions(
            int debtAge,
            List<Transaction> transactionsToNotify,
            List<Transaction> nonfinancialTransactions) {
        for (Transaction transaction : nonfinancialTransactions) {
            if (isOldEnoughToNotify(transaction.getDateTime(), debtAge)) {
                transactionsToNotify.add(transaction);
            }
        }
    }

    private Transaction getSummaryTransaction(Person person, Date lastSettledDate, BigDecimal amount) {
        return new Transaction(
                System.currentTimeMillis(),
                person.getId(),
                person.getGroupId(),
                getAmountAsString(amount),
                true,
                amount.compareTo(BigDecimal.ZERO) == -1 ? Direction.WITHDRAWAL : Direction.DEPOSIT,
                null,
                lastSettledDate);
    }

    private String getAmountAsString(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == -1) {
            return amount.negate().toPlainString();
        }
        return amount.toPlainString();
    }

    private boolean isOldEnoughToNotify(Date date, int debtAge) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -debtAge);
        return date.getTime() < calendar.getTimeInMillis();
    }

    private void createNotification(Context context, ArrayList<Transaction> transactionsToNotify) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, ListDebtorsActivity.class)
                .putExtra(Transaction.KEY, transactionsToNotify);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setTicker(context.getString(R.string.notification_title))
                .setContentTitle(context.getString(R.string.notification_content_title))
                .setContentText(context.getString(R.string.notification_content_text))
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .getNotification();

        notificationManager.notify(0, notification);
    }
}