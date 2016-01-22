package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.ActivityPurpose;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.util.Parcelables;
import cz.pikadorama.uome.common.util.SnackbarHelper;
import cz.pikadorama.uome.common.util.Toaster;
import cz.pikadorama.uome.common.view.DateTimePicker;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.Transaction.Direction;
import cz.pikadorama.uome.model.TransactionDao;
import cz.pikadorama.uome.model.parcelable.ParcelablePerson;
import cz.pikadorama.uome.model.parcelable.TransactionData;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.newArrayList;

public class GroupAddTransactionActivity extends UomeActivity implements DateTimePicker.Holder {

    private static final int REQUEST_SELECT_PERSONS = 3;

    private PersonDao personDao;
    private TransactionDao transactionDao;

    private Toaster toaster;
    private SnackbarHelper snackbarHelper;

    private RadioGroup directionRadioGroup;
    private RadioButton withdrawalRadio;
    private RadioButton depositRadio;

    private TextView personMultiPicker;

    private EditText amountEditText;
    private DateTimePicker dateTimePicker;
    private EditText descriptionEditText;

    /**
     * invariant: can never be null, only empty
     */
    private List<Person> selectedPersons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toaster = new Toaster(this);
        snackbarHelper = new SnackbarHelper(this);

        initDaos();
        initViews();

        loadIntent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.group_add_transaction;
    }

    private void initDaos() {
        personDao = new PersonDao(getApplicationContext());
        transactionDao = new TransactionDao(getApplicationContext());
    }

    private void initViews() {
        directionRadioGroup = findView(R.id.typeRadioGroup);
        depositRadio = findView(R.id.depositRadioButton);
        withdrawalRadio = findView(R.id.withdrawalRadioButton);

        amountEditText = findView(R.id.amountEditText);
        dateTimePicker = findView(R.id.dateTimePicker);
        descriptionEditText = findView(R.id.descriptionEditText);

        personMultiPicker = findView(R.id.personMultiPicker);
        personMultiPicker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(self, ListPersonsMultichoiceActivity.class)
                        .putExtra(Constants.GROUP_ID, getGroupId())
                        .putParcelableArrayListExtra(Constants.SELECTED_PERSONS,
                                Parcelables.fromPersons(selectedPersons));
                startActivityForResult(intent, REQUEST_SELECT_PERSONS);
            }
        });
    }

    private long getGroupId() {
        TransactionData data = requireIntentExtra(TransactionData.TAG);
        return data.getGroupId();
    }

    private void loadIntent() {
        int purpose = requireIntentExtra(ActivityPurpose.TAG);
        checkState(purpose == ActivityPurpose.ADD_NEW_PREFILLED, "Illegal purpose: %s", purpose);

        TransactionData data = requireIntentExtra(TransactionData.TAG);

        Long personId = data.getPersonId();
        if (personId != null) {
            Person person = checkNotNull(personDao.getById(personId));
            selectPersons(newArrayList(person));
            personMultiPicker.setEnabled(false);
        }

        Direction direction = data.getDirection();
        if (direction != null) {
            checkDirectionRadio(direction);
        }

        amountEditText.setText(data.getValue());
        descriptionEditText.setText(data.getDescription());
    }

    private void checkDirectionRadio(Direction direction) {
        RadioButton radio = direction == Direction.DEPOSIT ? depositRadio : withdrawalRadio;
        radio.setChecked(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.SELECTED_PERSONS, Parcelables.fromPersons(selectedPersons));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<ParcelablePerson> parcelablePersons =
                savedInstanceState.getParcelableArrayList(Constants.SELECTED_PERSONS);
        selectPersons(Parcelables.toPersons(parcelablePersons));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            checkState(requestCode == REQUEST_SELECT_PERSONS, "Illegal request code: %s", requestCode);

            ArrayList<ParcelablePerson> parcelablePersons =
                    data.getExtras().getParcelableArrayList(Constants.SELECTED_PERSONS);
            selectPersons(Parcelables.toPersons(parcelablePersons));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSelectedPersons();
    }

    private void selectPersons(List<Person> newPersons) {
        checkNotNull(newPersons);
        if (!selectedPersons.equals(newPersons)) {
            selectedPersons = newPersons;
            refreshSelectedPersons();
        }
    }

    private void refreshSelectedPersons() {
        if (!selectedPersons.isEmpty()) {
            List<String> personNames = Lists.transform(selectedPersons, getPersonName);
            personMultiPicker.setText(Joiner.on(", ").join(personNames));
        } else {
            personMultiPicker.setText("");
        }
    }

    private static final Function<Person, String> getPersonName = new Function<Person, String>() {
        @Override
        public String apply(Person person) {
            char noBreakSpace = '\u00A0';
            return person.getName().replace(' ', noBreakSpace);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_entity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveTransaction() {
        if (selectedPersons.isEmpty()) {
            snackbarHelper.warn(R.string.error_no_people);
            return;
        }

        BigDecimal amount = getAmount();
        if (amount == null) {
            snackbarHelper.warn(R.string.error_no_amount);
            return;
        }

        boolean financial = true;
        String description = descriptionEditText.getText().toString().trim();
        Date dateTime = dateTimePicker.getDateTime();

        // shuffle persons - to avoid that the last one always pays the diff
        Collections.shuffle(selectedPersons);
        for (int i = 0; i < selectedPersons.size(); i++) {
            BigDecimal dividedAmount = amount.divide(new BigDecimal(selectedPersons.size() - i),
                    MoneyFormatter.DECIMAL_PLACES, RoundingMode.DOWN);

            Person person = selectedPersons.get(i);

            Transaction transaction = new Transaction(person.getId(), getGroupId(), dividedAmount.toPlainString(),
                    financial, getDirection(), description, dateTime);
            transactionDao.create(transaction);
            amount = amount.subtract(dividedAmount);
        }

        toaster.show(R.string.toast_transaction_added);
        finish();
    }

    private BigDecimal getAmount() {
        try {
            String amountText = amountEditText.getText().toString().trim();
            BigDecimal amount = new BigDecimal(amountText).setScale(MoneyFormatter.DECIMAL_PLACES, RoundingMode.DOWN);
            return amount.compareTo(BigDecimal.ZERO) > 0 ? amount : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Direction getDirection() {
        int checkedRadioId = directionRadioGroup.getCheckedRadioButtonId();
        switch (checkedRadioId) {
            case R.id.withdrawalRadioButton:
                return Direction.WITHDRAWAL;
            case R.id.depositRadioButton:
                return Direction.DEPOSIT;
            default:
                throw new IllegalStateException("Illegal radio ID: " + checkedRadioId);
        }
    }

    @Override
    public DateTimePicker getDateTimePicker() {
        return dateTimePicker;
    }
}
