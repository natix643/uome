package cz.pikadorama.uome.common;

/**
 * @deprecated replace with {@link ActivityRequest}
 */
@Deprecated
public class ActivityPurpose {

    private ActivityPurpose() {}

    public static final String TAG = ActivityPurpose.class.getSimpleName();

    public static final int ADD_NEW_EMPTY = 1;
    public static final int EDIT_EXISTING = 3;

}
