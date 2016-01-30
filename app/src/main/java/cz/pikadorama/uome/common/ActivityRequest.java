package cz.pikadorama.uome.common;

import android.content.Intent;
import android.os.Bundle;

/**
 * Request codes used in {@link android.app.Activity#startActivityForResult(Intent, int, Bundle)}.
 */
public class ActivityRequest {

    private ActivityRequest() {}

    public static final int ADD_GROUP = 0;
    public static final int EDIT_GROUP = 1;

    public static final int ADD_PERSON = 2;
    public static final int EDIT_PERSON = 3;

    public static final int ADD_TRANSACTION = 4;
    public static final int EDIT_TRANSACTION = 5;

    public static final int LIST_PERSON_TRANSACTIONS = 6;
}
