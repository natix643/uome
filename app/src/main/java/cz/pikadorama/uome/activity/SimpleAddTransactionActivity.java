package cz.pikadorama.uome.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.PersonSpinnerAdapter;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.view.DateTimePicker;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.Transaction.Direction;
import cz.pikadorama.uome.model.TransactionDao;
import cz.pikadorama.uome.model.parcelable.TransactionData;

public class SimpleAddTransactionActivity extends UomeActivity implements DateTimePicker.Holder {

    private PersonDao personDao;
    private TransactionDao transactionDao;

    private PersonSpinnerAdapter adapter;

    private RadioGroup directionRadioGroup;
    private RadioButton withdrawalRadio;
    private RadioButton depositRadio;

    private Spinner personSpinner;

    private EditText amountEditText;
    private TextInputLayout amountTextLayout;
    private EditText itemEditText;
    private TextInputLayout itemTextLayout;

    private ViewSwitcher financialViewSwitcher;
    private SwitchCompat financialSwitch;

    private DateTimePicker dateTimePicker;
    private EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDaos();
        initViews();

        getSupportActionBar().setTitle(actionBarTitle());
        loadIntent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.simple_add_transaction;
    }

    private void initDaos() {
        personDao = new PersonDao(getApplicationContext());
        transactionDao = new TransactionDao(getApplicationContext());
    }

    private void initViews() {
        directionRadioGroup = requireView(R.id.directionRadioGroup);
        withdrawalRadio = requireView(R.id.withdrawalRadioButton);
        depositRadio = requireView(R.id.depositRadioButton);

        adapter = new PersonSpinnerAdapter(this, personDao.getAllForGroup(Constants.SIMPLE_GROUP_ID));
        personSpinner = requireView(R.id.personSpinner);
        personSpinner.setAdapter(adapter);

        financialViewSwitcher = requireView(R.id.financialViewSwitcher);

        int accent = getResources().getColor(R.color.accent);
        financialSwitch = requireView(R.id.financialSwitch);
        financialSwitch.getThumbDrawable().setColorFilter(accent, PorterDuff.Mode.MULTIPLY);
        financialSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    itemTextLayout.setError(null);
                    financialViewSwitcher.setDisplayedChild(0);
                } else {
                    amountTextLayout.setError(null);
                    financialViewSwitcher.setDisplayedChild(1);
                }
            }
        });

        amountEditText = requireView(R.id.amountEditText);
        amountTextLayout = requireView(R.id.amountTextLayout);
        amountTextLayout.setHint(null);
        Views.autoClearError(amountTextLayout);

        itemEditText = requireView(R.id.itemEditText);
        itemTextLayout = requireView(R.id.itemTextLayout);
        itemTextLayout.setHint(null);
        Views.autoClearError(itemTextLayout);

        dateTimePicker = requireView(R.id.dateTimePicker);
        descriptionEditText = requireView(R.id.descriptionEditText);
    }

    private int actionBarTitle() {
        switch (getRequestCode()) {
            case ActivityRequest.ADD_TRANSACTION:
                return R.string.title_add_transaction;
            case ActivityRequest.EDIT_TRANSACTION:
                return R.string.title_edit_transaction;
            default:
                throw new IllegalStateException();
        }
    }

    private void loadIntent() {
        TransactionData data = requireIntentExtra(TransactionData.KEY);

        Long personId = data.getPersonId();
        if (personId != null) {
            Person person = personDao.getById(personId);
            personSpinner.setSelection(adapter.getPosition(person));
            personSpinner.setEnabled(false);
        }

        Boolean isFinancial = data.isFinancial();
        if (isFinancial != null) {
            financialSwitch.setChecked(isFinancial);
            (isFinancial ? amountEditText : itemEditText).setText(data.getValue());
        }

        Direction direction = data.getDirection();
        if (direction != null) {
            checkDirectionRadio(direction);
        }

        descriptionEditText.setText(data.getDescription());

        if (getRequestCode() == ActivityRequest.EDIT_TRANSACTION) {
            dateTimePicker.setDateTime(data.getDateTime());
        }
    }

    private long getEditedTransactionId() {
        TransactionData data = requireIntentExtra(TransactionData.KEY);
        return data.getId();
    }

    private void checkDirectionRadio(Direction direction) {
        RadioButton radio = (direction == Direction.DEPOSIT) ? depositRadio : withdrawalRadio;
        radio.setChecked(true);
    }

    private int getRequestCode() {
        int code = requireIntentExtra(ActivityRequest.KEY);

        switch (code) {
            case ActivityRequest.ADD_TRANSACTION:
            case ActivityRequest.EDIT_TRANSACTION:
                return code;
            default:
                throw new IllegalStateException("invalid request code: " + code);
        }
    }

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
        Person person = adapter.getItem(personSpinner.getSelectedItemPosition());

        boolean financial = financialSwitch.isChecked();

        String value = financial ? getAmount() : getBorrowedItem();
        if (value == null) {
            if (financial) {
                amountTextLayout.setError(getString(R.string.error_no_amount));
            } else {
                itemTextLayout.setError(getString(R.string.error_no_borrowed_item));
            }
            return;
        }

        Date dateTime = dateTimePicker.getDateTime();
        String description = descriptionEditText.getText().toString().trim();

        Transaction transaction = new Transaction(person.getId(), Constants.SIMPLE_GROUP_ID,
                value, financial, getDirection(), description, dateTime);

        switch (getRequestCode()) {
            case ActivityRequest.ADD_TRANSACTION:
                transactionDao.create(transaction);
                break;
            case ActivityRequest.EDIT_TRANSACTION:
                transaction.setId(getEditedTransactionId());
                transactionDao.update(transaction);
                break;
        }

        setResult(RESULT_OK);
        finish();
    }

    private String getAmount() {
        try {
            String amountString = amountEditText.getText().toString().trim();
            BigDecimal amount = new BigDecimal(amountString).setScale(MoneyFormatter.DECIMAL_PLACES, RoundingMode.DOWN);
            return amount.compareTo(BigDecimal.ZERO) > 0 ? amount.toPlainString() : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String getBorrowedItem() {
        return Strings.emptyToNull(itemEditText.getText().toString().trim());
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
