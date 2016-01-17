package cz.kns.uome.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.kns.uome.R;
import cz.kns.uome.common.activity.UomeListAdapter;
import cz.kns.uome.common.util.Views;
import cz.kns.uome.common.view.AvatarView;
import cz.kns.uome.model.Person;

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
