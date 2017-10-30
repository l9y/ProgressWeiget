package com.ztstech.progressweiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhiyuan on 2017/10/30.
 */

public class PointerProgressView extends View {

    private Paint mPaint;
    /**最大为1*/
    private float progress = 0.0f;
    /**第二进度*/
    private float secondaryProgress;

    /**
     * 标记位置
     */
    private List<PointerItem> pointerItems = new ArrayList<>();

    /**进度条背景色，默认透明*/
    private int progressBackground = 0x00000000;
    /**进度条颜色*/
    private int progressColor = 0xffff0000;
    /**进度条高度*/
    private int progressHeight = 20;
    /**指针颜色*/
    private int pointerColor = 0xffff0000;
    /**指针文字颜色*/
    private int pointerTextColor = 0xffff0000;
    /**指针文本颜色*/
    private int pointerTextSize = 20;
    /**指针距离下面大小*/
    private int pointerMargin = 8;
    /**指针内边距*/
    private int pointerPadding = 8;
    /**指针三角形高度*/
    private int pointerHeight = 16;

    /**绘制进度条矩形*/
    private RectF mRectf;
    private Path mPath;

    private PointerTextFormatter textFormatter;

    public PointerProgressView(Context context) {
        this(context, null, 0);
    }

    public PointerProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointerProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mRectf = new RectF();
        mPath = new Path();

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.PointerProgressView);
            progressBackground = typedArray.getColor(
                    R.styleable.PointerProgressView_progressBackground, 0x00000000);
            progressHeight = typedArray.getDimensionPixelSize(
                    R.styleable.PointerProgressView_progressHeight, 20);
            progressColor = typedArray.getColor(R.styleable.PointerProgressView_progressColor,
                    0xffff0000);
            pointerColor = typedArray.getColor(R.styleable.PointerProgressView_pointerColor,
                    0xffff0000);
            pointerTextColor = typedArray.getColor(R.styleable.PointerProgressView_pointerTextColor,
                    0xff000000);
            pointerTextSize = typedArray.getDimensionPixelSize(
                    R.styleable.PointerProgressView_pointerTextSize, 20);
            pointerMargin = typedArray.getDimensionPixelSize(
                    R.styleable.PointerProgressView_pointerMargin, 8);
            pointerPadding = typedArray.getDimensionPixelSize(
                    R.styleable.PointerProgressView_pointerPadding, 8);
            pointerHeight = typedArray.getDimensionPixelSize(
                    R.styleable.PointerProgressView_pointerHeight, 16);
            typedArray.recycle();
        }

        mPaint.setTextSize(pointerTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        //最小高度
        int minHeight = progressHeight + pointerMargin + pointerTextSize + 2 * pointerPadding;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (MeasureSpec.UNSPECIFIED == heightMode) {
            height = minHeight;
        }else if (MeasureSpec.AT_MOST == heightMode) {
            height = Math.min(minHeight, height);
        }



        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        String text;
        if (textFormatter == null) {
            text = String.valueOf((int) (progress * 100));
        }else {
            text = textFormatter.getFormatter(progress);
        }


        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float currentProgress = progress * width;

        mPaint.setColor(progressBackground);
        mPaint.setStrokeWidth(progressHeight);
        mPaint.setStyle(Paint.Style.FILL);
        //先画背景
        mRectf.set(0, height - progressHeight, width, height);
        canvas.drawRect(mRectf, mPaint);

        //画进度
        mPaint.setColor(progressColor);
        mRectf.set(0, height - progressHeight, currentProgress, height);
        canvas.drawRect(mRectf, mPaint);



        for (PointerItem item : pointerItems) {
            //绘制item
            float pointerX = item.position * width;
            drawPointer(canvas, item.text, pointerX, item.pointerColor, item.textColor);
        }


        //画当前进度标签
        drawPointer(canvas, text, currentProgress, pointerColor, pointerTextColor);

    }

    /**
     * 绘制标签
     * @param canvas 画布
     * @param text 文本
     * @param x 标签中心x位置
     * @param currentPointerColor 标签颜色
     * @param currentPointerTextColor 标签文本颜色
     */
    private void drawPointer(Canvas canvas, String text, float x, int currentPointerColor,
                             int currentPointerTextColor) {
        mPath.reset();
        mPath.moveTo(x, canvas.getHeight() - progressHeight - pointerMargin);

        //测量文本宽度
        float textWidth = mPaint.measureText(text);

        float minX = x - (textWidth / 2 + (float) pointerPadding);
        float maxX = x + (textWidth / 2 + (float) pointerPadding);
        float maxY = canvas.getHeight() - progressHeight - pointerMargin - pointerHeight;
        float minY = maxY - pointerTextSize - 2 * pointerPadding;
        mPath.lineTo(minX, maxY);
        mPath.lineTo(minX, minY);
        mPath.lineTo(maxX, minY);
        mPath.lineTo(maxX, maxY);
        mPath.lineTo(x, canvas.getHeight() - progressHeight - pointerMargin);
        mPath.close();

        mPaint.setColor(currentPointerColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath, mPaint);

        //绘制文本
        mPaint.setColor(currentPointerTextColor);
        canvas.drawText(text, x, minY + pointerPadding + pointerTextSize, mPaint);

    }

    public void setProgress(int progress) {
        this.progress = (float) progress / 100.0f;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setSecondaryProgress(int progress) {

    }

    /**
     * 设置文本显示效果
     * @param formatter
     */
    public void setTextFormatter(PointerTextFormatter formatter) {
        this.textFormatter = formatter;
    }

    public void addPointer(List<PointerItem> items) {
        pointerItems.addAll(items);
    }

    public void addPointer(PointerItem item) {
        pointerItems.add(item);
    }

    public void removePointer(PointerItem item) {
        pointerItems.remove(item);
    }

    public void removePointer(int id) {
        Iterator<PointerItem> itemIterator = pointerItems.iterator();
        while (itemIterator.hasNext()) {
            PointerItem item = itemIterator.next();
            if (item.id == id) {
                itemIterator.remove();;
            }
        }
    }

    public void removeAllPointer() {
        pointerItems.clear();
    }

    /**
     * 增加标记点
     */
    public static class PointerItem {

        /**
         * 所处位置，百分比
         */
        public float position;

        /**
         * 显示文本
         */
        public String text;

        /**
         * 文本颜色
         */
        public int textColor;

        /**
         * 指针颜色
         */
        public int pointerColor;

        /**
         * 指针id
         */
        public int id;
    }

    public interface PointerTextFormatter {

        /**
         * 获取显示文本
         * @param progress
         * @return
         */
        String getFormatter(float progress);
    }
}
