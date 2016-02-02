package cz.pikadorama.uome.adapter.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.TransactionDao;

import static com.google.common.base.Preconditions.checkNotNull;

public class GroupItem extends NavigationItem {

    private final MoneyFormatter moneyFormatter = MoneyFormatter.withPlusPrefix();

    private final Group group;

    public GroupItem(Group group, NavigationListener listener) {
        super(listener);
        this.group = checkNotNull(group);
    }

    @Override
    public View getView(Context context, ViewGroup parent) {
        View root = LayoutInflater.from(context).inflate(R.layout.item_drawer_group, parent, false);

        TextView titleTextView = Views.require(root, R.id.titleTextView);
        if (group.getId() == Constants.SIMPLE_GROUP_ID) {
            titleTextView.setText(R.string.simple_debts_name);
        } else {
            titleTextView.setText(group.getName());
        }

        TransactionDao transactionDao = new TransactionDao(context);
        BigDecimal totalAmount = ListViewUtil.sumTransactions(transactionDao.getAllForGroup(group));

        TextView amountTextView = Views.require(root, R.id.valueTextView);
        amountTextView.setText(moneyFormatter.format(totalAmount));
        amountTextView.setTextColor(ListViewUtil.getAmountColor(context, totalAmount));

        return root;
    }

    public Group getGroup() {
        return group;
    }

}
