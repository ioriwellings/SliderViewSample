package cn.jarlen.android.view;
import java.util.ArrayList;
import java.util.List;

import cn.jarlen.android.view.sliderview.BaseSliderView;
import cn.jarlen.android.view.sliderview.DefaultSliderView;
import cn.jarlen.android.view.sliderview.SliderAdapter.OnSliderViewClickListener;
import cn.jarlen.android.view.sliderview.SliderView;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements OnSliderViewClickListener {

	private SliderView sliderView = null;

	private String[] picList = {
			"http://img2.3lian.com/2014/f4/102/d/90.jpg",
			"http://img1.3lian.com/img013/v5/22/d/21.jpg",
			"http://www.bz55.com/uploads/allimg/130829/1-130RZ91G8.jpg",
			"http://i8.download.fd.pchome.net/t_1366x768/g1/M00/08/05/ooYBAFN18jGIAtTGAAhaCsQ2amsAABiKgLTZ7YACFoi940.jpg",
			"http://image.tianjimedia.com/uploadImages/2013/140/0K11OI9O07HU_1366x768.jpg",
			"http://image.tianjimedia.com/uploadImages/2014/119/20/4084H6Q87KT0_1366x768.jpg" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_slider_view);

		sliderView = (SliderView) findViewById(R.id.sliderView);

		List<BaseSliderView> list = new ArrayList<BaseSliderView>();
		for (int index = 0; index < picList.length; index++) {
			BaseSliderView baseSliderView = createItemSlider(index, "index",
					picList[index]);
			// adapter.addSliderView(baseSliderView);
			list.add(baseSliderView);
		}

		sliderView.addSliderViews(list);
		
		sliderView.setOnSliderViewClickListener(this);

	}

	private BaseSliderView createItemSlider(int position, String key,
			String value) {
		DefaultSliderView mSliderView = new DefaultSliderView(this);

		// initialize a SliderLayout
		mSliderView.setId("" + position).description(key).image(value)
				.setScaleType(BaseSliderView.ScaleType.CenterCrop);

		return mSliderView;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("===", "onResume");
		if(sliderView != null){
			sliderView.recoverCycle();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("===", "onPause");
		if(sliderView != null){
			sliderView.pauseAutoCycle();
		}
	}

	@Override
	public void onSliderViewClick(BaseSliderView sliderview) {
		
		Log.i("===", "onSliderViewClick  : "+sliderview.getId());
		
	}

}
