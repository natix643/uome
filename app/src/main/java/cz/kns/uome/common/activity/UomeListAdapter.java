package cz.kns.uome.common.activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class UomeListAdapter<T> extends ArrayAdapter<T> {

	public UomeListAdapter(Context context) {
		super(context, 0);
	}

	public UomeListAdapter(Context context, List<T> elements) {
		super(context, 0, elements);
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}

	public void reset(Iterable<? extends T> newElements) {
		clear();
		for (T element : newElements) {
			add(element);
		}
	}

}
