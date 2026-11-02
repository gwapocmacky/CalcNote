package com.example.calcnote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class LinedEditText extends EditText {
    private final List<Double> lineResults = new ArrayList<Double>();
    private final Paint resultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ResultListener listener;

    public interface ResultListener { void onTotalsChanged(double grand); }

    public LinedEditText(Context context) { super(context); init(); }
    public LinedEditText(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public LinedEditText(Context context, AttributeSet attrs, int style) { super(context, attrs, style); init(); }

    private void init() {
        resultPaint.setTextSize(getTextSize());
        resultPaint.setTextAlign(Paint.Align.RIGHT);
        resultPaint.setTypeface(android.graphics.Typeface.MONOSPACE);
        addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int a) { recompute(); }
            public void afterTextChanged(Editable s) {}
        });
    }

    public void setResultListener(ResultListener l) { this.listener = l; }

    public void recompute() {
        lineResults.clear();
        String text = getText() == null ? "" : getText().toString();
        String[] lines = text.split("\\n", -1);
        for (String ln : lines) {
            double v = ExprEvaluator.lineTotal(ln);
            lineResults.add(ExprEvaluator.hasNumber(ln) ? v : null);
        }
        invalidate();
        if (listener != null) listener.onTotalsChanged(ExprEvaluator.grandTotal(text));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int lineCount = getLineCount();
        int color = UiUtils.getColor(getContext(), R.color.foreground_soft);
        // subtle but readable
        resultPaint.setColor(color);
        resultPaint.setAlpha(170);
        resultPaint.setTextSize(getTextSize());
        for (int i = 0; i < lineCount && i < lineResults.size(); i++) {
            Double r = lineResults.get(i);
            if (r == null) continue;
            float x = getWidth() - getPaddingRight();
            android.graphics.Rect rect = new android.graphics.Rect();
            int baseline = getLineBounds(i, rect);
            String txt = format(r);
            // measure and draw subtle background pill
            float tw = resultPaint.measureText(txt);
            float pad = 8f;
            android.graphics.RectF bgRect = new android.graphics.RectF(x - tw - pad, baseline - getTextSize() + 4, x, baseline + 8);
            android.graphics.Paint bgPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(UiUtils.getColor(getContext(), R.color.accent_soft));
            bgPaint.setAlpha(180);
            canvas.drawRoundRect(bgRect, 10, 10, bgPaint);
            canvas.drawText(txt, x - pad, baseline, resultPaint);
        }
    }

    private String format(double v) {
        if (v == (long) v) return String.valueOf((long) v);
        java.text.DecimalFormat f = new java.text.DecimalFormat("#,##0.##");
        return f.format(v);
    }
}
