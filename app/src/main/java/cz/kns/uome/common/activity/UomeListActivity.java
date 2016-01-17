package cz.kns.uome.common.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class UomeListActivity extends UomeActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = findView(android.R.id.list);
        if (listView == null) {
            throw new IllegalStateException("Layout must have a ListView with id 'android.R.id.list'");
        }
        listView.setOnItemClickListener(onItemClickListener);

        initEmptyView();
    }

    private void initEmptyView() {
        View emptyView = findView(android.R.id.empty);
        listView.setEmptyView(emptyView);

        if (emptyView != null) {
            Integer emptyTextId = getEmptyTextId();
            if (emptyTextId != null) {
                ((TextView) emptyView).setText(emptyTextId);
            }
        }
    }

    protected Integer getEmptyTextId() {
        return null;
    }

    public ListView getListView() {
        return listView;
    }

    public ListAdapter getListAdapter() {
        return listView.getAdapter();
    }

    public void setListAdapter(ListAdapter adapter) {
        listView.setAdapter(adapter);
    }

    /**
     *
     * @param list
     * @param view
     * @param position
     * @param id
     */
    protected void onListItemClick(ListView list, View view, int position, long id) {}

    private final OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onListItemClick((ListView) parent, view, position, id);
        }
    };
}
