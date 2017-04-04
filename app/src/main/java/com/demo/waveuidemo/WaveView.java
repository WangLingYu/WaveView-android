package com.demo.waveuidemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wang on 2017/4/4.
 */
//没有用到贝塞尔曲线
//选择继承view，在实现的过程中我们需要关注如下几个方法：
//1.onMeasure():最先回调，用于控件的测量;
//2.onSizeChanged():在onMeasure后面回调，可以拿到view的宽高等数据，在横竖屏切换时也会回调;
//3.onDraw()：真正的绘制部分，绘制的代码都写到这里面;
//既然如此，我们先复写这三个方法，然后来实现如上两个效果；
//一：标准正余弦水波纹
//这种水波纹可以用具体函数模拟出具体的轨迹，所以思路基本如下：
//1.确定水波函数方程
//2.根据函数方程得出每一个波纹上点的坐标；
//3.将水波进行平移，即将水波上的点不断的移动；
//4.不断的重新绘制，生成动态水波纹；
//有了上面的思路，我们一步一步进行实现：
public class WaveView extends View {

    //周期
    float mCycleFactorw;
    //每个波浪上坐标点要显示的位置
    float[] mYPositions;
    //每次重新绘制的开始图形数组
    float[] mResetYPositions;
    //总宽度，总高度
    int mTotalHeight, mTotalWidth;
    Paint mWavePaint;

    //移动速度和应该开始移动的点开始的下标
    int mXOffsetSpeedOne, mXOneOffset;

    public WaveView(Context context) {
        super(context);
        initPaint();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //选取view宽度为一个周期
        mCycleFactorw = (float) (2 * Math.PI / w);
        mTotalHeight = h;
        mTotalWidth = w;
        mYPositions = new float[w];
        //求出每一个点对应的Y坐标
        //正余弦函数方程为：
        //y = Asin(wx+b)+h ，这个公式里：w影响周期，A影响振幅，h影响y位置，b为初相；
        for (int i = 0; i < w; i++) {
            mYPositions[i] = (float) (40 * Math.sin(mCycleFactorw * i) + 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        resetPositionY();
        for (int i = 0; i < mYPositions.length; i++) {
            //起始点就是波浪线上的点，终点就是View总高度的一条线，也就是View底部,下面起点的Y坐标-300可以看做是波浪深度，可以作为变量动态改变达到波浪慢慢上升的效果
            canvas.drawLine(i, mTotalHeight - mResetYPositions[i] - 300, i, mTotalHeight, mWavePaint);
        }

        mXOneOffset += mXOffsetSpeedOne;

        if (mXOneOffset > mTotalWidth) {
            mXOneOffset = 0;
        }
        postInvalidate();
    }

    private void resetPositionY() {

        //y间距,新数组的长度
        int yInterval = mYPositions.length - mXOneOffset;
        mResetYPositions = new float[mTotalWidth];
        //先是将mYPositions往前平移的波长度复制给mResetYPositions数组，长度为yInterval
        System.arraycopy(mYPositions, mXOneOffset, mResetYPositions, 0, yInterval);
        //然后将mYPositions剩下的复制给mResetYPositions数组
        System.arraycopy(mYPositions, 0, mResetYPositions, yInterval, mXOneOffset);
        //相当于把一个完整波形的一部分截取到数组后面，把剩下部分放到数组前面
    }

    private void initPaint() {
        mXOffsetSpeedOne = 10;
        mWavePaint = new Paint();

        mWavePaint.setAntiAlias(true);

        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setColor(getResources().getColor(android.R.color.holo_blue_light));
    }
}
