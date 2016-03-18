package cn.jarlen.android.view.sliderview;

import java.io.File;

import cn.jarlen.android.view.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * When you want to make your own slider view, you must extends from this class.
 * BaseSliderView provides some useful methods. I provide two example:
 * {@link com.PagerItemImage.common.uimodule.slider.SliderTypes.DefaultSliderView}
 * and {@link com.marshalchen.common.uimodule.slider.SliderTypes.TextSliderView}
 * if you want to show progressbar, you just need to set a progressbar id as
 * 
 * @+id/loading_bar.
 * 
 *                   图片Bitmap宽高�? height:134dp width�?360dp
 * 
 */
public abstract class BaseSliderView {

	protected Context mContext;

	private Bundle mBundle;

	/**
	 * Error place holder image.
	 */
	private int mErrorPlaceHolderRes;

	/**
	 * Empty imageView placeholder.
	 */
	private int mEmptyPlaceHolderRes;

	private String mUrl;
	private File mFile;
	private int mRes;

	private boolean mErrorDisappear;

	private ImageLoadListener mLoadListener;

	private String mDescription;

	private String id = null;

	private View currentView = null;

	/**
	 * Scale type of the image.
	 */
	private ScaleType mScaleType = ScaleType.Fit;

	public enum ScaleType {
		CenterCrop, CenterInside, Fit, FitCenterCrop
	}

	protected BaseSliderView(Context context) {
		mContext = context;
		this.mBundle = new Bundle();
	}

	/**
	 * the placeholder image when loading image from url or file.
	 * 
	 * @param resId
	 *            Image resource id
	 * @return
	 */
	public BaseSliderView empty(int resId) {
		mEmptyPlaceHolderRes = resId;
		return this;
	}

	/**
	 * determine whether remove the image which failed to download or load from
	 * file
	 * 
	 * @param disappear
	 * @return
	 */
	public BaseSliderView errorDisappear(boolean disappear) {
		mErrorDisappear = disappear;
		return this;
	}

	/**
	 * if you set errorDisappear false, this will set a error placeholder image.
	 * 
	 * @param resId
	 *            image resource id
	 * @return
	 */
	public BaseSliderView error(int resId) {
		mErrorPlaceHolderRes = resId;
		return this;
	}

	/**
	 * the description of a slider image.
	 * 
	 * @param description
	 * @return
	 */
	public BaseSliderView description(String description) {
		mDescription = description;
		return this;
	}

	/**
	 * set the id of a slider
	 * 
	 * @param id
	 * @return
	 */
	public BaseSliderView setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * get the id of a slider
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * set a url as a image that preparing to load
	 * 
	 * @param url
	 * @return
	 */
	public BaseSliderView image(String url) {
		if (mFile != null || mRes != 0) {
			throw new IllegalStateException("Call multi image function,"
					+ "you only have permission to call it once");
		}
		mUrl = url;
		return this;
	}

	/**
	 * set a file as a image that will to load
	 * 
	 * @param file
	 * @return
	 */
	public BaseSliderView image(File file) {
		if (mUrl != null || mRes != 0) {
			throw new IllegalStateException("Call multi image function,"
					+ "you only have permission to call it once");
		}
		mFile = file;
		return this;
	}

	public BaseSliderView image(int res) {
		if (mUrl != null || mFile != null) {
			throw new IllegalStateException("Call multi image function,"
					+ "you only have permission to call it once");
		}
		mRes = res;
		return this;
	}

	public String getUrl() {
		return mUrl;
	}

	public boolean isErrorDisappear() {
		return mErrorDisappear;
	}

	public int getEmpty() {
		return mEmptyPlaceHolderRes;
	}

	public int getError() {
		return mErrorPlaceHolderRes;
	}

	public String getDescription() {
		return mDescription;
	}

	public Context getContext() {
		return mContext;
	}

	/**
	 * When you want to implement your own slider view, please call this method
	 * in the end in `getView()` method
	 * 
	 * @param v
	 *            the whole view
	 * @param targetImageView
	 *            where to place image
	 */
	protected void bindEventAndShow(final View v, ImageView targetImageView) {
		currentView = v;
		if (targetImageView == null)
			return;

		final BaseSliderView me = this;

		if (mLoadListener != null) {
			mLoadListener.onStart(me);
		}

		Glide.with(mContext).load(getUrl()).fitCenter()
		.placeholder(R.drawable.icon_ad_default)
		.listener(new RequestListener<String, GlideDrawable>() {

			@Override
			public boolean onException(Exception arg0, String arg1,
					Target<GlideDrawable> arg2, boolean arg3) {
				// TODO Auto-generated method stub
				if (mLoadListener != null) {
					mLoadListener.onEnd(false, me);
				}
				return false;
			}

			@Override
			public boolean onResourceReady(GlideDrawable arg0,
					String arg1, Target<GlideDrawable> arg2,
					boolean arg3, boolean arg4) {

				if (mLoadListener != null) {
					mLoadListener.onEnd(true, me);
				}
				return false;
			}

		}).error(R.drawable.icon_ad_default).into(targetImageView);
	}

	public BaseSliderView setScaleType(ScaleType type) {
		mScaleType = type;
		return this;
	}

	public ScaleType getScaleType() {
		return mScaleType;
	}

	/**
	 * the extended class have to implement getView(), which is called by the
	 * adapter, every extended class response to render their own view.
	 * 
	 * @return
	 */
	public abstract View getView();

	/**
	 * set a listener to get a message , if load error.
	 * 
	 * @param l
	 */
	public void setOnImageLoadListener(ImageLoadListener l) {
		mLoadListener = l;
	}

	public interface OnSliderClickListener {
		public void onSliderClick(BaseSliderView slider);
	}

	/**
	 * when you have some extra information, please put it in this bundle.
	 * 
	 * @return
	 */
	public Bundle getBundle() {
		return mBundle;
	}

	public interface ImageLoadListener {
		public void onStart(BaseSliderView target);

		public void onEnd(boolean result, BaseSliderView target);
	}

	public View getCurrentView() {
		return currentView;
	}

}
