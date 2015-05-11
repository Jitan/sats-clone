package se.greatbrain.sats.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.TextView;

import se.greatbrain.sats.R;

public abstract class CalendarRowView extends TextView
{
    protected boolean shouldDrawTextView = true;

    public static final int PAST_ACTIVITY = 1;
    public static final int FUTURE_OR_CURRENT_ACTIVITY = 2;

    private static final String TAG = "CalendarRowView";
    private static final int TO_NEXT_WEEK = 1;
    protected static final int TO_PREVIOUS_WEEK = -1;

    protected final int numActivities;
    protected final boolean shouldDrawCircle;
    protected final boolean isPastActivity;
    protected final int numPreviousWeekActivities;
    protected final int numNextWeekActivities;
    private final boolean shouldDrawLineToPreviousWeek;
    protected final boolean shouldDrawLineToNextWeek;
    protected final Drawable horizontalLine;
    protected final Drawable circle;
    protected final Drawable hollowCircle;

    protected int drawBoundsWidth;
    protected int drawBoundsHeight;
    private int width;
    private int height;
    protected int centerX;
    protected int centerY;

    protected int lineThicknessToNextWeek;
    protected int lineThicknessToPreviousWeek;

    protected CalendarRowView(Context context, int numActivities, boolean shouldDrawCircle,
            boolean isPastActivity, boolean drawLineToPreviousWeek,
            int numPreviousWeekActivities, boolean drawLineToNextWeek, int numNextWeekActivities)
    {
        super(context);
        this.numActivities = numActivities;
        this.shouldDrawCircle = shouldDrawCircle;
        this.isPastActivity = isPastActivity;
        this.shouldDrawLineToPreviousWeek = drawLineToPreviousWeek;
        this.shouldDrawLineToNextWeek = drawLineToNextWeek;
        this.numPreviousWeekActivities = numPreviousWeekActivities;
        this.numNextWeekActivities = numNextWeekActivities;

        horizontalLine = getResources().getDrawable(R.drawable.line);
        circle = getResources().getDrawable(R.drawable.calendar_circle);
        hollowCircle = getResources().getDrawable(R.drawable.calendar_hollow_circle);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        setIncludeFontPadding(false);

        super.onLayout(changed, left, top, right, bottom);
        drawBoundsWidth = getWidth();
        drawBoundsHeight = convertToDrawBoundsHeight(getHeight());
        width = getWidth();
        height = getHeight();
        centerX = drawBoundsWidth / 2;
        centerY = drawBoundsHeight / 2;
        lineThicknessToPreviousWeek = getLineThicknessToPreviousWeek();
        lineThicknessToNextWeek = getLineThicknessToNextWeek();

        horizontalLine.setBounds(0, 0, drawBoundsWidth, drawBoundsHeight);

        int circleDiameter = getResources().getDimensionPixelSize(R.dimen.calendar_circle_diameter);

        int circleLeftOffsetPx = centerX - (circleDiameter / 2);
        int circleTopOffsetPx = centerY - (circleDiameter / 2);

        circle.setBounds(circleLeftOffsetPx, circleTopOffsetPx, circleLeftOffsetPx + circleDiameter,
                circleTopOffsetPx + circleDiameter);
        hollowCircle.setBounds(circleLeftOffsetPx, circleTopOffsetPx,
                circleLeftOffsetPx + circleDiameter,
                circleTopOffsetPx + circleDiameter);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas)
    {
        horizontalLine.draw(canvas);

        if (shouldDrawCircle)
        {
            canvas.save();
            //draw outside bounds
            Rect clipBounds = canvas.getClipBounds();
            clipBounds.inset(-200, -200);
            canvas.clipRect(clipBounds, Region.Op.REPLACE);
            if (isPastActivity)
            {
                circle.draw(canvas);
                setTextColor(getResources().getColor(R.color.white));

                drawOrangeLine(TO_PREVIOUS_WEEK, canvas);
                if (shouldDrawLineToNextWeek)
                {
                    drawOrangeLineToNextWeek(canvas);
                }
            }
            else
            {
                hollowCircle.draw(canvas);
                setTextColor(getResources().getColor(R.color.black));
            }
            canvas.restore();

            if(shouldDrawTextView)
            {
                super.onDraw(canvas);
            }
        }
    }

    protected abstract int convertToDrawBoundsHeight(int height);

    protected abstract void drawOrangeLine(int direction, Canvas canvas);

    protected abstract void drawOrangeLineToNextWeek(Canvas canvas);

    protected abstract int getLineThicknessToPreviousWeek();

    protected abstract int getLineThicknessToNextWeek();

    public static class Builder
    {
        private Context context;
        private int numActivities = 0;
        private boolean drawCircle = false;
        private boolean isPastActivity;
        private boolean drawLineToPreviousWeek = false;
        private boolean drawLineToNextWeek = false;
        private boolean isZeroRow = false;
        private int numNextWeekActivities;
        private int numPreviousWeekActivities;

        private Builder() {}

        public Builder(Context context, int numActivities)
        {
            this.context = context;
            this.numActivities = numActivities;
        }

        public Builder drawCircle(int activityType)
        {
            if (activityType == PAST_ACTIVITY)
            {
                this.isPastActivity = true;
            }
            else if (activityType == FUTURE_OR_CURRENT_ACTIVITY)
            {
                this.isPastActivity = false;
            }
            else
            {
                throw new IllegalArgumentException("Invalid activity type");
            }
            this.drawCircle = true;
            return this;
        }

        public Builder setIsZeroRow()
        {
            isZeroRow = true;
            return this;
        }

        public Builder drawLineToPreviousWeek(int numPreviousWeekActivities)
        {
            this.drawLineToPreviousWeek = true;
            this.numPreviousWeekActivities = numPreviousWeekActivities;
            return this;
        }

        public Builder drawLineToNextWeek(int numNextWeekActivities)
        {
            this.drawLineToNextWeek = true;
            this.numNextWeekActivities = numNextWeekActivities;
            return this;
        }


        public CalendarRowView build()
        {
            if (isZeroRow)
            {
                return new CalendarHalfRowView(context, numActivities, drawCircle, isPastActivity,
                        drawLineToPreviousWeek, numPreviousWeekActivities,
                        drawLineToNextWeek, numNextWeekActivities);
            }
            return new CalendarFullRowView(context, numActivities, drawCircle, isPastActivity,
                    drawLineToPreviousWeek, numPreviousWeekActivities,
                    drawLineToNextWeek, numNextWeekActivities);
        }

    }
}