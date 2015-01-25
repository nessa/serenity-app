package com.amusebouche.amuseapp;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.amusebouche.ui.FloatingActionButton;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("INFO", "On create");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, screen_width));


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
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v("INFO", "On resume");

        FloatingActionButton addButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_add_white_48dp))
                .withButtonColor(getResources().getColor(R.color.accent))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        // TODO: If user is logged in, addButton must be visible
        if (false) {
            addButton.setVisibility(View.GONE);
        }

        // TODO: addButton on click must go to create activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

