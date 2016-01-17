package cz.pikadorama.uome.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import cz.pikadorama.uome.common.fragment.DualDialogFragment;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConfirmationDialog extends DualDialogFragment {

    private static final String ARG_REQUEST_CODE = "requestCode";

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE_BUTTON = "positiveButton";
    private static final String ARG_NEGATIVE_BUTTON = "negativeButton";

    public interface Callback {
        void onConfirmed(String requestCode);
    }

    private Callback callback;

    private final OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String requestCode = requireArgument(ARG_REQUEST_CODE);
            callback.onConfirmed(requestCode);
        }
    };

    public static ConfirmationDialog of(String requestCode) {
        checkNotNull(requestCode);

        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_CODE, requestCode);

        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public ConfirmationDialog setTitle(int stringId) {
        getArguments().putInt(ARG_TITLE, stringId);
        return this;
    }

    public ConfirmationDialog setMessage(int stringId) {
        getArguments().putInt(ARG_MESSAGE, stringId);
        return this;
    }

    public ConfirmationDialog setPositiveButton(int stringId) {
        getArguments().putInt(ARG_POSITIVE_BUTTON, stringId);
        return this;
    }

    public ConfirmationDialog setNegativeButton(int stringId) {
        getArguments().putInt(ARG_NEGATIVE_BUTTON, stringId);
        return this;
    }

    @Override
    protected void onAttachToActivity(Activity activity) {
        this.callback = (Callback) activity;
    }

    @Override
    protected void onAttachToFragment(Fragment fragment) {
        this.callback = (Callback) fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int positiveButton = getArguments().getInt(ARG_POSITIVE_BUTTON, android.R.string.ok);
        int negativeButton = getArguments().getInt(ARG_NEGATIVE_BUTTON, android.R.string.cancel);

        return new AlertDialog.Builder(getActivity())
                .setTitle(getStringArg(ARG_TITLE))
                .setMessage(getStringArg(ARG_MESSAGE))
                .setPositiveButton(positiveButton, positiveListener)
                .setNegativeButton(negativeButton, null)
                .create();
    }

    private String getStringArg(String key) {
        int id = getArguments().getInt(key);
        return (id != 0) ? getString(id) : null;
    }

}
