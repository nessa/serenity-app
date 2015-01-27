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

        /*
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectItem(position);
                    }
                });
        mDrawerListView.setDivider(null);
        mNavigationDrawerAdapter = mNavigationDrawerAdapter == null ?
                new NavigationDrawerAdapter(getActivity()) : mNavigationDrawerAdapter;
        mDrawerListView.setAdapter(mNavigationDrawerAdapter);
        if (!mFromSavedInstanceState)
            selectItem(mCurrentSelectedPosition);
        return mDrawerListView;
*/



        mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_recipe_list, container,
                false);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;

        GridView gridview = (GridView) mLayout.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getActivity(), screen_width));


        // Calling transition from ImageAdapter
        /*
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.v("INFO", "Inside item click");
                Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
                String transitionName = getString(R.string.transition_recipe_detail);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this,
                    v, // The view which starts the transition
                    transitionName // The transitionName of the view we’re transitioning to
                );
                MainActivity.this.startActivity(intent, options.toBundle());
            }
        });
        */

        /*
        FloatingActionButton addButton = new FloatingActionButton.Builder()//this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_add_white_48dp))
                .withButtonColor(getResources().getColor(R.color.accent))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16);
        //        .create();

        mLayout.addView(addButton);
        // TODO: If user is logged in, addButton must be visible
        if (false) {
            addButton.setVisibility(View.GONE);
        }

        // TODO: addButton on click must go to create activity
        */
        return mLayout;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
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

    public void search() {
        Log.v("INFO", "Search clicked");
    }

    public void makeFavorite(View v) {
        Log.v("INFO", "Favorite " + v.getTag());
    }

}