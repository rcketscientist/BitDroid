package com.anthonymandra.bitfinex.widget;

import com.anthonymandra.BitDroid.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ExpandableRefreshHeader extends LinearLayout {

	int srcCollapse = R.drawable.ic_action_collapse;
	int srcExpand = R.drawable.ic_action_expand;
	boolean isCollapsed = true;
	String title = "Title";
		
	public ExpandableRefreshHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.expandable_refresh_header, this, true);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableRefreshHeader, 0, 0);

  		String title = a.getString(R.styleable.ExpandableRefreshHeader_titleText);
  		if (title == null)
  			title = "Title";
  		isCollapsed = a.getBoolean(R.styleable.ExpandableRefreshHeader_isCollapsed, true);
  		srcCollapse = a.getResourceId(R.styleable.ExpandableRefreshHeader_srcCollapse, R.drawable.ic_action_collapse);
  		srcExpand = a.getResourceId(R.styleable.ExpandableRefreshHeader_srcExpand, R.drawable.ic_action_expand);
  		int srcRefresh = a.getResourceId(R.styleable.ExpandableRefreshHeader_srcRefresh, R.drawable.ic_action_refresh);
  		
  		((Button)findViewById(R.id.headerButton)).setText(title);
  		if (isCollapsed)
  			collapse();

  		((ImageButton)findViewById(R.id.refreshButton)).setImageResource(srcRefresh);

  		a.recycle();
	}
	
	public void collapse()
	{
		((ImageButton)findViewById(R.id.collapseButton)).setImageResource(srcExpand);
	}
	
	public void expand()
	{
		((ImageButton)findViewById(R.id.collapseButton)).setImageResource(srcCollapse);
	}
	
	public void setOnClickListenerHeader(OnClickListener listener)
	{
		((Button)findViewById(R.id.headerButton)).setOnClickListener(listener);
		((ImageButton)findViewById(R.id.collapseButton)).setOnClickListener(listener);
	}

	public void setOnClickListenerRefresh(OnClickListener listener)
	{
		((ImageButton)findViewById(R.id.refreshButton)).setOnClickListener(listener);
	}
}
