package cz.kns.uome.common.pager;

import java.util.List;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import cz.kns.uome.R;
import cz.kns.uome.common.activity.BaseActivity;

public abstract class PagerActivity extends BaseActivity {

	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pager = findView(getPagerId());
		pager.setAdapter(createPagerAdapter());
	}

	protected ViewPager getPager() {
		return pager;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.pager;
	}

	protected int getPagerId() {
		return R.id.pager;
	}

	protected PagerAdapter createPagerAdapter() {
		return new BasePagerAdapter(this, getPages());
	}

	protected abstract List<Page> getPages();
}
