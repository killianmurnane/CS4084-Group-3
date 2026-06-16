package com.example.cs4084_group_3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class ExerciseProgressChart extends View {

    //data
    private List<String> labels = new ArrayList<>();
    private List<Float> values  = new ArrayList<>();
    private String yAxisLabel   = "";

    // paint objects
    private final Paint gridPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pbPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);

    // layout constants (dp → px resolved in init)
    private float paddingLeft, paddingRight, paddingTop, paddingBottom;
    private float dotRadius, dotStrokeWidth, lineStrokeWidth;
    private float labelTextSize, valueTextSize;

    public ExerciseProgressChart(Context context) {
        super(context);
        init(context);
    }

    public ExerciseProgressChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExerciseProgressChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float d = context.getResources().getDisplayMetrics().density;

        paddingLeft   = 56 * d;
        paddingRight  = 20 * d;
        paddingTop    = 24 * d;
        paddingBottom = 48 * d;
        dotRadius       = 5  * d;
        dotStrokeWidth  = 2  * d;
        lineStrokeWidth = 2.5f * d;
        labelTextSize   = 10 * d;
        valueTextSize   = 9  * d;

        // Resolve theme primary colour with safe fallback
        int primary;
        try {
            int[] attrs = new int[] { androidx.appcompat.R.attr.colorPrimary };
            android.content.res.TypedArray ta = context.obtainStyledAttributes(attrs);
            primary = ta.getColor(0, Color.parseColor("#6750A4"));
            ta.recycle();
        } catch (Exception e) {
            primary = Color.parseColor("#6750A4");
        }

        gridPaint.setColor(Color.parseColor("#1AFFFFFF"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1 * d);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{4 * d, 4 * d}, 0));

        axisPaint.setColor(Color.parseColor("#33FFFFFF"));
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(1 * d);

        linePaint.setColor(primary);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineStrokeWidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        int fillColor = (primary & 0x00FFFFFF) | 0x22000000;
        fillPaint.setColor(fillColor);
        fillPaint.setStyle(Paint.Style.FILL);

        dotPaint.setColor(primary);
        dotPaint.setStyle(Paint.Style.FILL);

        labelPaint.setColor(Color.parseColor("#99FFFFFF"));
        labelPaint.setTextSize(labelTextSize);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint.setColor(Color.parseColor("#BBFFFFFF"));
        valuePaint.setTextSize(valueTextSize);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        emptyPaint.setColor(Color.parseColor("#66FFFFFF"));
        emptyPaint.setTextSize(labelTextSize * 1.2f);
        emptyPaint.setTextAlign(Paint.Align.CENTER);

        pbPaint.setColor(Color.parseColor("#FFFFD54F")); // amber accent for PB dot
        pbPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Supply chart data.
     *
     * @param labels     X-axis date labels (one per data point)
     * @param values     Y-axis values     (one per data point)
     * @param yAxisLabel Shown at top-left as a units hint, e.g. "kg"
     */
    public void setData(List<String> labels, List<Float> values, String yAxisLabel) {
        this.labels     = labels != null ? labels : new ArrayList<>();
        this.values     = values != null ? values : new ArrayList<>();
        this.yAxisLabel = yAxisLabel != null ? yAxisLabel : "";
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        float chartLeft   = paddingLeft;
        float chartRight  = w - paddingRight;
        float chartTop    = paddingTop;
        float chartBottom = h - paddingBottom;
        float chartW      = chartRight - chartLeft;
        float chartH      = chartBottom - chartTop;

        // ── empty state ───────────────────────────────────────────────────────
        if (values.isEmpty()) {
            canvas.drawText("No data yet — complete a workout to see progress",
                    w / 2f, h / 2f, emptyPaint);
            return;
        }

        // ── compute range ──────────────────────────────────────────────────────
        float maxVal = values.get(0);
        float minVal = values.get(0);
        int   pbIdx  = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) > maxVal) { maxVal = values.get(i); pbIdx = i; }
            if (values.get(i) < minVal)   minVal = values.get(i);
        }

        // Add a 10 % padding above so the top dot isn't clipped
        float range = maxVal - minVal;
        if (range < 1f) range = 1f;
        float yMin = Math.max(0, minVal - range * 0.15f);
        float yMax = maxVal + range * 0.15f;

        // ── grid lines (4 horizontal) ──────────────────────────────────────────
        int gridLines = 4;
        for (int i = 0; i <= gridLines; i++) {
            float y = chartBottom - (chartH / gridLines) * i;
            canvas.drawLine(chartLeft, y, chartRight, y, gridPaint);

            // Y-axis tick labels
            float tickVal = yMin + (yMax - yMin) * i / gridLines;
            String tick = tickVal >= 100 ? String.format("%.0f", tickVal)
                    : String.format("%.1f", tickVal);
            Paint tp = new Paint(valuePaint);
            tp.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(tick, chartLeft - 6, y + valueTextSize / 2.5f, tp);
        }

        // ── Y axis label ───────────────────────────────────────────────────────
        if (!yAxisLabel.isEmpty()) {
            Paint yLabelPaint = new Paint(valuePaint);
            yLabelPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(yAxisLabel, 4, chartTop - 6, yLabelPaint);
        }

        // ── axes ───────────────────────────────────────────────────────────────
        canvas.drawLine(chartLeft, chartTop,    chartLeft,  chartBottom, axisPaint);
        canvas.drawLine(chartLeft, chartBottom, chartRight, chartBottom, axisPaint);

        int n = values.size();
        if (n == 1) {
            // Single point — draw as a centred dot with label
            float cx = chartLeft + chartW / 2f;
            float cy = chartBottom - chartH * 0.5f;
            canvas.drawCircle(cx, cy, dotRadius * 1.4f, dotPaint);
            canvas.drawText(labels.get(0), cx, chartBottom + labelTextSize + 4, labelPaint);
            return;
        }

        // ── compute x/y positions ──────────────────────────────────────────────
        float[] px = new float[n];
        float[] py = new float[n];
        for (int i = 0; i < n; i++) {
            px[i] = chartLeft + chartW * i / (n - 1f);
            py[i] = chartBottom - chartH * (values.get(i) - yMin) / (yMax - yMin);
        }

        // ── filled area under the line ─────────────────────────────────────────
        Path fillPath = new Path();
        fillPath.moveTo(px[0], chartBottom);
        fillPath.lineTo(px[0], py[0]);
        for (int i = 1; i < n; i++) fillPath.lineTo(px[i], py[i]);
        fillPath.lineTo(px[n - 1], chartBottom);
        fillPath.close();
        canvas.drawPath(fillPath, fillPaint);

        // ── line ──────────────────────────────────────────────────────────────
        Path linePath = new Path();
        linePath.moveTo(px[0], py[0]);
        for (int i = 1; i < n; i++) linePath.lineTo(px[i], py[i]);
        canvas.drawPath(linePath, linePaint);

        // ── dots + x-axis labels ──────────────────────────────────────────────
        // Decide how many x labels to show to avoid overlap
        int labelStep = Math.max(1, (int) Math.ceil(n / 6.0));

        for (int i = 0; i < n; i++) {
            // Dot — gold for PB, primary otherwise
            Paint dp = (i == pbIdx) ? pbPaint : dotPaint;
            canvas.drawCircle(px[i], py[i], dotRadius, dp);

            // Value above dot (only for PB or first/last)
            if (i == pbIdx || i == 0 || i == n - 1) {
                String vLabel = values.get(i) >= 100
                        ? String.format("%.0f", values.get(i))
                        : String.format("%.1f", values.get(i));
                canvas.drawText(vLabel, px[i], py[i] - dotRadius - 4, valuePaint);
            }

            // X-axis label (rotated for readability via translate)
            if (i % labelStep == 0 || i == n - 1) {
                canvas.save();
                canvas.translate(px[i], chartBottom + labelTextSize + 6);
                canvas.rotate(-30);
                canvas.drawText(labels.get(i), 0, 0, labelPaint);
                canvas.restore();
            }
        }
    }
}