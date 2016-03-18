package cn.jarlen.android.view.sliderview;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.transform.Transformer;

import cn.jarlen.android.view.R;
import cn.jarlen.android.view.sliderview.BaseAnimationInterface;
import cn.jarlen.android.view.sliderview.BaseSliderView;
import cn.jarlen.android.view.sliderview.BaseTransformer;
import cn.jarlen.android.view.sliderview.FixedSpeedScroller;
import cn.jarlen.android.view.sliderview.SliderAdapter;
import cn.jarlen.android.view.sliderview.SliderAdapter.OnSliderViewClickListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

/**
 * SliderLayout is compound layout. This is combined with
 * {@link cn.jarlen.android.view.SliderPagerIndicator.common.uimodule.slider.Indicators.PagerIndicator}
 * and
 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
 * .
 * 
 * There is some properties you can set in XML:
 * 
 * indicator_visibility visible invisible
 * 
 * indicator_shape oval rect
 * 
 * indicator_selected_color
 * 
 * indicator_unselected_color
 * 
 * indicator_selected_drawable
 * 
 * indicator_unselected_drawable
 * 
 * pager_animation Default Accordion Background2Foreground CubeIn DepthPage Fade
 * FlipHorizontal FlipPage Foreground2Background RotateDown RotateUp Stack
 * Tablet ZoomIn ZoomOutSlide ZoomOut
 * 
 * pager_animation_span
 * 
 * 
 */
public class SliderView extends RelativeLayout {
	private static String TAG = "SliderView";

	private Context mContext;
	/**
	 * InfiniteViewPager is extended from ViewPagerEx. As the name says, it can
	 * scroll without bounder.
	 */
	private SliderViewPager mSliderViewPager;

	/**
	 * InfiniteViewPager adapter.
	 */
	private SliderAdapter mSliderAdapter;

	/**
	 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
	 * indicator.
	 */
	private SliderPagerIndicator mIndicator;

	/**
	 * A timer and a TimerTask using to cycle the
	 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
	 * .
	 */
	private Timer mCycleTimer;
	private TimerTask mCycleTask;

	/**
	 * For resuming the cycle, after user touch or click the
	 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
	 * .
	 */
	private Timer mResumingTimer;
	private TimerTask mResumingTask;

	/**
	 * If
	 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
	 * is Cycling
	 */
	private boolean mCycling;

	/**
	 * Determine if auto recover after user touch the
	 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
	 */
	private boolean mAutoRecover = true;

	private int mTransformerId;

	/**
	 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
	 * transformer time span.
	 */
	private int mTransformerSpan = 1100;

	private boolean mAutoCycle;

	private final long recycleTime = 5 * 1000;

	/**
	 * the duration between animation.
	 */
	private long mSliderDuration = recycleTime;

	/**
	 * Visibility of
	 * {@link cn.jarlen.android.view.SliderPagerIndicator.common.uimodule.slider.Indicators.PagerIndicator}
	 */
	private SliderPagerIndicator.IndicatorVisibility mIndicatorVisibility = SliderPagerIndicator.IndicatorVisibility.Visible;

	/**
	 * {@link com.soft.slider.widget.Transformers.marshalchen.common.uimodule.slider.Tricks.ViewPagerEx}
	 * 's transformer
	 */
	private BaseTransformer mViewPagerTransformer;

	/**
	 * @see com.marshalchen.common.uimodule.slider.Animations.BaseAnimationInterface
	 */
	private BaseAnimationInterface mCustomAnimation;

	/**
	 * 是否可以左右轮播
	 */
	public boolean canSlide = false;

	public enum PresetIndicators {
		Center_Bottom("Center_Bottom", R.id.default_center_bottom_indicator), Right_Bottom(
				"Right_Bottom", R.id.default_bottom_right_indicator), Left_Bottom(
				"Left_Bottom", R.id.default_bottom_left_indicator), Center_Top(
				"Center_Top", R.id.default_center_top_indicator), Right_Top(
				"Right_Top", R.id.default_center_top_right_indicator), Left_Top(
				"Left_Top", R.id.default_center_top_left_indicator);

		private final String name;
		private final int id;

		private PresetIndicators(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public String toString() {
			return name;
		}

		public int getResourceId() {
			return id;
		}
	}

	/**
	 * preset transformers and their names
	 */
	public enum Transformer {
		Default("Default"), Accordion("Accordion"), Background2Foreground(
				"Background2Foreground"), CubeIn("CubeIn"), DepthPage(
				"DepthPage"), Fade("Fade"), FlipHorizontal("FlipHorizontal"), FlipPage(
				"FlipPage"), Foreground2Background("Foreground2Background"), RotateDown(
				"RotateDown"), RotateUp("RotateUp"), Stack("Stack"), Tablet(
				"Tablet"), ZoomIn("ZoomIn"), ZoomOutSlide("ZoomOutSlide"), ZoomOut(
				"ZoomOut");

		private final String name;

		private Transformer(String s) {
			name = s;
		}

		public String toString() {
			return name;
		}

		public boolean equals(String other) {
			return (other == null) ? false : name.equals(other);
		}
	};

	private Handler showNextSliderHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mSliderViewPager != null) {
				mSliderViewPager.nextItem();
			}
		};
	};

	public SliderView(Context context) {
		this(context, null);
	}

	public SliderView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.SliderStyle);
	}

	public SliderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.layout_slider_view, this,
				true);

		final TypedArray attributes = context.getTheme()
				.obtainStyledAttributes(attrs, R.styleable.SliderView,
						defStyle, 0);

		mTransformerSpan = attributes.getInteger(
				R.styleable.SliderView_pager_animation_span, 1100);
		mTransformerId = attributes.getInt(
				R.styleable.SliderView_pager_animation,
				Transformer.Default.ordinal());
		mAutoCycle = attributes.getBoolean(R.styleable.SliderView_auto_cycle,
				true);
		mSliderDuration = attributes.getInteger(
				R.styleable.SliderView_cycle_duration, 3000);

		int visibility = attributes.getInt(
				R.styleable.SliderView_indicator_visibility, 0);
		for (SliderPagerIndicator.IndicatorVisibility v : SliderPagerIndicator.IndicatorVisibility
				.values()) {
			if (v.ordinal() == visibility) {
				mIndicatorVisibility = v;
				break;
			}
		}

		mSliderViewPager = (SliderViewPager) findViewById(R.id.slider_viewpager);
		mSliderViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_UP:
					recoverCycle();
					break;
				}
				return false;
			}
		});

		attributes.recycle();

		setPresetIndicator(PresetIndicators.Right_Bottom);
		setSliderTransformDuration(mTransformerSpan, null);

		mSliderAdapter = new SliderAdapter(mContext);
		mSliderViewPager.setAdapter(mSliderAdapter);

		if (mIndicator != null) {
			mIndicator.setViewPager(mSliderViewPager);
		}

		setIndicatorVisibility(mIndicatorVisibility);
	}

	/**
	 * 获取当前SliderView数据适配器
	 * 
	 * @return
	 */
	public SliderAdapter getSliderAdapter() {
		return mSliderAdapter;
	}

	public void updateSliderAdapter(SliderAdapter sliderAdapter) {
		this.mSliderAdapter = sliderAdapter;
		mSliderViewPager.setAdapter(mSliderAdapter);
		if (mIndicator != null) {
			mIndicator.setViewPager(mSliderViewPager);
		}

		if (mSliderViewPager != null) {
			mSliderViewPager
					.setCurrentItem(mSliderAdapter.getRealCount() * 100);
		}

		if (mSliderAdapter.getRealCount() < 2) {
			setIndicatorVisibility(SliderPagerIndicator.IndicatorVisibility.Invisible);
		} else {
			setIndicatorVisibility(SliderPagerIndicator.IndicatorVisibility.Visible);
		}
		startAutoCycle();
	}

	/**
	 * 设置SliderView点击事件监听
	 * 
	 * @param onSliderViewClickListener
	 */
	public void setOnSliderViewClickListener(
			OnSliderViewClickListener onSliderViewClickListener) {
		if (mSliderAdapter != null) {
			mSliderAdapter
					.setOnSliderViewClickListener(onSliderViewClickListener);
		}
	}

	/**
	 * 添加SliderView数据
	 * 
	 * @param list
	 */
	public void addSliderViews(List<BaseSliderView> list) {

		if (list == null) {
			return;
		}

		if (mSliderAdapter != null) {
			mSliderAdapter.removeSliderViews();
			mSliderAdapter.addSliderViews(list);
		}

		if (mSliderViewPager != null) {
			mSliderViewPager.setCurrentItem(list.size() * 100);
		}

		if (mSliderAdapter.getRealCount() < 2) {
			setIndicatorVisibility(SliderPagerIndicator.IndicatorVisibility.Invisible);
		} else {
			setIndicatorVisibility(SliderPagerIndicator.IndicatorVisibility.Visible);
		}
		startAutoCycle();
	}

	public void startAutoCycle() {
		startAutoCycle(recycleTime, mSliderDuration, mAutoRecover);
	}

	public void startAutoCycle(long delay, long duration, boolean autoRecover) {

		if (mCycleTimer != null) {
			mCycleTimer.cancel();
		}

		if (mCycleTask != null) {
			mCycleTask.cancel();
		}

		if (mResumingTask != null) {
			mResumingTask.cancel();
		}

		if (mResumingTimer != null) {
			mResumingTimer.cancel();
		}

		if (mSliderAdapter != null) {
			if (mSliderAdapter.getRealCount() < 2) {
				return;
			}
		}

		mSliderDuration = duration;
		mCycleTimer = new Timer();
		mAutoRecover = autoRecover;
		mCycleTask = new TimerTask() {
			@Override
			public void run() {
				showNextSliderHandler.sendEmptyMessage(0);
			}
		};

		mCycleTimer.schedule(mCycleTask, delay, mSliderDuration);
		mCycling = true;
		mAutoCycle = true;
	}

	public void recoverCycle() {
		if (!mAutoRecover || !mAutoCycle) {
			Log.i(TAG, "recoverCycle");
			return;
		}

		if (mSliderAdapter != null) {
			if (mSliderAdapter.getRealCount() < 2) {
				return;
			}
		}

		if (!mCycling) {
			if (mResumingTask != null && mResumingTimer != null) {
				mResumingTimer.cancel();
				mResumingTask.cancel();
			}
			mResumingTimer = new Timer();
			mResumingTask = new TimerTask() {
				@Override
				public void run() {
					startAutoCycle();
				}
			};
			mResumingTimer.schedule(mResumingTask, recycleTime);
		}
	}

	/**
	 * pause auto cycle.
	 */
	public void pauseAutoCycle() {
		if (mCycling) {
			mCycleTimer.cancel();
			mCycleTask.cancel();
			mCycling = false;
		} else {
			if (mResumingTimer != null && mResumingTask != null) {
				recoverCycle();
			}
		}
	}

	private void setPresetIndicator(PresetIndicators presetIndicator) {
		SliderPagerIndicator pagerIndicator = (SliderPagerIndicator) findViewById(presetIndicator
				.getResourceId());
		setCustomIndicator(pagerIndicator);
	}

	private void setCustomIndicator(SliderPagerIndicator indicator) {
		if (mIndicator != null) {
			mIndicator.destroySelf();
		}
		mIndicator = indicator;
		mIndicator.setIndicatorVisibility(mIndicatorVisibility);
	}

	/**
	 * set the duration between two slider changes.
	 * 
	 * @param period
	 * @param interpolator
	 */
	private void setSliderTransformDuration(int period,
			Interpolator interpolator) {
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					mSliderViewPager.getContext(), interpolator, period);
			mScroller.set(mSliderViewPager, scroller);
		} catch (Exception e) {

		}
	}

	/**
	 * Inject your custom animation into PageTransformer, you can know more
	 * details in
	 * {@link com.marshalchen.common.uimodule.slider.Animations.BaseAnimationInterface}
	 * , and you can see a example in
	 * {@link com.marshalchen.common.uimodule.slider.Animations.DescriptionAnimation}
	 * 
	 * @param animation
	 */
	public void setCustomAnimation(BaseAnimationInterface animation) {
		mCustomAnimation = animation;
		if (mViewPagerTransformer != null) {
			mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
		}
	}

	/**
	 * pretty much right? enjoy it. :-D
	 * 
	 * @param ts
	 */
	public void setPresetTransformer(BaseTransformer t) {
		setPagerTransformer(true, t);
	}

	/**
	 * set ViewPager transformer.
	 * 
	 * @param reverseDrawingOrder
	 * @param transformer
	 */
	public void setPagerTransformer(boolean reverseDrawingOrder,
			BaseTransformer transformer) {
		mViewPagerTransformer = transformer;
		mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
		mSliderViewPager.setPageTransformer(reverseDrawingOrder,
				mViewPagerTransformer);
	}

	/**
	 * get the current item position
	 * 
	 * @return
	 */
	public int getCurrentPosition() {

		if (getSliderAdapter() == null)
			throw new IllegalStateException("You did not set a slider adapter");

		return mSliderViewPager.getCurrentItem()
				% getSliderAdapter().getCount();

	}

	/**
	 * Set the visibility of the indicators.
	 * 
	 * @param visibility
	 */
	public void setIndicatorVisibility(
			SliderPagerIndicator.IndicatorVisibility visibility) {
		if (mIndicator == null) {
			return;
		}

		mIndicator.setIndicatorVisibility(visibility);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			pauseAutoCycle();
			break;
		case MotionEvent.ACTION_UP:
			startAutoCycle();
			break;
		}
		return false;
	}
}
