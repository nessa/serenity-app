package com.amusebouche.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amusebouche.activities.R;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.services.UserFriendlyTranslationsHandler;
import com.amusebouche.fragments.RecipeEditionSecondTabFragment;
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
public class RecipeEditionIngredientListAdapter extends DragItemAdapter<Pair<Long, RecipeIngredient>,
        RecipeEditionIngredientListAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private Context mContext;
    private RecipeEditionSecondTabFragment mFragment;

    public RecipeEditionIngredientListAdapter(ArrayList<Pair<Long, RecipeIngredient>> list,
                                              int layoutId, int grabHandleId, boolean dragOnLongPress,
                                              Context c, RecipeEditionSecondTabFragment fragment) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mContext = c;
        mFragment = fragment;
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

        // Set cell data
        holder.mNameTextView.setText(i.getName());
        holder.mQuantityTextView.setText(UserFriendlyTranslationsHandler.getIngredientQuantity(i.getQuantity(),
                i.getMeasurementUnit(), mContext));
        holder.itemView.setTag(i.getSortNumber() - 1);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, RecipeIngredient>,
            RecipeEditionIngredientListAdapter.ViewHolder>.ViewHolder {

        public ImageView mDeleteImage;
        public LinearLayout mIngredientData;
        public TextView mNameTextView;
        public TextView mQuantityTextView;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mDeleteImage = (ImageView) itemView.findViewById(R.id.delete);
            mIngredientData = (LinearLayout) itemView.findViewById(R.id.ingredient_data);
            mNameTextView = (TextView) itemView.findViewById(R.id.name);
            mQuantityTextView = (TextView) itemView.findViewById(R.id.quantity);

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.removeIngredient((int) itemView.getTag());
                }
            });

            mIngredientData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.showEditionDialog((int) itemView.getTag());
                }
            });
        }
    }
}