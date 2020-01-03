package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.UserContract;


/**
 * Created by Thal Marc on 4/11/2017.
 */

public class UserCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link UserCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public UserCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //new empty view
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView sumary = (TextView) view.findViewById(R.id.summary);

        int nameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_NAME);
        int sumaryIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_AMOUNT);

        String currentName = cursor.getString(nameIndex);
        String currentSumary = cursor.getString(sumaryIndex);

        name.setText(currentName);
        sumary.setText(currentSumary);

    }
}
