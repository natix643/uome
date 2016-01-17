package cz.pikadorama.uome.io;

import android.app.Activity;

import com.csvreader.CsvWriter;
import com.google.common.base.CharMatcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.format.DateTimeFormatter;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.PersonDao;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.Transaction.Direction;
import cz.pikadorama.uome.model.TransactionDao;

import static com.google.common.base.CharMatcher.is;
import static com.google.common.base.Preconditions.*;
import static cz.pikadorama.uome.model.Transaction.Direction.WITHDRAWAL;

public class CsvExport {

    private static final String PATH_PATTERN = "{0}/export_{1}/";

    private static final char DELIMITER = ';';
    private static final CharMatcher DELIMITERS = CharMatcher.is(DELIMITER);
    private static final CharMatcher VALID_FILENAME_CHARS = CharMatcher.JAVA_LETTER_OR_DIGIT.or(is(' '));

    private final DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
    private final MoneyFormatter moneyFormatter = MoneyFormatter.withPlusPrefix();

    private final Activity activity;
    private final DateTimeFormatter dateTimeFormatter;
    private final PersonDao personDao;
    private final TransactionDao transactionDao;

    public CsvExport(Activity activity) {
        this.activity = activity;
        this.dateTimeFormatter = DateTimeFormatter.showDateTime(activity).showYearAlways();
        this.personDao = new PersonDao(activity);
        this.transactionDao = new TransactionDao(activity);
    }

    public void export(List<Group> groups, File directory) throws IOException {
        checkArgument(directory.isDirectory(), "%s is not a directory", directory);
        checkArgument(directory.canWrite(), "cannot write into directory %s", directory);

        String path = MessageFormat.format(PATH_PATTERN, directory, timestampFormat.format(new Date()));
        new File(path).mkdir();

        for (Group group : groups) {
            List<Person> persons = personDao.getAllForGroup(group);
            List<Transaction> transactions = transactionDao.getAllForGroup(group);

            CsvWriter out = null;
            try {
                String groupName = cleanGroupName(group);
                File file = new File(path + groupName + ".csv");
                file.createNewFile();
                out = new CsvWriter(new FileWriter(file, false), DELIMITER);

                exportGroupTransactions(persons, transactions, out);

                out.close();

                String summary = activity.getString(R.string.export_summary_suffix);
                file = new File(path + groupName + "_" + summary + ".csv");
                file.createNewFile();
                out = new CsvWriter(new FileWriter(file, false), DELIMITER);

                exportGroupSummary(persons, transactions, out);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    private void exportGroupTransactions(List<Person> persons, List<Transaction> transactions,
            CsvWriter out) throws IOException {
        // header
        out.write(activity.getString(R.string.export_header_person));
        out.write(activity.getString(R.string.export_header_email));
        out.write(activity.getString(R.string.export_header_financial));
        out.write(activity.getString(R.string.export_header_value));
        out.write(activity.getString(R.string.export_header_description));
        out.write(activity.getString(R.string.export_header_datetime));
        out.endRecord();

        // data
        for (Transaction transaction : transactions) {
            out.write(removeDelimiters(getPersonByIdFromList(transaction.getPersonId(), persons)
                    .getName()));
            out.write(getPersonByIdFromList(transaction.getPersonId(), persons).getEmail());
            out.write(activity.getString(transaction.isFinancial() ? R.string.yes : R.string.no));
            out.write(resolveValue(transaction));
            out.write(removeDelimiters(transaction.getDescription()));
            out.write(dateTimeFormatter.format(transaction.getDateTime()));
            out.endRecord();
        }
    }

    private void exportGroupSummary(List<Person> persons, List<Transaction> transactions,
            CsvWriter out) throws IOException {
        // header
        out.write(activity.getString(R.string.export_header_person));
        out.write(activity.getString(R.string.export_header_balance));
        out.endRecord();

        for (Person person : persons) {
            // data
            out.write(person.getName());
            out.write(resolveSummaryBalance(person, transactions));
            out.endRecord();
        }
    }

    private String resolveSummaryBalance(Person person, List<Transaction> transactions) {
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getPersonId() == person.getId() && transaction.isFinancial()) {
                switch (transaction.getDirection()) {
                    case WITHDRAWAL:
                        balance = balance.subtract(new BigDecimal(transaction.getValue()));
                        break;
                    case DEPOSIT:
                        balance = balance.add(new BigDecimal(transaction.getValue()));
                        break;
                    default:
                        throw new AssertionError(transaction.getDirection());
                }
            }
        }
        return moneyFormatter.format(balance);
    }

    private static Person getPersonByIdFromList(Long personId, List<Person> persons) {
        for (Person person : persons) {
            if (person.getId().equals(personId)) {
                return person;
            }
        }
        throw new IllegalStateException("No person found.");
    }

    private String resolveValue(Transaction transaction) {
        Direction direction = checkNotNull(transaction.getDirection());

        if (transaction.isFinancial()) {
            BigDecimal amount = new BigDecimal(transaction.getValue());
            return moneyFormatter.format(direction == WITHDRAWAL ? amount.negate() : amount);
        } else {
            String item = removeDelimiters(transaction.getValue());
            return direction == WITHDRAWAL ? "-" + item : "+" + item;
        }
    }

    private String cleanGroupName(Group group) {
        return VALID_FILENAME_CHARS.retainFrom(resolveGroupName(group)).trim();
    }

    private String resolveGroupName(Group group) {
        if (group.getId() == Constants.SIMPLE_GROUP_ID) {
            return activity.getString(R.string.simple_debts_name);
        } else {
            return group.getName();
        }
    }

    private static String removeDelimiters(String string) {
        return DELIMITERS.removeFrom(string);
    }
}
