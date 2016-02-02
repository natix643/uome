package cz.pikadorama.uome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeListAdapter;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.model.Group;

public class GroupMultichoiceAdapter extends UomeListAdapter<Group> {

    public GroupMultichoiceAdapter(Context context, List<Group> groups) {
        super(context, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.item_group_multichoice, parent, false);

        Group group = getItem(position);

        TextView titleTextView = Views.find(root, R.id.titleTextView);
        TextView descriptionTextView = Views.find(root, R.id.descriptionTextView);

        if (group.getId() == Constants.SIMPLE_GROUP_ID) {
            titleTextView.setText(R.string.simple_debts_name);
            descriptionTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setText(group.getName());
            if (!group.getDescription().isEmpty()) {
                descriptionTextView.setText(group.getDescription());
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }
        }

        return root;
    }

}
