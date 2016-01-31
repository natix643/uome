package cz.pikadorama.uome.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.math.BigDecimal;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.activity.AddGroupActivity;
import cz.pikadorama.uome.activity.AddPersonActivity;
import cz.pikadorama.uome.activity.GroupAddTransactionActivity;
import cz.pikadorama.uome.activity.GroupListPersonTransactionsActivity;
import cz.pikadorama.uome.activity.GroupOverviewActivity;
import cz.pikadorama.uome.activity.SimpleAddTransactionActivity;
import cz.pikadorama.uome.activity.SimpleListPersonTransactionsActivity;
import cz.pikadorama.uome.activity.SimpleOverviewActivity;
import cz.pikadorama.uome.common.ActivityPurpose;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.format.EmailFormatter;
import cz.pikadorama.uome.model.Balance;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.Transaction.Direction;
import cz.pikadorama.uome.model.parcelable.ParcelableGroup;
import cz.pikadorama.uome.model.parcelable.ParcelablePerson;
import cz.pikadorama.uome.model.parcelable.TransactionData;

public class Intents {

    private Intents() {}

	/*
     * Adding
	 */

    public static Intent addTransaction(Activity activity, long groupId) {
        return addPrefilledTransaction(activity, addTransactionData(groupId, null));
    }

    public static Intent addTransaction(Activity activity, long groupId, long personId) {
        return addPrefilledTransaction(activity, addTransactionData(groupId, personId));
    }

    public static Intent addTransaction(Activity activity, Person person) {
        return addTransaction(activity, person.getGroupId(), person.getId());
    }

    public static Intent settleDebt(Activity activity, Balance balance) {
        return addPrefilledTransaction(activity, settleDebtData(balance, activity));
    }

    public static Intent settleTransaction(Activity activity, Transaction transaction) {
        return addPrefilledTransaction(activity, settleTransactionData(transaction));
    }

    private static Intent addPrefilledTransaction(Activity activity, TransactionData data) {
        return new Intent(activity, addActivityClass(data.getGroupId()))
                .putExtra(ActivityRequest.KEY, ActivityRequest.ADD_TRANSACTION)
                .putExtra(TransactionData.TAG, data);
    }

    private static TransactionData addTransactionData(Long groupId, Long personId) {
        TransactionData data = new TransactionData();
        data.setGroupId(groupId);
        data.setPersonId(personId);
        return data;
    }

    private static TransactionData settleDebtData(Balance balance, Activity activity) {
        TransactionData data = new TransactionData();
        data.setGroupId(balance.getGroupId());
        data.setPersonId(balance.getPerson().getId());
        data.setFinancial(true);
        data.setValue(balance.getAmount().abs().toPlainString());
        data.setDirection(reverseDirectionFromAmount(balance.getAmount()));
        data.setDescription(activity.getString(R.string.debt_settlement));
        return data;
    }

    private static TransactionData settleTransactionData(Transaction transaction) {
        TransactionData data = TransactionData.from(transaction);
        data.setId(null);
        data.setDateTime(null);
        data.setDirection(reverse(data.getDirection()));
        return data;
    }

    private static Direction reverse(Direction direction) {
        return direction == Direction.DEPOSIT ? Direction.WITHDRAWAL : Direction.DEPOSIT;
    }

    private static Direction reverseDirectionFromAmount(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 ? Direction.WITHDRAWAL : Direction.DEPOSIT;
    }

    public static Intent addPerson(Activity activity, long groupId) {
        return new Intent(activity, AddPersonActivity.class)
                .putExtra(Constants.GROUP_ID, groupId)
                .putExtra(ActivityPurpose.TAG, ActivityPurpose.ADD_NEW_EMPTY);
    }

    public static Intent addGroup(Activity activity) {
        return new Intent(activity, AddGroupActivity.class)
                .putExtra(ActivityPurpose.TAG, ActivityPurpose.ADD_NEW_EMPTY);
    }

	/*
     * Editing
	 */

    public static Intent editTransaction(Activity activity, Transaction transaction) {
        return new Intent(activity, SimpleAddTransactionActivity.class)
                .putExtra(ActivityRequest.KEY, ActivityRequest.EDIT_TRANSACTION)
                .putExtra(TransactionData.TAG, TransactionData.from(transaction));
    }

    public static Intent editPerson(Activity activity, Person person) {
        return new Intent(activity, AddPersonActivity.class)
                .putExtra(ActivityPurpose.TAG, ActivityPurpose.EDIT_EXISTING)
                .putExtra(Constants.SELECTED_PERSON, new ParcelablePerson(person));
    }

    public static Intent editGroup(Activity activity, Group group) {
        return new Intent(activity, AddGroupActivity.class)
                .putExtra(ActivityPurpose.TAG, ActivityPurpose.EDIT_EXISTING)
                .putExtra(Constants.SELECTED_GROUP, new ParcelableGroup(group));
    }

	/*
	 * Misc.
	 */

    public static Intent shareViaEmail(Balance balance, Context context) {
        EmailFormatter formatter = new EmailFormatter(context);
        String[] address = {balance.getPerson().getEmail()};

        return new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, address)
                .putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
                .putExtra(Intent.EXTRA_TEXT, formatter.formatEmail(balance));
    }

    public static Intent listPersonTransactions(Activity activity, Person person) {
        return new Intent(activity, listPersonTransactionsClass(person.getGroupId()))
                .putExtra(Constants.PERSON_ID, person.getId());
    }

    public static Intent openGroup(Activity activity, Group group) {
        return openGroup(activity, group.getId());
    }

    public static Intent openGroup(Activity activity, long groupId) {
        if (groupId == Constants.SIMPLE_GROUP_ID) {
            return new Intent(activity, SimpleOverviewActivity.class);
        } else {
            return new Intent(activity, GroupOverviewActivity.class).putExtra(Constants.GROUP_ID, groupId);
        }
    }

    private static Class<?> addActivityClass(long groupId) {
        return groupId == Constants.SIMPLE_GROUP_ID
                ? SimpleAddTransactionActivity.class
                : GroupAddTransactionActivity.class;
    }

    private static Class<?> listPersonTransactionsClass(long groupId) {
        return groupId == Constants.SIMPLE_GROUP_ID
                ? SimpleListPersonTransactionsActivity.class
                : GroupListPersonTransactionsActivity.class;
    }

}
