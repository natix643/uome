package cz.kns.uome.dialog;

import java.util.Collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import cz.kns.uome.R;
import cz.kns.uome.common.fragment.BaseDialogFragment;

public class SelectEmailDialog extends BaseDialogFragment {

	public interface Callback {
		void onEmailSelected(String email);
	}

	private static final String ARG_EMAILS = "emails";

	private Callback callback;

	public static SelectEmailDialog with(Collection<String> emails) {
		SelectEmailDialog dialog = new SelectEmailDialog();
		Bundle arguments = new Bundle();
		arguments.putStringArray(ARG_EMAILS, emails.toArray(new String[0]));
		dialog.setArguments(arguments);
		return dialog;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = (Callback) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String[] emails = getArguments().getStringArray(ARG_EMAILS);
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_select_email_title)
				.setItems(emails, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						callback.onEmailSelected(emails[which]);
					}
				})
				.create();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		callback.onEmailSelected("");
	}
}
