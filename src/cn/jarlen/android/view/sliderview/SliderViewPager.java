package cn.jarlen.android.view.sliderview;

import cn.jarlen.android.view.sliderview.SliderAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义的轮播图ViewPager(当只有一个Item时，不能左右滑动)
 * @author :jarlen
 * @date : 2016-3-15
 */
public class SliderViewPager extends ViewPager {

	public SliderViewPager(Context context) {
		super(context);
	}

	public SliderViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void nextItem() {
		int position = (getCurrentItem() + 1);
		super.setCurrentItem(position);
	}

	@Override
	public void setAdapter(PagerAdapter arg0) {
		super.setAdapter(arg0);
	}

	/**
	 * added by jarlen for 轮播图一张的时候禁止左右滑动
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SliderAdapter pagerAdapter = (SliderAdapter) getAdapter();
		if (pagerAdapter != null && pagerAdapter.getRealCount() > 1) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	/**
	 * added by jarlen for 轮播图一张的时候禁止左右滑动
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		SliderAdapter pagerAdapter = (SliderAdapter) getAdapter();
		if (pagerAdapter != null && pagerAdapter.getRealCount() > 1) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}
}
