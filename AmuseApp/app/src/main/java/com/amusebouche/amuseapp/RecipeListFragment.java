package com.amusebouche.amuseapp;

import android.graphics.Point;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.amusebouche.ui.FloatingActionButton;

/**
 * Recipe list fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains a dynamic gridview with recipes (filtered or not).
 *
 * Related layouts:
 * - Menu: menu_recipe_list.xml
 * - Content: fragment_recipe_list.xml
 */
public class RecipeListFragment extends Fragment {
    private RelativeLayout mLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");

        mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_recipe_list,
                container, false);

        // Set gridview parameters
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        int screen_width = screenSize.x;

        GridView gridview = (GridView) mLayout.findViewById(R.id.gridview);
        gridview.setAdapter(new GridviewCellAdapter(getActivity(), screen_width));

        // Add FAB programatically
        float scale = getResources().getDisplayMetrics().density;
        int addButtonSize = FloatingActionButton.convertToPixels(72, scale);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(addButtonSize,
            addButtonSize);

        FloatingActionButton addButton = new FloatingActionButton(this.getActivity());
        addButton.setFloatingActionButtonColor(getResources().getColor(R.color.accent));
        addButton.setFloatingActionButtonDrawable(getResources()
            .getDrawable(R.drawable.ic_add_white_48dp));

        params.bottomMargin = FloatingActionButton.convertToPixels(20, scale);
        params.rightMargin = FloatingActionButton.convertToPixels(20, scale);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        addButton.setLayoutParams(params);
        mLayout.addView(addButton);

        // TODO: If user is logged in, addButton must be visible
        if (false) {
            addButton.setVisibility(View.GONE);
        }

        // TODO: addButton on click must go to create activity

        return mLayout;
    }

    @Override
    public void onResume() {
        Log.i(getClass().getSimpleName(), "onResume()");
        super.onResume();
        changeActionButton();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(getClass().getSimpleName(), "onHiddenChanged()");
        if (!hidden) {
            Log.i(getClass().getSimpleName(), "not hidden");
            changeActionButton();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                search();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Search will call to another (lateral) fragment.
     * TODO: Implement this method.
     */
    public void search() {
        Log.v("INFO", "Search clicked");
    }


    public void makeFavorite(View v) {
        Log.v("INFO", "Favorite " + v.getTag());
    }

    /* Instead of using the action bar method setNavigationMode, we define specifically the
     * buttons to show. We call this method when the app is created the first time (onResume)
     * and every time it appears again (onHiddenChange). */
    private void changeActionButton() {
        MainActivity x = (MainActivity) getActivity();
        x.setDrawerIndicatorEnabled(true);
    }
}