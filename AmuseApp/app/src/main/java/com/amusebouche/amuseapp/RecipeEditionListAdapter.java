package com.amusebouche.amuseapp;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.data.UserFriendlyRecipeData;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

/**
 * Created by noelia on 17/03/16.
 */
public class RecipeEditionListAdapter extends DragItemAdapter<Pair<Long, RecipeIngredient>,
        RecipeEditionListAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private Context mContext;

    public RecipeEditionListAdapter(ArrayList<Pair<Long, RecipeIngredient>> list, int layoutId,
                                    int grabHandleId, boolean dragOnLongPress, Context c) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mContext = c;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        RecipeIngredient i = mItemList.get(position).second;

        holder.mNameTextView.setText(i.getName());
        holder.mQuantityTextView.setText(UserFriendlyRecipeData.getIngredientQuantity(i.getQuantity(),
                i.getMeasurementUnit(), mContext));
        holder.itemView.setTag(i.getName());
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, RecipeIngredient>,
            RecipeEditionListAdapter.ViewHolder>.ViewHolder {

        public TextView mNameTextView;
        public TextView mQuantityTextView;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mNameTextView = (TextView) itemView.findViewById(R.id.name);
            mQuantityTextView = (TextView) itemView.findViewById(R.id.quantity);
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}