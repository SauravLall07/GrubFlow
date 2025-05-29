package com.yourpackage.name;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusSpinnerAdapter extends ArrayAdapter<String> {

    private final List<String> items;
    private final Map<String, Integer> colors;

    public StatusSpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.items = items;

        // Default colors for different statuses
        this.colors = new HashMap<>();
        this.colors.put("Pending", Color.parseColor("#008080"));
        this.colors.put("Preparing", Color.parseColor("#008080"));
        this.colors.put("Ready", Color.parseColor("#008080"));
        this.colors.put("Delivered", Color.parseColor("#008080"));
        this.colors.put("Cancelled", Color.parseColor("#008080"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        String item = items.get(position);

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            Integer color = colors.get(item);
            if (color != null) {
                textView.setTextColor(color);
            }
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        String item = items.get(position);

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            Integer color = colors.get(item);
            if (color != null) {
                textView.setTextColor(color);
                GradientDrawable drawable = createColorDrawable(color, 8, textView.getHeight());
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                textView.setCompoundDrawablePadding(16);
            }
        }

        return view;
    }

    private GradientDrawable createColorDrawable(int color, int width, int height) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setSize(width, height);
        return drawable;
    }
}
