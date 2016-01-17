package cz.pikadorama.uome.adapter.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.pikadorama.uome.R;

public class DividerItem extends NavigationItem {

    public DividerItem() {
        super(null);
    }

    @Override
    public View getView(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_drawer_divider, parent, false);
    }
}
