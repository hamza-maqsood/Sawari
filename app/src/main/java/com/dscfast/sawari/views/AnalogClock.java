package com.dscfast.sawari.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

public class AnalogClock extends View {

    //width & height of clock
    int WIDTH, HEIGHT;
    int secHAND, minHAND, hrHAND; // lengths of the hands

    //center
    int cx, cy;

    private Handler h = new Handler();
    private Runnable r;

    Paint paint = new Paint();

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context);
    }

    public AnalogClock(Context context) {
        super(context);
        //timer
        r = new Runnable() {
            @Override
            public void run() {
                invalidate(); //invalidate the graphics every 1 second
                Timber.d("Invalidated clock!");
                h.removeCallbacks(r);
                if (getRootView().isShown()) {
                    h.postDelayed(this, 1000);
                }
            }
        };
        h.postDelayed(r, 1000); // 1 second delay (takes millis)
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        h.removeCallbacks(r);
        stopRunnable();
        Timber.d("Detaching from window");
    }

    public void stopRunnable() {
        h.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        //get screen size
        int x = getWidth();
        int y = getHeight();

        //get the center
        cx = x / 2;
        cy = y / 2;

        //set the hands length
        secHAND = (x / 2) - (x / 25);
        minHAND = (x / 2) - (x / 12);
        hrHAND = (x / 2) - (x / 5);

        //set the clock width, height (same width as height)
        WIDTH = getWidth();
        HEIGHT = WIDTH;

        //set the clock radius
        int radius = (WIDTH / 2); // - 2

        //anti aliasing
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        //draw background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        //draw clock circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(WIDTH / 120); //4
        paint.setColor(Color.BLACK);
//        canvas.drawCircle(cx, cy, 4, paint);


        //every hour makes 30 degrees
        //every minute makes 6 degrees
        int degrees = 0;
        int[] coord_hour = new int[2];
        int[] coord_hour_tick = new int[2];
        int hour_tick_size = WIDTH / 70;
        int minute_tick_size = (WIDTH / 70) / 2;
        int hour_length = (WIDTH / 2) - (WIDTH / 9); //+ WIDTH / 60
        int hour_tick_length = ((WIDTH / 2) - (WIDTH / 9)) + WIDTH / 12;
        int hour = 12;

        for (int i = 1; i <= 60; i++)
        {
            if (degrees >= 0 && degrees <= 180)
            {
                if (degrees % 30 == 0)
                {
                    coord_hour[0] = cx + (int)(hour_length * Math.sin(Math.PI * degrees / 180));
                    coord_hour[1] = cy - (int)(hour_length * Math.cos(Math.PI * degrees / 180));
                }

                coord_hour_tick[0] = cx + (int)(hour_tick_length * Math.sin(Math.PI * degrees / 180));
                coord_hour_tick[1] = cy - (int)(hour_tick_length * Math.cos(Math.PI * degrees / 180));

            }
            else
            {
                if (degrees % 30 == 0)
                {
                    coord_hour[0] = cx - (int)(hour_length * -Math.sin(Math.PI * degrees / 180));
                    coord_hour[1] = cy - (int)(hour_length * Math.cos(Math.PI * degrees / 180));
                }

                coord_hour_tick[0] = cx - (int)(hour_tick_length * -Math.sin(Math.PI * degrees / 180));
                coord_hour_tick[1] = cy - (int)(hour_tick_length * Math.cos(Math.PI * degrees / 180));

            }

            //draw the point on clock face
            if (degrees % 30 == 0)
            {
                //set the font for hours
                TextPaint textPaint = new TextPaint();
                textPaint.setTextSize((WIDTH / 10) - 8);
                textPaint.setColor(Color.BLACK);
                textPaint.setTypeface(Typeface.create("Comic Sans MS", Typeface.BOLD));

                //measure the string width & height
                Rect bounds = new Rect();
                textPaint.getTextBounds(String.valueOf(hour),0,String.valueOf(hour).length(),bounds);

                //draw the hours
                canvas.drawText(String.valueOf(hour), coord_hour[0] - bounds.exactCenterX(), coord_hour[1] - bounds.exactCenterY(), textPaint);

                //draw hour tick
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLACK);
                canvas.drawCircle(coord_hour_tick[0], coord_hour_tick[1], hour_tick_size, paint);

                if (hour == 12)
                    hour = 1;
                else
                    hour++;
            }
            else
            {
                //draw the ticks for minutes
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLACK);
                canvas.drawCircle(coord_hour_tick[0] ,coord_hour_tick[1], minute_tick_size, paint);

            }

            degrees = degrees + 6;

        }


        //draw center of the clock face
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(cx, cy, WIDTH / 27, paint);

        //draw box for the date
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(WIDTH / 120); //4
        int rect_width = WIDTH / 12;
        canvas.drawRect((cx - (rect_width / 2)) + (WIDTH / 4),cy - (rect_width / 2), (cx - (rect_width / 2)) + (WIDTH / 4) + rect_width, (cy - (rect_width / 2)) + rect_width, paint);



        //get time
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int ss = calendar.get(Calendar.SECOND);
        int mm = calendar.get(Calendar.MINUTE);
        int hh = calendar.get(Calendar.HOUR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int[] handCoord = new int[2];

        //draw hands in sequence: hour, minute, second
        //hour hand
        handCoord = hrCoord(hh % 12, mm, hrHAND);
        paint.setStrokeWidth(WIDTH / 48);//10
        canvas.drawLine(cx, cy, handCoord[0], handCoord[1], paint);

        //minute hand
        handCoord = msCoord(mm, minHAND);
        paint.setStrokeWidth(WIDTH / 80); // 6
        canvas.drawLine(cx, cy, handCoord[0], handCoord[1], paint);


        //second hand
        handCoord = msCoord(ss, secHAND);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(WIDTH /120); //4
        canvas.drawLine(cx, cy, handCoord[0], handCoord[1], paint);

        //cover center hands
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawCircle(cx, cy, WIDTH / 50, paint);

        //display date day
        TextPaint textPaint = new TextPaint();
        int size = ((WIDTH / 10) - 8) / 4;
        textPaint.setTextSize(((WIDTH / 10) - 8) - size);
        textPaint.setColor(Color.RED);
        textPaint.setTypeface(Typeface.create("Comic Sans MS", Typeface.BOLD));

        //measure the string width & height
        Rect bounds = new Rect();
        textPaint.getTextBounds(String.valueOf(day), 0, String.valueOf(day).length(), bounds);


        canvas.drawText(String.valueOf(day),(cx + (WIDTH / 4))- bounds.exactCenterX() , cy - bounds.exactCenterY(), textPaint);

    }

    //coord for minute and second hand
    private int[] msCoord(int val, int hlen) {
        int[] coord = new int[2];
        val *= 6;   //each minute and second make 6 degree

        if (val >= 0 && val <= 180)
        {
            coord[0] = cx + (int)(hlen * Math.sin(Math.PI * val / 180));
            coord[1] = cy - (int)(hlen * Math.cos(Math.PI * val / 180));
        }
        else
        {
            coord[0] = cx - (int)(hlen * -Math.sin(Math.PI * val / 180));
            coord[1] = cy - (int)(hlen * Math.cos(Math.PI * val / 180));
        }
        return coord;
    }

    //coord for hour hand
    private int[] hrCoord(int hval, int mval, int hlen) {
        int[] coord = new int[2];

        //each hour makes 30 degree
        //each min makes 0.5 degree
        int val = (int)((hval * 30) + (mval * 0.5));

        if (val >= 0 && val <= 180)
        {
            coord[0] = cx + (int)(hlen * Math.sin(Math.PI * val / 180));
            coord[1] = cy - (int)(hlen * Math.cos(Math.PI * val / 180));
        }
        else
        {
            coord[0] = cx - (int)(hlen * -Math.sin(Math.PI * val / 180));
            coord[1] = cy - (int)(hlen * Math.cos(Math.PI * val / 180));
        }
        return coord;
    }

    @Override
    public void finalize() {
        h.removeCallbacks(r);
        h = null;
        Timber.d("Removed Callbacks");
    }
}