package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Locale;

/**
 * Recipe detail fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains and shows the recipe detailed information.
 *
 * Related layouts:
 * - Menu: menu_recipe_detail.xml
 * - Content: fragment_recipe_detail.xml
 */
public class RecipeDetailFragment extends Fragment {
    private LinearLayout mLayout;
    private TextToSpeech mTTS;
    private EditText mEditText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        mTTS = new TextToSpeech(this.getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    Locale locSpanish = new Locale("spa", "ESP");
                    mTTS.setLanguage(locSpanish);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
        changeActionButton();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTTS != null){
            mTTS.shutdown();
        }
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

        mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_recipe_detail,
                container, false);

        mEditText = (EditText) mLayout.findViewById(R.id.edit_text);
        Button speechButton = (Button) mLayout.findViewById(R.id.speech_button);


        // Calling transition from image (to detail image)
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = mEditText.getText();

                if (text != "") {
                    mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                }


            }
        });

        return mLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(getClass().getSimpleName(), "onHidden()");
        if (!hidden) {
            Log.i(getClass().getSimpleName(), "Not hidden");
            changeActionButton();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_fav:
                makeFavorite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void makeFavorite() {
        Log.v("INFO", "Favorite X");
        // TODO: Implement this method.
    }

    /* Instead of using the action bar method setNavigationMode, we define specifically the
     * buttons to show. We call this method when the app is created the first time (onResume) and
     * every time it appears again (onHiddenChange). */
    private void changeActionButton() {
        MainActivity x = (MainActivity) getActivity();
        x.setDrawerIndicatorEnabled(false);
    }
}
