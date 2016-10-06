package com.amusebouche.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileOutputStream;

import com.amusebouche.activities.DetailActivity;
import com.amusebouche.activities.MainActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.Recipe;
import com.amusebouche.fragments.RecipeListFragment;
import com.amusebouche.services.AppData;
import com.amusebouche.services.ImageHandler;

/**
 * Grid view cell adapter class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * It declares the view of each grid view cells that contains:
 * - Recipe image.
 * - Recipe name.
 *
 * Related layouts:
 * - Content: cell_grid_view.xml
 */
public class GridViewCellAdapter extends RecyclerView.Adapter<GridViewCellAdapter.ViewHolder> {
    private Context mContext;
    private RecipeListFragment mFragment;
    private int mScreenWidth;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView name;
        public ImageView image;
        public RelativeLayout fadeViews;

        public ViewHolder(View v) {
            super(v);
            view = v;
            name = (TextView) v.findViewById(R.id.recipe_name);
            image = (ImageView) v.findViewById(R.id.recipe_image);
            fadeViews = (RelativeLayout) v.findViewById(R.id.fade_views);
        }
    }

    public GridViewCellAdapter(Context c, RecipeListFragment f, int screenWidth) {
        mContext = c;
        mFragment = f;
        mScreenWidth = screenWidth;
    }


    public Recipe getItem(int position) {
        return ((MainActivity) mContext).getRecipes().get(position);
    }

    public long getItemId(int position) {
        try {
            if (((MainActivity) mContext).getRecipes().get(position).getIsOnline()) {
                // API identifier
                return Long.valueOf(((MainActivity) mContext).getRecipes().get(position).getId());
            } else {
                // Database identifier
                return Long.valueOf(((MainActivity) mContext).getRecipes().get(position).getDatabaseId());
            }
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if (((MainActivity) mContext).getRecipes() != null) {
            return ((MainActivity) mContext).getRecipes().size();
        } else {
            return 0;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GridViewCellAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_grid_view, parent, false);
        v.setLayoutParams(new GridView.LayoutParams(mScreenWidth /
            mContext.getResources().getInteger(R.integer.gridview_columns) -
            mContext.getResources().getInteger(R.integer.gridview_margin),
            mScreenWidth / mContext.getResources().getInteger(R.integer.gridview_columns) -
                mContext.getResources().getInteger(R.integer.gridview_margin)));

        return new ViewHolder(v);
    }


    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder View holder
     * @param position Position of this view in the gridview.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Keep downloading next recipes when we show the last ones
        if (position > ((MainActivity) mContext).getPreviousTotal() && position == getItemCount() -
            mContext.getResources().getInteger(R.integer.gridview_left_items_to_reload)) {
            ((MainActivity) mContext).setPreviousTotal(position);
            mFragment.downloadNextRecipes();
        }

        // Get the item in the adapter
        final Recipe presentRecipe = getItem(position);

        // Set position as tag in the view
        holder.view.setTag(position);

        // Get the text view to update the string
        holder.name.setText(presentRecipe.getTitle());

        // Get the image to update the content
        ImageHandler.setCellImage(mContext, presentRecipe.getImage(), holder.image, position);

        // Call transition from image (to detail image)
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View clickedView) {

                // Can't compress a recycled bitmap so we copy it
                holder.image.buildDrawingCache(true);
                Bitmap bitmap = holder.image.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
                holder.image.destroyDrawingCache();

                try {
                    // Save bitmap into file to prevent transactiontoolargeexception
                    FileOutputStream stream = mContext.openFileOutput(
                        AppData.RECIPE_TRANSITION_IMAGE, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    // Cleanup
                    stream.close();
                    bitmap.recycle();


                    // Material transition
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        // Fade out title and view
                        Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
                        fadeOutAnimation.setDuration(250);

                        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // Send selected recipe to the next activity
                                Intent i = new Intent(mContext, DetailActivity.class);
                                i.putExtra(AppData.INTENT_KEY_RECIPE,
                                    ((MainActivity) mContext).getRecipes().get((int) clickedView.getTag()));
                                i.putExtra(AppData.INTENT_KEY_RECIPE_POSITION, (int) clickedView.getTag());

                                Pair<View, String> p1 = Pair.create((View) holder.image, mContext.getString(R.string.transition_detail_image));
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    (MainActivity) mContext, p1);

                                ActivityCompat.startActivityForResult((MainActivity) mContext, i,
                                    AppData.REQUEST_FROM_DETAIL_TO_LIST_CODE, options.toBundle());
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });

                        holder.fadeViews.startAnimation(fadeOutAnimation);
                    } else{
                        // Pre-lollipop transition
                        Intent i = new Intent(mContext, DetailActivity.class);
                        i.putExtra(AppData.INTENT_KEY_RECIPE,
                            ((MainActivity) mContext).getRecipes().get((int) clickedView.getTag()));
                        i.putExtra(AppData.INTENT_KEY_RECIPE_POSITION, (int) clickedView.getTag());

                        mContext.startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}