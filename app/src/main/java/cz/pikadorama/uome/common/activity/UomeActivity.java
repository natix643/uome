package cz.pikadorama.uome.common.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import cz.pikadorama.uome.R;

public abstract class UomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findView(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
