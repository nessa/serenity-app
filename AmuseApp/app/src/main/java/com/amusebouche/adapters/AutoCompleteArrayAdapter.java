package com.amusebouche.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.amusebouche.activities.R;
import com.amusebouche.services.DatabaseHelper;

import java.util.ArrayList;

/**
 * Autocomplete array adapter class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com>
 *
 * It declares the view of each list view cells showed on autocomplete text views.
 *
 * Related layouts:
 * - Item: autocomplete_list_item
 */
public class AutoCompleteArrayAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;
    private DatabaseHelper mDatabaseHelper;
    private LayoutInflater mInflater;
    private int mResourceId;

    public AutoCompleteArrayAdapter(Context context) {
        super(context, R.layout.autocomplete_list_item);
        this.mDatabaseHelper = new DatabaseHelper(context);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResourceId = R.layout.autocomplete_list_item;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    /**
     * Create a new view for each item referenced by the adapter. If the view already existed, it
     * will update it.
     * @param position Position of this view in the list.
     * @param convertView Existing view (it may not exist, so it will be null).
     * @param parent Parent view
     * @return New view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(this.mResourceId, parent, false);
        }

        TextView textViewItem = (TextView) convertView.findViewById(R.id.text);
        textViewItem.setText(getItem(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    // TODO: Get language before
                    resultList = mDatabaseHelper.getIngredientsTranslations(10, "es",
                            constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
    }
}