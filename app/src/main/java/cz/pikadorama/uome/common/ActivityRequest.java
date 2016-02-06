package cz.pikadorama.uome.common;

import android.content.Intent;
import android.os.Bundle;

/**
 * Request codes used in {@link android.app.Activity#startActivityForResult(Intent, int, Bundle)}.
 * They may also be passed into the target activity as intent extra using the {@link #KEY}.
 */
public class ActivityRequest {

    private ActivityRequest() {}

    public static final String KEY = ActivityRequest.class.getSimpleName();

    public static final int ADD_GROUP = 0;
    public static final int EDIT_GROUP = 1;

    public static final int ADD_PERSON = 2;
    public static final int EDIT_PERSON = 3;

    public static final int ADD_TRANSACTION = 4;
    public static final int EDIT_TRANSACTION = 5;

    public static final int OPEN_PERSON_DETAIL = 6;
}
