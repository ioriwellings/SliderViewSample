package cn.jarlen.android.view.sliderview;
import cn.jarlen.android.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * a simple slider view, which just show an image. If you want to make your own
 * slider view,
 * 
 * just extend BaseSliderView, and implement getView() method.
 */
public class DefaultSliderView extends BaseSliderView {

	public DefaultSliderView(Context context) {
		super(context);
	}

	@Override
	public View getView() {
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.widget_slider_render_type_img, null);
		ImageView target = (ImageView) view.findViewById(R.id.slide_item_img);
		bindEventAndShow(view, target);
		return view;
	}
}
