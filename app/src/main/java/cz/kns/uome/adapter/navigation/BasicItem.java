package cz.kns.uome.adapter.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.kns.uome.R;

public class BasicItem extends NavigationItem {

    private final int textId;
    private final Integer iconId;

    public BasicItem(int textId, NavigationListener listener) {
        this(listener, textId, null);
    }

    public BasicItem(int textId, int iconId, NavigationListener listener) {
        this(listener, textId, iconId);
    }

    private BasicItem(NavigationListener listener, int textId, Integer iconId) {
        super(listener);
        this.textId = textId;
        this.iconId = iconId;
    }

    @Override
    public View getView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        TextView view = (TextView) inflater.inflate(R.layout.item_drawer_simple, parent, false);
        view.setText(textId);

        if (iconId != null) {
            view.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
        }

        return view;
    }

}
