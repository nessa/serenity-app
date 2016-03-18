package com.amusebouche.amuseapp;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.data.UserFriendlyRecipeData;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;


import java.util.ArrayList;

/**
 * Recipe edtion fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of add activity and edit activity.
 * It contains lots of inputs to edit recipe's data.
 *
 * Related layouts:
 * - Content: fragment_edition_first_tab.xmlxml
 */
public class RecipeEditionSecondTabFragment extends Fragment {


    private Recipe mRecipe;
    private LinearLayout mLayout;

    private ArrayList<String> typesOfDish;
    private ArrayList<String> difficulties;

    private EditText mTitle;
    private Spinner mTypeOfDish;
    private Spinner mDifficulty;
    private TextView mCookingTimeLabel;
    private SeekBar mCookingTimeHours;
    private SeekBar mCookingTimeMinutes;
    private TextView mServingsLabel;
    private SeekBar mServings;
    private DragListView mIngredientsListView;

    // LIFECYCLE METHODS


    /**
     * Called to do initial creation of a fragment. This is called after onAttach and before
     * onCreateView.
     *
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        typesOfDish = new ArrayList<String>();
        typesOfDish.add(getString(R.string.type_of_dish_appetizer));
        typesOfDish.add(getString(R.string.type_of_dish_first_course));
        typesOfDish.add(getString(R.string.type_of_dish_second_course));
        typesOfDish.add(getString(R.string.type_of_dish_main_dish));
        typesOfDish.add(getString(R.string.type_of_dish_dessert));
        typesOfDish.add(getString(R.string.type_of_dish_other));

        difficulties = new ArrayList<String>();
        difficulties.add(getString(R.string.difficulty_low));
        difficulties.add(getString(R.string.difficulty_medium));
        difficulties.add(getString(R.string.difficulty_high));


    }


    /**
     * Called when a fragment is first attached to its activity.
     *
     * @param activity Fragemnt activity (DetailActivity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    /**
     * Called when the fragment's activity has been created and this fragment's view
     * hierarchy instantiated.
     *
     * @param savedInstanceState State of the fragment if it's being re-created.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
    }


    /**
     * Called when the Fragment is no longer started.
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop()");
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be
     * reconstructed in a new instance of its process is restarted.
     *
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the fragment is no longer attached to its activity. Called after onDestroy.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }


    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * onCreate and onActivityCreated, onViewStateRestored, onStart().
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment,
     * @param container          This is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If this fragment is being re-constructed from a previous saved
     *                           state as given here.
     * @return Return the View for the this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");


        mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_edition_second_tab,
                container, false);

        if (getActivity() instanceof AddActivity) {
            // do something
            AddActivity x = (AddActivity) getActivity();

            // Create an empty recipe
            mRecipe = new Recipe(
                    "",
                    "",
                    "",
                    "", // Set username
                    "es", // Set preferences language
                    UserFriendlyRecipeData.getDefaultTypeOfDish(),
                    UserFriendlyRecipeData.getDefaultDifficulty(),
                    null,
                    null,
                    null,
                    "",
                    0,
                    0,
                    0,
                    "",
                    new ArrayList<RecipeCategory>(),
                    new ArrayList<RecipeIngredient>(),
                    new ArrayList<RecipeDirection>());
        } else {
            //do something else
            Log.d("INFO", "ELSE");
        }


        // Test
        ArrayList<RecipeIngredient> ing = new ArrayList<RecipeIngredient>();
        ing.add(new RecipeIngredient(1, "garbanzos", Float.valueOf("50"), "g"));
        ing.add(new RecipeIngredient(2, "chorizo", Float.valueOf("1"), "unit"));
        ing.add(new RecipeIngredient(3, "aceite de oliva", Float.valueOf("20"), "ml"));

        ArrayList<Pair<Long, RecipeIngredient>> mItemArray = new ArrayList<>();
        for (int i = 0; i < ing.size(); i++) {
            RecipeIngredient ri = ing.get(i);
            mItemArray.add(new Pair<>(Long.valueOf(ri.getSortNumber()), ri));
        }



        mIngredientsListView = (DragListView) mLayout.findViewById(R.id.ingredients);
        mIngredientsListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                }
            }
        });
        mIngredientsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecipeEditionListAdapter listAdapter = new RecipeEditionListAdapter(mItemArray,
                R.layout.item_edition_ingredient, R.id.image, false, getActivity());
        mIngredientsListView.setAdapter(listAdapter, true);
        mIngredientsListView.setCanDragHorizontally(false);
        mIngredientsListView.setCustomDragItem(new MyDragItem(getActivity(),
                R.layout.item_edition_ingredient));


        return mLayout;
    }

    private static class MyDragItem extends DragItem {

        private Context mContext;

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
            mContext = context;
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            ((TextView) dragView.findViewById(R.id.name)).setText(((TextView) clickedView.findViewById(R.id.name)).getText());
            ((TextView) dragView.findViewById(R.id.quantity)).setText(((TextView) clickedView.findViewById(R.id.quantity)).getText());

            ((TextView) dragView.findViewById(R.id.name)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            ((TextView) dragView.findViewById(R.id.quantity)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.theme_default_primary));
        }
    }
}
