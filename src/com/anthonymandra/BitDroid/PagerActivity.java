package com.anthonymandra.BitDroid;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PagerActivity extends SherlockFragmentActivity {
	private static final String TAG = PagerActivity.class.getSimpleName();

    private TabAdapter pageAdapter;
    private ViewPager pager;
    
    private ChartFragment chart;
    private boolean isVisibleSmallChart = false;
    private boolean isVisibleLargeChart = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);
		
		BitfinexFragment.initialize(this);
		
        List<Fragment> fragments = getFragments();
        pageAdapter = new TabAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPager) findViewById(R.id.pager);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        
        pager.setAdapter(pageAdapter);
        pager.setOnPageChangeListener(new SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position) {
                // When swiping between pages, select the
                // corresponding tab.
            	actionBar.setSelectedNavigationItem(position);
            }
        });
		
	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				pager.setCurrentItem(tab.getPosition());
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
	    };
//	    View exchange = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_layout, null);
//	    ((TextView)exchange.findViewById(R.id.tabsText)).setText(R.string.exchange);
//	    View margin = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_layout, null);
//	    margin.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
//	    ((TextView)margin.findViewById(R.id.tabsText)).setText(R.string.margin);
        
	    actionBar.addTab(actionBar.newTab()
//                .setCustomView(exchange)
	    		.setText(R.string.exchange)
                .setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab()
//	    		.setCustomView(margin)
	    		.setText(R.string.margin)
                .setTabListener(tabListener));
//	    actionBar.addTab(actionBar.newTab()
////	    		.setCustomView(margin)
//	    		.setIcon(R.drawable.chart)
//                .setTabListener(tabListener));
		
	    chart = new ChartFragment();
//		FragmentManager fm = getSupportFragmentManager();
//		fm.beginTransaction().add(R.id.baseView, chart, "chart").hide(chart).commit();
//		fm.executePendingTransactions();
	}
	
	@Override
	public void onBackPressed() {
		if (isVisibleLargeChart)
			hideLargeChart();
		else
			super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.largeChartMenu:
				// One chart at a time
				if (isVisibleSmallChart)
					hideSmallChart();
				
				// Handle the button as a toggle
				// TODO: Add custom toggle action button
				if (isVisibleLargeChart)
					hideLargeChart();
				else				
					showLargeChart();
				return true;
			case R.id.smallChartMenu:
				// One chart at a time
				if (isVisibleLargeChart)
					hideLargeChart();
				
				// Handle the button as a toggle
				// TODO: Add custom toggle action button
				if (isVisibleSmallChart)
					hideSmallChart();
				else				
					showSmallChart();
				showSmallChart();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void showSmallChart()
	{
		isVisibleSmallChart = true;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f = fm.findFragmentByTag(ChartFragment.FRAGMENT_TAG);
		if (f != null)
		{
			ft.remove(chart);
			ft.commit();
			fm.executePendingTransactions();
			ft = fm.beginTransaction();
		}
		ft.add(R.id.chartContainer, chart, ChartFragment.FRAGMENT_TAG);
		ft.show(chart).commit();
		fm.executePendingTransactions();
	}
	
	private void showLargeChart()
	{
		isVisibleLargeChart = true;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (chart != null)
		{
			ft.remove(chart);
			ft.commit();
			fm.executePendingTransactions();
			ft = fm.beginTransaction();
		}
		ft.add(R.id.baseView, chart, ChartFragment.FRAGMENT_TAG);
		ft.show(chart).commit();
		fm.executePendingTransactions();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	private void hideLargeChart()
	{
		isVisibleLargeChart = false;
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().hide(chart).commit();
		fm.executePendingTransactions();
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	private void hideSmallChart()
	{
		isVisibleSmallChart = false;
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(ChartFragment.FRAGMENT_TAG)).commit();
		fm.executePendingTransactions();
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	private List<Fragment> getFragments(){

        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(new ExchangeFragment());
        fList.add(new MarginFragment());
//        fList.add(new ChartFragment());
        return fList;
    }

    public static class TabAdapter extends FragmentStatePagerAdapter
    {
        List<Fragment> fragments;

        public TabAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return this.fragments.get(i);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
