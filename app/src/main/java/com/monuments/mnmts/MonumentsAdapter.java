package com.monuments.mnmts;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MonumentsAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView textView;
        public final TextView secondTextView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.list_item_monuments_textview);
            secondTextView = (TextView) view.findViewById(R.id.list_item_monuments_secondview);
        }
    }

    public MonumentsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_monuments, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String itemName = cursor.getString(MonumentsFragment.COL_MONUMENTS_NAME);
        String itemInfo = cursor.getString(MonumentsFragment.COL_MONUMENTS_PAATOSPAIVA);

        viewHolder.textView.setText(itemName);
        viewHolder.secondTextView.setText("Päätöspäivä: " + itemInfo);

    }
}