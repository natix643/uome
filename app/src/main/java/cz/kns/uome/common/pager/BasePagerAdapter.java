package cz.kns.uome.common.pager;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import cz.kns.uome.common.fragment.Fragments;

public class BasePagerAdapter extends FragmentPagerAdapter {

	// TODO this is not really needed when MVC is used
	private final SparseArray<Fragment> activeFragments = new SparseArray<>();

	private final Activity activity;
	private final List<Page> pages;

	public BasePagerAdapter(Activity activity, List<Page> pages) {
		super(activity.getFragmentManager());
		this.activity = activity;
		this.pages = pages;
	}

	@Override
	public Fragment getItem(int position) {
		Page page = pages.get(position);
		return Fragments.instantiate(activity, page.getFragmentClass(), page.getArguments());
	}

	@Override
	public int getCount() {
		return pages.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Integer titleId = pages.get(position).getTitleId();
		return titleId != null ? activity.getString(titleId) : "";
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = (Fragment) super.instantiateItem(container, position);
		activeFragments.put(position, fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		activeFragments.remove(position);
	}

	public Fragment getFragment(int position) {
		return activeFragments.get(position);
	}

}