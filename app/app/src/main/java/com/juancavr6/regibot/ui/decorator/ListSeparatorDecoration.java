package com.juancavr6.regibot.ui.decorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.juancavr6.regibot.R;

public class ListSeparatorDecoration extends RecyclerView.ItemDecoration {
    private final Paint textPaint;
    private final Paint linePaint;
    private final int height;

    public ListSeparatorDecoration(Context context) {
        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.gray)); //Text color
        textPaint.setTextSize(50); // Size of the text
        textPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(context, R.color.gray)); // Line color
        linePaint.setStrokeWidth(2); // Thickness of the line

        height = 60; // Height of the separator
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child) + 1;

            float top = child.getTop() - height;
            if (top < 0) continue;

            float textX = child.getLeft();
            float textY = top + 40; // Centrado vertical del texto
            canvas.drawText(position + "°", textX, textY, textPaint);

            float lineStartX = textX + 80; // Separación del texto
            float lineY = textY - 15; // Alineado con el texto
            canvas.drawLine(lineStartX, lineY, child.getRight()-30, lineY, linePaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = height; // Añade espacio arriba del ítem
    }
}

