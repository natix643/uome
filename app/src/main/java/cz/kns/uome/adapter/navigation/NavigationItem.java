package cz.kns.uome.adapter.navigation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class NavigationItem {

	private final NavigationListener listener;

	public NavigationItem(NavigationListener listener) {
		this.listener = listener;
	}

	public NavigationListener getListener() {
		return listener;
	}

	public final boolean isEnabled() {
		return listener != null;
	}

	public abstract View getView(Context context, ViewGroup parent);

}
