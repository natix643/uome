package cz.pikadorama.uome.common;

import java.math.BigDecimal;

public class Constants {

    private Constants() {}

    public static final long SIMPLE_GROUP_ID = 0;

    public static final String GROUP_ID = "groupId";
    public static final String PERSON_ID = "personId";

    public static final String PREF_ALLOW_NOTIFICATIONS = "allow_notifications";
    public static final String PREF_DEBT_AGE = "debt_age";
    public static final String PREF_LAST_OPENED_GROUP = "lastOpenedGroup";

    public static final int MISSING_EXTRA = -1;

    public static final String SELECTED_PERSON = "selectedPerson";
    public static final String SELECTED_PERSONS = "selectedPersons";
    public static final String SELECTED_GROUP = "selectedGroup";
    public static final String SELECTED_TRANSACTIONS = "selectedTransactions";

    public static final String EDITED_TRANSACTION_ID = "editedTransactionId";

    public static final BigDecimal MINUS_ONE = new BigDecimal("-1");

    public static final String IMAGE_URI = "imageUri";

    // ugh
    public static final String DEFAULT_PREF_DEBT_AGE = "5";

}
