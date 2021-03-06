package cz.pikadorama.uome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.activity.UomeListAdapter;
import cz.pikadorama.uome.common.view.AvatarView;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.model.Person;

public class PersonSpinnerAdapter extends UomeListAdapter<Person> {

    public PersonSpinnerAdapter(Context context, List<Person> persons) {
        super(context, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView =
                (TextView) LayoutInflater.from(getContext()).inflate(R.layout.spinner_closed, parent, false);
        textView.setText(getItem(position).getName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_persons, parent, false);
        Person person = getItem(position);

        TextView nameTextView = Views.require(root, R.id.nameTextView);
        nameTextView.setText(person.getName());

        AvatarView avatarView = Views.require(root, R.id.avatar);
        avatarView.setPerson(person);

        return root;
    }

}
