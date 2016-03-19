package com.amusebouche.amuseapp;


import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amusebouche.data.RecipeDirection;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

/**
 * Ingredient list view cell adapter class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * It declares the view of each list view cells that contains:
 * - Remove icon.
 * - Ingredient name.
 * - Ingredient quantity.
 * - Drag & drop movement.
 *
 * Related layouts:
 * - Content: item_edition_ingredient.xml
 */
public class RecipeEditionDirectionListAdapter extends DragItemAdapter<Pair<Long, RecipeDirection>,
        RecipeEditionDirectionListAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private Context mContext;
    private RecipeEditionThirdTabFragment mFragment;

    public RecipeEditionDirectionListAdapter(ArrayList<Pair<Long, RecipeDirection>> list, int layoutId,
                                             int grabHandleId, boolean dragOnLongPress, Context c,
                                             RecipeEditionThirdTabFragment fragment) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mFragment = fragment;
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

        RecipeDirection d = mItemList.get(position).second;

        holder.mNameTextView.setText(String.format("%s %d", mContext.getString(R.string.detail_direction_label),
                d.getSortNumber()));
        holder.mDescriptionTextView.setText(d.getDescription());
        holder.itemView.setTag(d.getSortNumber() - 1);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, RecipeDirection>,
            RecipeEditionDirectionListAdapter.ViewHolder>.ViewHolder {

        public ImageView mDeleteImage;
        public LinearLayout mDirectionData;
        public TextView mNameTextView;
        public TextView mDescriptionTextView;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mDeleteImage = (ImageView) itemView.findViewById(R.id.delete);
            mDirectionData = (LinearLayout) itemView.findViewById(R.id.direction_data);
            mNameTextView = (TextView) itemView.findViewById(R.id.name);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.description);

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.removeDirection((int) itemView.getTag());
                }
            });

            mDirectionData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.showEditionDialog((int) itemView.getTag());
                }
            });
        }
    }
}