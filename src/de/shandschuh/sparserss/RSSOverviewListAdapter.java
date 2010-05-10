/**
 * Sparse rss
 * 
 * Copyright (c) 2010 Stefan Handschuh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package de.shandschuh.sparserss;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import de.shandschuh.sparserss.provider.FeedData;

public class RSSOverviewListAdapter extends ResourceCursorAdapter {
	private static final String COUNT = "COUNT(*) - COUNT(readdate)";
	
	private static final String COLON = ": ";
	
	private static final String COMMA = ", ";
	
	private int nameColumnPosition;
	
	private int lastUpdateColumn;
	
	private int idPosition;
	
	private int linkPosition;
	
	private int errorPosition;
	
	public RSSOverviewListAdapter(Activity context) {
		super(context, android.R.layout.simple_list_item_2, context.managedQuery(FeedData.FeedColumns.CONTENT_URI, null, null, null, null));
		nameColumnPosition = getCursor().getColumnIndex(FeedData.FeedColumns.NAME);
		lastUpdateColumn = getCursor().getColumnIndex(FeedData.FeedColumns.LASTUPDATE);
		idPosition = getCursor().getColumnIndex(FeedData.FeedColumns._ID);
		linkPosition = getCursor().getColumnIndex(FeedData.FeedColumns.URL);
		errorPosition = getCursor().getColumnIndex(FeedData.FeedColumns.ERROR);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView textView = ((TextView) view.findViewById(android.R.id.text1));
		
		textView.setText(cursor.isNull(nameColumnPosition) ? cursor.getString(linkPosition) : cursor.getString(nameColumnPosition));
		
		
		Cursor countCursor = context.getContentResolver().query(FeedData.EntryColumns.CONTENT_URI(cursor.getString(idPosition)), new String[] {COUNT}, null, null, null);
		
		countCursor.moveToFirst();
		
		int unreadCount = countCursor.getInt(0);
		
		countCursor.close();
		
		long date = cursor.getLong(lastUpdateColumn);
		
		if (cursor.isNull(errorPosition)) {
			((TextView) view.findViewById(android.R.id.text2)).setText(new StringBuilder(context.getString(R.string.update)).append(COLON).append(date == 0 ? context.getString(R.string.never) : new StringBuilder(DateFormat.getDateTimeInstance().format(new Date(date))).append(COMMA).append(unreadCount).append(' ').append(context.getString(R.string.unread))));
		} else {
			((TextView) view.findViewById(android.R.id.text2)).setText(new StringBuilder(context.getString(R.string.error)).append(COLON).append(cursor.getString(errorPosition)));
		}
		
		textView.setTypeface(unreadCount > 0 ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
	}

}
