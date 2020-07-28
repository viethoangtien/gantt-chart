package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;

import org.joda.time.DateTime;


/**
 * Created by nagamatsuayumu on 15/03/28.
 */
public class CalendarView extends View {
    private DateTime minDate;
    private DateTime maxDate;

    private Paint mCharPaint;
    private Paint mFillPaint;
    private Paint mLinePaint;

    private float scale;

    private int nx;
    private int dx;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scale = getContext().getResources().getDisplayMetrics().density;

        mCharPaint = new Paint();
        mCharPaint.setAntiAlias(true);
        mCharPaint.setColor(getResources().getColor(R.color.color_white));
        mCharPaint.setTextSize(30.f * scale);

        mLinePaint = new Paint();
        mLinePaint.setColor(getResources().getColor(R.color.color_white));

        mFillPaint = new Paint();
        mFillPaint.setColor(getResources().getColor(R.color.scale_dayoff));
    }

    public void setRange(DateTime minDate, DateTime maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public void setXAxis(int nx, int dx) {
        this.nx = nx;
        this.dx = dx;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.color_title_date));
        int height = canvas.getHeight();

        for (int i = 0; i < nx; i++) {
            int dayOfMonth = minDate.getDayOfMonth();
            int dayOfWeek = minDate.getDayOfWeek();

            // 月を描画
            if (dayOfMonth == 1) {
                canvas.drawText(String.valueOf(minDate.getMonthOfYear()) + "月", dx * i + 3 * scale, height / 2 - 3 * scale, mCharPaint);
                canvas.drawLine(dx * i, 0, dx * i, height / 2, mLinePaint);
            }

            // 土日の場合は背景に色をつける
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                canvas.drawRect(dx * i, height / 2, dx * (i + 1), height, mFillPaint);
            }

            // 日を描画
            canvas.drawText(String.valueOf(dayOfMonth), dx * i + 10, height - 10, mCharPaint);

            // 区切り線
            canvas.drawLine(dx * i, height / 2, dx * i, height, mLinePaint);
            canvas.drawLine(dx * i, height / 2, dx * (i + 1) - 1, height / 2, mLinePaint);

            minDate = minDate.plusDays(1);
        }
    }
}
