package cz.pikadorama.uome.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.model.Balance;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.TransactionDao;
import cz.pikadorama.uome.common.util.BalanceCategory;
import cz.pikadorama.uome.widget.config.WidgetPreferences;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private PersonDao personDao;
    private TransactionDao transactionDao;

    private BalanceCategory category;
    private List<Balance> balances;
    private int widgetId;

    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
        this.personDao = new PersonDao(context);
        this.transactionDao = new TransactionDao(context);
        this.widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        loadPeople();
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        loadPeople();
    }

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return balances.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Balance balance = balances.get(position);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), category.widgetItemLayoutResId());
        remoteViews.setTextViewText(R.id.nameText, balance.getPerson().getName());
        remoteViews.setTextViewText(R.id.amountText, MoneyFormatter.withoutPlusPrefix().format(balance.getAmount()));
        if (category.widgetItemLayoutResId() == R.layout.item_widget_with_time) {
            remoteViews.setTextViewText(R.id.debtLength, getUserFriendlyDebtLength(balance.getLastSettleDate()));
        }

        // fill details for the onclick listener (updating the pending intent template
        // set in the WidgetProvider)
        Intent listenerIntent = new Intent();
        listenerIntent.putExtra(Constants.PERSON_ID, balances.get(position).getPerson().getId());
        remoteViews.setOnClickFillInIntent(R.id.widgetItem, listenerIntent);

        return remoteViews;
    }

    private CharSequence getUserFriendlyDebtLength(Date lastSettleDate) {
        return DateUtils.getRelativeTimeSpanString(lastSettleDate.getTime(),
                System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadPeople() {
        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            WidgetPreferences prefs = new WidgetPreferences(context);
            category = prefs.getSavedCategory(widgetId);
            int numberOfItemsToShow = prefs.getSavedLimit(widgetId);

            List<Balance> balancesWithSettleDate = new ArrayList<>();
            List<Person> people = personDao.getAllForGroup(Constants.SIMPLE_GROUP_ID);
            for (Person person : people) {
                List<Transaction> transactions = transactionDao.getAllForPerson(person);
                BigDecimal amount = ListViewUtil.sumTransactions(transactions);
                Date lastSettleDate = ListViewUtil.getLastSettleDate(transactions, category);
                balancesWithSettleDate.add(new Balance(person, amount, lastSettleDate));
            }

            balances = category.filterAndSlice(balancesWithSettleDate, numberOfItemsToShow);
        }
    }
}
