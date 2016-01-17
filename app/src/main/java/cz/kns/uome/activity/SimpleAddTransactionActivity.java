package cz.kns.uome.activity;

import static com.google.common.base.Preconditions.checkState;
import static cz.kns.uome.common.ActivityPurpose.ADD_NEW_PREFILLED;
import static cz.kns.uome.common.ActivityPurpose.EDIT_EXISTING;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.common.base.Strings;

import cz.kns.uome.R;
import cz.kns.uome.adapter.PersonSpinnerAdapter;
import cz.kns.uome.common.ActivityPurpose;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.activity.UomeActivity;
import cz.kns.uome.common.format.MoneyFormatter;
import cz.kns.uome.common.util.Toaster;
import cz.kns.uome.common.view.DateTimePicker;
import cz.kns.uome.model.Person;
import cz.kns.uome.model.PersonDao;
import cz.kns.uome.model.Transaction;
import cz.kns.uome.model.Transaction.Direction;
import cz.kns.uome.model.TransactionDao;
import cz.kns.uome.model.parcelable.TransactionData;

public class SimpleAddTransactionActivity extends UomeActivity implements DateTimePicker.Holder {

	private PersonDao personDao;
	private TransactionDao transactionDao;

	private Toaster toaster;

	private PersonSpinnerAdapter adapter;

	private RadioGroup directionRadioGroup;
	private RadioButton withdrawalRadio;
	private RadioButton depositRadio;

	private Spinner personSpinner;
	private CheckBox financialCheckbox;

	private ImageView amountOrItemLabel;
	private EditText amountEditText;
	private EditText borrowedItemEditText;

	private DateTimePicker dateTimePicker;
	private EditText descriptionEditText;

	private Integer lazyPurpose;

	private Long editedTransactionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		toaster = new Toaster(this);

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
		directionRadioGroup = findView(R.id.directionRadioGroup);
		withdrawalRadio = findView(R.id.withdrawalRadioButton);
		depositRadio = findView(R.id.depositRadioButton);

		adapter = new PersonSpinnerAdapter(this, personDao.getAllForGroup(Constants.SIMPLE_GROUP_ID));
		personSpinner = findView(R.id.personSpinner);
		personSpinner.setAdapter(adapter);

		financialCheckbox = findView(R.id.financialTransactionCheckBox);
		financialCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked) {
					amountEditText.setVisibility(View.VISIBLE);
					borrowedItemEditText.setVisibility(View.INVISIBLE);
					amountOrItemLabel.setContentDescription(getString(R.string.label_amount));
					amountOrItemLabel.setImageResource(R.drawable.ic_label_amount);
				} else {
					amountEditText.setVisibility(View.INVISIBLE);
					borrowedItemEditText.setVisibility(View.VISIBLE);
					amountOrItemLabel.setContentDescription(getString(R.string.label_borrowed_item));
					amountOrItemLabel.setImageResource(R.drawable.ic_label_borrowed_item);
				}
			}
		});

		amountOrItemLabel = findView(R.id.amountOrItemLabel);
		amountEditText = findView(R.id.amountEditText);
		borrowedItemEditText = findView(R.id.nameEditText);

		dateTimePicker = findView(R.id.dateTimePicker);
		descriptionEditText = findView(R.id.descriptionEditText);
	}

	private int actionBarTitle() {
		switch (getPurpose()) {
			case ActivityPurpose.ADD_NEW_PREFILLED:
				return R.string.title_add_transaction;
			case ActivityPurpose.EDIT_EXISTING:
				return R.string.title_edit_transaction;
			default:
				throw new IllegalStateException();
		}
	}

	private void loadIntent() {
		TransactionData data = getIntent().getParcelableExtra(TransactionData.TAG);

		Long personId = data.getPersonId();
		if (personId != null) {
			Person person = personDao.getById(personId);
			personSpinner.setSelection(adapter.getPosition(person));
			personSpinner.setEnabled(false);
		}

		Boolean isFinancial = data.isFinancial();
		if (isFinancial != null) {
			financialCheckbox.setChecked(isFinancial);
			(isFinancial ? amountEditText : borrowedItemEditText).setText(data.getValue());
		}

		Direction direction = data.getDirection();
		if (direction != null) {
			checkDirectionRadio(direction);
		}

		descriptionEditText.setText(data.getDescription());

		if (getPurpose() == ActivityPurpose.EDIT_EXISTING) {
			editedTransactionId = data.getId();
			dateTimePicker.setDateTime(data.getDateTime());
		}
	}

	private void checkDirectionRadio(Direction direction) {
		RadioButton radio = (direction == Direction.DEPOSIT) ? depositRadio : withdrawalRadio;
		radio.setChecked(true);
	}

	private int getPurpose() {
		if (lazyPurpose == null) {
			lazyPurpose = requireIntentExtra(ActivityPurpose.TAG);
		}
		checkState(lazyPurpose == ADD_NEW_PREFILLED || lazyPurpose == EDIT_EXISTING,
				"Invalid purpose: %s", lazyPurpose);
		return lazyPurpose;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (editedTransactionId != null) {
			outState.putLong(Constants.EDITED_TRANSACTION_ID, editedTransactionId);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		long savedTransactionId = savedInstanceState.getLong(Constants.EDITED_TRANSACTION_ID, Constants.MISSING_EXTRA);
		if (savedTransactionId != Constants.MISSING_EXTRA) {
			editedTransactionId = savedTransactionId;
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

		boolean financial = financialCheckbox.isChecked();
		String value = financial ? getAmount() : getBorrowedItem();
		if (value == null) {
			toaster.show(financial ? R.string.error_no_amount : R.string.error_no_borrowed_item);
			return;
		}

		Date dateTime = dateTimePicker.getDateTime();
		String description = descriptionEditText.getText().toString().trim();

		Transaction transaction = new Transaction(person.getId(), Constants.SIMPLE_GROUP_ID,
				value, financial, getDirection(), description, dateTime);

		switch (getPurpose()) {
			case ActivityPurpose.ADD_NEW_PREFILLED:
				transactionDao.create(transaction);
				toaster.show(R.string.toast_transaction_added);
				break;
			case ActivityPurpose.EDIT_EXISTING:
				transaction.setId(editedTransactionId);
				transactionDao.update(transaction);
				toaster.show(R.string.toast_transaction_updated);
				break;
		}
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
		return Strings.emptyToNull(borrowedItemEditText.getText().toString().trim());
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
