package com.tlh.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.zhangyuhuishou.zyhs.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//模拟海底气泡上升的自定义控件
public class BubbleLayout extends View {

	private List<Bubble> bubbles = new ArrayList<>();
	private Random random = new Random();//生成随机数
	private int width, height;
	private boolean starting;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private boolean mStarting = false;
	private static final int MSG_CREATE_BUBBLE = 0;

	public BubbleLayout(Context context) {
		super(context);
		initParams();
	}

	public BubbleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParams();
	}

	public BubbleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initParams();
	}

	private void initParams(){
		paint.setColor(getResources().getColor(R.color.theme_color,null));
		paint.setAlpha(200);//设置不透明度：透明为0，完全不透明为255
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		width = getWidth();
		height = getHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		//画气泡
		drawBubble(canvas);
		invalidate();
	}

	/**
	 * 画气泡
	 */
	private void drawBubble(Canvas canvas) {
		//绘制气泡
		List<Bubble> list = new ArrayList<>(bubbles);
		//依次绘制气泡
		for (Bubble bubble : list) {
			//碰到上边界从数组中移除
			if (bubble.getY() - bubble.getSpeedY() <= 0) {
				bubbles.remove(bubble);
			}
			//碰到左边界从数组中移除
			else if(bubble.getX() - bubble.getRadius() <= 0) {
				bubbles.remove(bubble);
			}
			//碰到右边界从数组中移除
			else if(bubble.getX() + bubble.getRadius() >= width) {
				bubbles.remove(bubble);
			}
			else {
				int i = bubbles.indexOf(bubble);
				if (bubble.getX() + bubble.getSpeedX() <= bubble.getRadius()) {
					bubble.setX(bubble.getRadius());
				} else if (bubble.getX() + bubble.getSpeedX() >= width - bubble.getRadius()) {
					bubble.setX(width - bubble.getRadius());
				} else {
					bubble.setX(bubble.getX() + bubble.getSpeedX());
				}
				bubble.setY(bubble.getY() - bubble.getSpeedY());
				//海底溢出的甲烷上升过程越来越大（气压减小）
				//鱼类和潜水员吐出的气体却会越变越小（被海水和藻类吸收）
				//如果考虑太多现实情景的话，代码量就会变得很大，也容易出现bug
				//感兴趣的读者可以自行添加
				bubble.setRadius(bubble.getRadius());
				bubbles.set(i, bubble);
				canvas.drawCircle(bubble.getX(), bubble.getY(), bubble.getRadius(), paint);
				canvas.drawCircle(bubble.getX() - 20, bubble.getY() - 15, 5, paint);
				canvas.drawArc(bubble.getX() - 20,bubble.getY() - 30,bubble.getX() + 20,bubble.getY() - 20,-90,90,false,paint);
			}
		}
	}


	/**
	 * 开始气泡上升动画
	 */
	public void startAnim() {
		if (!mStarting) {
			try {
				mStarting = true;
				//此处开始需要通知不断绘制
				invalidate();
				//发送消息生成气泡
				mHandler.sendEmptyMessage(MSG_CREATE_BUBBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止气泡上升动画
	 */
	public void stopAnim() {
		if (mStarting) {
			mStarting = false;
			mHandler.removeMessages(MSG_CREATE_BUBBLE);
			bubbles.clear();
			invalidate();
		}
	}

	@Override
	public void invalidate() {
		if (mStarting) {
			super.invalidate();
		}
	}

	private class Bubble {
		//气泡半径 
		private float radius;
		//上升速度
		private float speedY;
		//平移速度
		private float speedX;
		//气泡x坐标
		private float x;
		// 气泡y坐标
		private float y;

		private float getRadius() {
			return radius;
		}

		private void setRadius(float radius) {
			this.radius = radius;
		}

		private float getX() {
			return x;
		}

		private void setX(float x) {
			this.x = x;
		}

		private float getY() {
			return y;
		}

		private void setY(float y) {
			this.y = y;
		}

		private float getSpeedY() {
			return speedY;
		}

		private void setSpeedY(float speedY) {
			this.speedY = speedY;
		}

		private float getSpeedX() {
			return speedX;
		}

		private void setSpeedX(float speedX) {
			this.speedX = speedX;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_CREATE_BUBBLE) {
				if (mStarting) {
					if (width > 0) {
						Bubble bubble = new Bubble();
						int radius = random.nextInt(20) + 30;
						float speedY = random.nextFloat() * 5;
						while (speedY < 1) {
							speedY = random.nextFloat() * 5;
						}
						bubble.setRadius(radius);
						bubble.setSpeedY(speedY);
						bubble.setX(width / 2);
						bubble.setY(height);
						float speedX = random.nextFloat() - 0.5f;
						while (speedX == 0) {
							speedX = random.nextFloat() - 0.5f;
						}
						bubble.setSpeedX(speedX * 2);
						bubbles.add(bubble);
					}
					//下次产生气泡的时间
					mHandler.sendEmptyMessageDelayed(MSG_CREATE_BUBBLE, (long) ((random.nextInt(2) + 1) * 1000));
				}
			}
		}
	};
}