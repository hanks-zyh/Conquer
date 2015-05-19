package app.hanks.com.conquer.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import app.hanks.com.conquer.bean.Zixi;
import app.hanks.com.conquer.fragment.FriendZixiFragment;


public class FriendZixiFragAapter extends FragmentPagerAdapter {
	private ArrayList<Zixi> list;
	private HashMap<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

	public FriendZixiFragAapter(FragmentManager fm, ArrayList<Zixi> list) {
		super(fm);
		this.list = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("zixi", list.get(arg0));
		Fragment frag = new FriendZixiFragment();
		mPageReferenceMap.put(arg0, frag);
		frag.setArguments(bundle);
		return frag;
	}

	@Override
	public int getCount() {
		return list.size();
	}
	
	public Fragment getFragment(int index){
		return mPageReferenceMap.get(index);
	}

}
