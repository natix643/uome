package cz.pikadorama.uome.adapter.navigation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import cz.pikadorama.uome.common.activity.UomeListAdapter;

public class NavigationAdapter extends UomeListAdapter<NavigationItem> {

    public NavigationAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(getContext(), parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }

}
