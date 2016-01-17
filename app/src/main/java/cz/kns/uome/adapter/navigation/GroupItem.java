package cz.kns.uome.adapter.navigation;

import static com.google.common.base.Preconditions.*;

import java.math.BigDecimal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.kns.uome.R;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.format.MoneyFormatter;
import cz.kns.uome.common.util.ListViewUtil;
import cz.kns.uome.common.util.Views;
import cz.kns.uome.model.Group;
import cz.kns.uome.model.TransactionDao;

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
