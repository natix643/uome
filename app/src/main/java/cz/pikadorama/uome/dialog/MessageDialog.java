package cz.pikadorama.uome.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.format.MessageFormatter;
import cz.pikadorama.uome.common.fragment.BaseDialogFragment;

public class MessageDialog extends BaseDialogFragment {

    private static final String ARG_MESSAGE_ID = "messageId";
    private static final String ARG_TITLE_ID = "titleId";
    private static final String ARG_ICON_ID = "iconId";

    private static final int MISSING_ARG = 0;

    private MessageFormatter messageFormatter;

    public static MessageDialog create(int messageId) {
        return MessageDialog.create(messageId, MISSING_ARG);
    }

    public static MessageDialog create(int messageId, int titleId) {
        return MessageDialog.create(messageId, titleId, MISSING_ARG);
    }

    public static MessageDialog create(int messageId, int titleId, int iconId) {
        MessageDialog fragment = new MessageDialog();

        Bundle arguments = new Bundle();
        arguments.putInt(ARG_MESSAGE_ID, messageId);
        arguments.putInt(ARG_TITLE_ID, titleId);
        arguments.putInt(ARG_ICON_ID, iconId);

        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        messageFormatter = new MessageFormatter(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int messageId = requireArgument(ARG_MESSAGE_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(messageFormatter.formatHtml(messageId))
                .setNeutralButton(R.string.close, null);

        int titleId = getArguments().getInt(ARG_TITLE_ID);
        if (titleId != MISSING_ARG) {
            builder.setTitle(titleId);
        }

        int iconId = getArguments().getInt(ARG_ICON_ID);
        if (iconId != MISSING_ARG) {
            builder.setIcon(iconId);
        }

        return builder.create();
    }

}
