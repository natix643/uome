package cz.kns.uome.common.pager;

import android.app.Fragment;
import android.os.Bundle;

public class Page {

	private final Class<? extends Fragment> fragmentClass;
	private final Bundle arguments;
	private final Integer titleId;

	public Page(Class<? extends Fragment> fragmentClass) {
		this(fragmentClass, null, null);
	}

	public Page(Class<? extends Fragment> fragmentClass, Bundle arguments) {
		this(fragmentClass, arguments, null);
	}

	public Page(Class<? extends Fragment> fragmentClass, int titleId) {
		this(fragmentClass, null, Integer.valueOf(titleId));
	}

	public Page(Class<? extends Fragment> fragmentClass, Bundle arguments, int titleId) {
		this(fragmentClass, arguments, Integer.valueOf(titleId));
	}

	private Page(Class<? extends Fragment> fragmentClass, Bundle arguments, Integer titleId) {
		this.fragmentClass = fragmentClass;
		this.arguments = arguments;
		this.titleId = titleId;
	}

	public Class<? extends Fragment> getFragmentClass() {
		return fragmentClass;
	}

	public Bundle getArguments() {
		return arguments;
	}

	public Integer getTitleId() {
		return titleId;
	}

}