package cn.jarlen.android.view.sliderview;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * A slider adapter
 */
public class SliderAdapter extends PagerAdapter implements OnClickListener {

	private Context mContext;
	private List<BaseSliderView> mImageContents = new ArrayList<BaseSliderView>();

	private OnSliderViewClickListener listener = null;

	public SliderAdapter(Context context) {
		mContext = context;
	}

	public void addSliderViews(List<BaseSliderView> list) {
		mImageContents.addAll(list);
		notifyDataSetChanged();
	}

	public void addSliderView(BaseSliderView sliderView) {
		mImageContents.add(sliderView);
		notifyDataSetChanged();
	}

	public void removeSliderViews() {
		if (mImageContents != null) {
			mImageContents.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public int getRealCount() {
		return mImageContents.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
		Log.d("===", "destroyItem " + position);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.d("===", "instantiateItem " + position);
		position %= mImageContents.size();
		BaseSliderView sliderView = mImageContents.get(position);
		View view = sliderView.getView();
		view.setTag("" + position);
		view.setOnClickListener(this);
		container.addView(view);
		return view;
	}

	@Override
	public void onClick(View view) {
		if (listener != null) {
			int position = Integer.parseInt((String) view.getTag());
			listener.onSliderViewClick(mImageContents.get(position));
		}
	}

	public void setOnSliderViewClickListener(
			OnSliderViewClickListener onSliderViewClickListener) {
		this.listener = onSliderViewClickListener;
	}

	public interface OnSliderViewClickListener {
		public void onSliderViewClick(BaseSliderView sliderview);
	}

}
