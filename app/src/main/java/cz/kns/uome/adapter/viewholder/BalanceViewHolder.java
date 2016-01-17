package cz.kns.uome.adapter.viewholder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import cz.kns.uome.R;
import cz.kns.uome.common.format.MoneyFormatter;
import cz.kns.uome.common.util.ListViewUtil;
import cz.kns.uome.common.util.Views;
import cz.kns.uome.common.view.AvatarView;
import cz.kns.uome.model.Balance;
import cz.kns.uome.model.Person;

public abstract class BalanceViewHolder implements ViewHolder<Balance> {

	public static ViewHoldingListAdapter<Balance> forSimpleDebts(Context context) {
		return forSimpleDebts(context, new ArrayList<Balance>());
	}

	public static ViewHoldingListAdapter<Balance> forSimpleDebts(Context context, List<Balance> balances) {
		return new ViewHoldingListAdapter<>(
				balances,
				ViewInflator.viewInflatorFor(context, R.layout.item_balance),
				new SimpleBalanceViewHolderFactory());
	}

	public static ViewHoldingListAdapter<Balance> forGroupDebts(Context context) {
		return forGroupDebts(context, new ArrayList<Balance>());
	}

	public static ViewHoldingListAdapter<Balance> forGroupDebts(Context context, List<Balance> balances) {
		return new ViewHoldingListAdapter<>(
				balances,
				ViewInflator.viewInflatorFor(context, R.layout.item_balance),
				new GroupBalanceViewHolderFactory());
	}

	private final Context context;

	private final AvatarView avatar;
	private final TextView nameText;
	private final TextView emailText;

	protected final TextView amountText;
	protected final TextView directionText;

	private BalanceViewHolder(View root) {
		this.context = root.getContext();

		this.avatar = Views.require(root, R.id.avatar);
		this.nameText = Views.require(root, R.id.nameText);
		this.emailText = Views.require(root, R.id.emailText);
		this.amountText = Views.require(root, R.id.amountText);
		this.directionText = Views.require(root, R.id.directionText);
	}

	@Override
	public void updateViewFor(Balance balance) {
		Person person = balance.getPerson();

		avatar.setPerson(person);

		nameText.setText(person.getName());

		if (!person.getEmail().isEmpty()) {
			emailText.setText(person.getEmail());
			emailText.setVisibility(View.VISIBLE);
		} else {
			emailText.setVisibility(View.GONE);
		}

		amountText.setText(formatAmount(balance));
		amountText.setTextColor(ListViewUtil.getAmountColor(context, balance.getAmount()));
	}

	protected abstract String formatAmount(Balance balance);

	/*
	 * Concrete subclasses
	 */

	private static final class SimpleBalanceViewHolder extends BalanceViewHolder {

		private final MoneyFormatter moneyFormatter = MoneyFormatter.withoutPlusPrefix();

		private SimpleBalanceViewHolder(View root) {
			super(root);
		}

		@Override
		public void updateViewFor(Balance balance) {
			super.updateViewFor(balance);

			if (balance.getAmount().compareTo(BigDecimal.ZERO) == 0) {
				amountText.setVisibility(View.GONE);
			} else {
				amountText.setVisibility(View.VISIBLE);
			}

			directionText.setText(ListViewUtil.getHintForBalance(balance.getAmount()));
		}

		@Override
		protected String formatAmount(Balance balance) {
			return moneyFormatter.format(balance.getAmount().abs());
		}
	}

	private static final class GroupBalanceViewHolder extends BalanceViewHolder {

		private final MoneyFormatter moneyFormatter = MoneyFormatter.withPlusPrefix();

		private GroupBalanceViewHolder(View root) {
			super(root);
		}

		@Override
		public void updateViewFor(Balance balance) {
			super.updateViewFor(balance);
			directionText.setVisibility(View.GONE);
		}

		@Override
		protected String formatAmount(Balance balance) {
			return moneyFormatter.format(balance.getAmount());
		}
	}

	private static final class SimpleBalanceViewHolderFactory implements ViewHolderFactory<Balance> {

		@Override
		public ViewHolder<Balance> createViewHolderFor(View view) {
			return new SimpleBalanceViewHolder(view);
		}

		@Override
		public Class<? extends ViewHolder<Balance>> getHolderClass() {
			return SimpleBalanceViewHolder.class;
		}

	}

	private static final class GroupBalanceViewHolderFactory implements ViewHolderFactory<Balance> {

		@Override
		public ViewHolder<Balance> createViewHolderFor(View view) {
			return new GroupBalanceViewHolder(view);
		}

		@Override
		public Class<? extends ViewHolder<Balance>> getHolderClass() {
			return GroupBalanceViewHolder.class;
		}

	}

}
