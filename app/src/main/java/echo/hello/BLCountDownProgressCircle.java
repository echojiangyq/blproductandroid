package echo.hello;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 倒计时进度圆环
 *
 * @author JiangYaqiang
 * createrd at 2018/12/15 14:08
 */
public class BLCountDownProgressCircle extends View {
    /**
     * 进度条最大值
     */
    private int maxValue = 100;

    /**
     * 当前进度值
     */
    private int currentValue = 0;

    /**
     * 每次扫过的角度，用来设置进度条圆弧所对应的圆心角，alphaAngle=(currentValue/maxValue)*360
     */
    private float alphaAngle = 0f;

    /**
     * 底部圆弧的颜色，默认为Color.LTGRAY
     */
    private int firstColor = getResources().getColor(R.color.themeYellow);

    /**
     * 进度条圆弧块的颜色
     */
    private int secondColor = Color.WHITE;
    /**
     * 中间文字颜色(默认蓝色)
     */
    private int centerTextColor = getResources().getColor(R.color.themeYellow);
    /**
     * 中间文字的字体大小(默认40dp)
     */
    private int centerTextSize = BLConvertUtils.dip2px(getContext(), 13);

    /**
     * 圆环的宽度
     */
    private int circleWidth = BLConvertUtils.dip2px(getContext(), 2);

    /**
     * 画圆弧的画笔
     */
    private Paint circlePaint;

    /**
     * 画文字的画笔
     */
    private Paint textPaint;
    /**
     * 是否使用渐变色
     */
    private boolean isShowGradient = false;
    /**
     * 文字，如果不为空表示使用倒计时
     */
    private String centerText = null;
    /**
     * 渐变圆周颜色数组
     */
    private int[] colorArray = new int[]{Color.parseColor("#2773FF"), Color.parseColor("#27C0D2"), Color.parseColor("#40C66E")};
    private int duration;
    private OnFinishListener listener;
    public BLCountDownProgressCircle(Context context) {
        this(context, null);
    }
    public BLCountDownProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public BLCountDownProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BLCountDownProgressCircle, defStyleAttr, 0);
        int n = ta.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.BLCountDownProgressCircle_countDown_firstColor:
                    firstColor = ta.getColor(attr, getResources().getColor(R.color.themeYellow)); // 默认圆环颜色为主题色 
                    break;
                case R.styleable.BLCountDownProgressCircle_countDown_secondColor:
                    secondColor = ta.getColor(attr, Color.WHITE); // 默认底色为透明色
                    break;
                case R.styleable.BLCountDownProgressCircle_countDown_centerTextSize:
                    centerTextSize = ta.getDimensionPixelSize(attr, BLConvertUtils.dip2px(getContext(), 13)); // 默认中间文字字体大小为13dp
                    break;
                case R.styleable.BLCountDownProgressCircle_countDown_circleWidth:
                    circleWidth = ta.getDimensionPixelSize(attr, BLConvertUtils.dip2px(getContext(), 2)); // 默认圆弧宽度为2dp
                    break;
                case R.styleable.BLCountDownProgressCircle_countDown_centerTextColor:
                    centerTextColor = ta.getColor(attr, getResources().getColor(R.color.themeYellow)); // 默认中间文字颜色为主题色
                    break;
                case R.styleable.BLCountDownProgressCircle_countDown_isShowGradient:
                    isShowGradient = ta.getBoolean(attr, false); // 默认不适用渐变色
                    break;
                case R.styleable.BLCountDownProgressCircle_countDown_text:
                    centerText = ta.getString(attr);
                    break;
                default:
                    break;
            }
        }
        ta.recycle();

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true); // 抗锯齿
        circlePaint.setDither(true); // 防抖动
        circlePaint.setStrokeWidth(circleWidth);//画笔宽度

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 分别获取期望的宽度和高度，并取其中较小的尺寸作为该控件的宽和高,并且不超过屏幕宽高
        int widthPixels = this.getResources().getDisplayMetrics().widthPixels;//获取屏幕宽
        int heightPixels = this.getResources().getDisplayMetrics().heightPixels;//获取屏幕高
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int minWidth = Math.min(widthPixels, width);
        int minHedight = Math.min(heightPixels, height);
        setMeasuredDimension(Math.min(minWidth, minHedight), Math.min(minWidth, minHedight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = this.getWidth() / 2;
        int radius = center - circleWidth / 2;

        drawCircle(canvas, center, radius); // 绘制进度圆弧
        drawText(canvas, center);
    }

    /**
     * 绘制进度圆弧
     *
     * @param canvas 画布对象
     * @param center 圆心的x和y坐标
     * @param radius 圆的半径
     */
    private void drawCircle(Canvas canvas, int center, int radius) {
        circlePaint.setShader(null); // 清除上一次的shader
        circlePaint.setColor(firstColor); // 设置底部圆环的颜色，这里使用第一种颜色
        circlePaint.setStyle(Paint.Style.STROKE); // 设置绘制的圆为空心
        circlePaint.setStrokeWidth(circleWidth);//画笔宽度
        
        canvas.drawCircle(center, center, radius, circlePaint); // 画底部的空心圆
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); // 圆的外接正方形
        if (isShowGradient) {
            // 绘制颜色渐变圆环
            // shader类是Android在图形变换中非常重要的一个类。Shader在三维软件中我们称之为着色器，其作用是来给图像着色。
            LinearGradient linearGradient = new LinearGradient(circleWidth, circleWidth, getMeasuredWidth() - circleWidth, getMeasuredHeight() - circleWidth, colorArray, null, 
                    Shader.TileMode.MIRROR);
            circlePaint.setShader(linearGradient);
        }
        //circlePaint.setShadowLayer(10, 10, 10, Color.BLUE);
        //circlePaint.setStrokeCap(Paint.Cap.ROUND); // 把每段圆弧改成圆角的
        circlePaint.setColor(secondColor); // 设置圆弧的颜色
        circlePaint.setStrokeWidth(circleWidth+2);//画笔宽度
        
        alphaAngle = currentValue * 360.0f / maxValue * 1.0f; // 计算每次画圆弧时扫过的角度，这里计算要注意分母要转为float类型，否则alphaAngle永远为0
        canvas.drawArc(oval, -90, alphaAngle, false, circlePaint);
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布对象
     * @param center 圆心的x和y坐标
     */
    private void drawText(Canvas canvas, int center) {
        String percent;
        if (TextUtils.isEmpty(centerText)) {
            percent = String.valueOf(((maxValue - currentValue) / maxValue));
        } else {
            percent = centerText;
        }

        textPaint.setTextSize(centerTextSize); // 设置要绘制的文字大小
        textPaint.setTextAlign(Paint.Align.CENTER); // 设置文字居中，文字的x坐标要注意
        textPaint.setColor(centerTextColor); // 设置文字颜色

        textPaint.setStrokeWidth(0); // 注意此处一定要重新设置宽度为0,否则绘制的文字会重叠
        Rect bounds = new Rect(); // 文字边框
        textPaint.getTextBounds(percent, 0, percent.length(), bounds); // 获得绘制文字的边界矩形  
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt(); // 获取绘制Text时的四条线  
        int baseline = center + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom; // 计算文字的基线,方法见http://blog.csdn.net/harvic880925/article/details/50423762  
        canvas.drawText(percent, center, baseline, textPaint); // 绘制表示进度的文字
        if (maxValue == currentValue) {
            if (listener != null) {
                listener.onFinish();
            }
        }
    }

    /**
     * 设置圆环的宽度，默认2dp
     *
     * @param width
     */
    public void setCircleWidth(int width) {
        this.circleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
        circlePaint.setStrokeWidth(circleWidth);
        invalidate();
    }

    /**
     * 设置圆环的底色，默认透明
     *
     * @param color
     */
    public void setFirstColor(int color) {
        this.firstColor = color;
        circlePaint.setColor(firstColor);
        //一般只是希望在View发生改变时对UI进行重绘。invalidate()方法系统会自动调用 View的onDraw()方法。
        invalidate();
    }

    /**
     * 设置进度条的颜色，默认主题色
     *
     * @param color
     */
    public void setSecondColor(int color) {
        this.secondColor = color;
        circlePaint.setColor(secondColor);
        invalidate();
    }

    /**
     * 设置进度条渐变色颜色数组
     *
     * @param colors 颜色数组，类型为int[]
     */
    public void setColorArray(int[] colors) {
        this.colorArray = colors;
        invalidate();
    }


    public String getCenterText() {
        return centerText;
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        invalidate();
    }


    public void setDuration(int duration, OnFinishListener listener) {
        setDuration(duration, 0, listener);
    }

    public void setDuration(int duration, int offset, OnFinishListener listener) {
        this.listener = listener;
        this.duration = duration + 1000;

        int offsetPercent = offset * maxValue / duration;
        int leftDuration = duration - offset;

        ValueAnimator animator = ValueAnimator.ofInt(offsetPercent, maxValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(leftDuration);
        animator.start();
    }

    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }

    public interface OnFinishListener {
        void onFinish();
    }
}  