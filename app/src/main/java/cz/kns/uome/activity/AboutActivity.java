package cz.kns.uome.activity;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import cz.kns.uome.R;
import cz.kns.uome.common.activity.UomeActivity;
import cz.kns.uome.common.format.MessageFormatter;
import cz.kns.uome.dialog.ListLibrariesDialog;

public class AboutActivity extends UomeActivity {

	private final MessageFormatter messageFormatter = new MessageFormatter(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView versionText = requireView(R.id.aboutVersionText);
		versionText.setText(messageFormatter.format(R.string.about_version, getVersionName()));

		TextView emailLink = requireView(R.id.aboutEmailLink);
		emailLink.setMovementMethod(LinkMovementMethod.getInstance());

		Button librariesButton = requireView(R.id.aboutLibrariesButton);
		librariesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ListLibrariesDialog().show(self);
			}
		});
	}

	private String getVersionName() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.about;
	}

}
