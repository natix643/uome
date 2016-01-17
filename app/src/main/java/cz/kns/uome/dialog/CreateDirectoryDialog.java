package cz.kns.uome.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import cz.kns.uome.R;
import cz.kns.uome.common.fragment.DualDialogFragment;
import cz.kns.uome.common.util.Views;
import cz.kns.uome.common.view.BaseTextWatcher;

public class CreateDirectoryDialog extends DualDialogFragment {

	public interface Callback {
		void onCreateDirectory(String name, Dialog dialog);
	}

	private Callback callback;

	@Override
	protected void onAttachToActivity(Activity activity) {
		callback = (Callback) activity;
	}

	@Override
	protected void onAttachToFragment(Fragment fragment) {
		callback = (Callback) fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View layout = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_directory, null);
		final EditText editText = Views.require(layout, R.id.nameEditText);

		final AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_create_directory_title)
				.setView(layout)
				.setPositiveButton(android.R.string.ok, null)
				.setNegativeButton(android.R.string.cancel, null)
				.create();

		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface d) {
				final Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				okButton.setEnabled(!editText.getText().toString().trim().isEmpty());

				editText.addTextChangedListener(new BaseTextWatcher() {
					@Override
					public void afterTextChanged(Editable s) {
						okButton.setEnabled(!s.toString().trim().isEmpty());
					}
				});

				okButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						callback.onCreateDirectory(editText.getText().toString().trim(), dialog);
					}
				});
			}
		});

		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return dialog;
	}

}
