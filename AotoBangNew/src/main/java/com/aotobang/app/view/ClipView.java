package com.aotobang.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/**
 * �ü��߿�
 * @author AC
 *
 */
public class ClipView extends View {

	/**
	 * �߿�����ұ߽���룬���ڵ����߿򳤶�
	 */
	public static final int BORDERDISTANCE = 50;

	private Paint mPaint;
	private Context mContext;

	public ClipView(Context context) {
		this(context, null);
		mContext = context;
	}

	public ClipView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
	}

	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		int innerCircle = dip2px(mContext, 150); // ��Բ�뾶
		int ringWidth = height; // Բ�����

		// ��һ�ַ�������Բ��
		// ������Բ
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(2);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(width / 2, height / 2, innerCircle, mPaint);

		// ����Բ��
		mPaint.setColor(0xaa000000);
		mPaint.setStrokeWidth(ringWidth);
		canvas.drawCircle(width / 2, height / 2, innerCircle + 1 + ringWidth
				/ 2, mPaint);

	}

	/* �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
