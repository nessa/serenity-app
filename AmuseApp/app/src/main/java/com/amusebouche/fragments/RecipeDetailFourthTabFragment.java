package com.amusebouche.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amusebouche.activities.DetailActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.Comment;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.RetrofitServiceGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Recipe detail fourth tab fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of detail activity.
 * It's used inside a list of tabs.
 * It contains and shows the recipe's comments.
 *
 * Related layouts:
 * - Content: fragment_detail_fourth_tab.xml
 */
public class RecipeDetailFourthTabFragment extends Fragment {

    private DetailActivity mActivity;

    // Data variables
    private ArrayList<Comment> mComments;

    // UI variables
    private ScrollView mScroll;
    private LinearLayout mCommentsView;
    private EditText mCommentTextView;
    private ProgressBar mLoadingIndicator;

    // LIFECYCLE METHODS

    /**
     * Called when a fragment is first attached to its activity.
     *
     * @param activity Fragment activity (DetailActivity)
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
     * Called to do initial creation of a fragment. This is called after onAttach and before
     * onCreateView.
     *
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        mActivity = (DetailActivity) getActivity();
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

        // Get layouts
        RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_detail_fourth_tab,
                container, false);

        mScroll = (ScrollView) mLayout.findViewById(R.id.scroll);
        mLoadingIndicator = (ProgressBar) mLayout.findViewById(R.id.loading_indicator);
        mCommentsView = (LinearLayout) mLayout.findViewById(R.id.comments);

        mCommentTextView = (EditText) mLayout.findViewById(R.id.comment_text);
        final Button sendCommentButton = (Button) mLayout.findViewById(R.id.send_comment_button);

        mCommentTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendCommentButton.setEnabled(!s.toString().equals("") &&
                        mActivity.getRecipe().getId() != null &&
                        !mActivity.getRecipe().getId().equals("0"));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCommentTextView.getText().toString().equals("")) {
                    sendComment(mCommentTextView.getText().toString());
                }
            }
        });

        // Get comments if needed
        reloadComments();

        return mLayout;
    }

    public void reloadComments() {
        if (mComments == null) {
            mComments = new ArrayList<>();
        } else {
            mComments.clear();
        }
        mCommentsView.removeAllViews();
        getComments();
    }

    private void getComments() {
        if (mActivity.getRecipe().getId() != null && !mActivity.getRecipe().getId().equals("0")) {

            mScroll.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);

            AmuseAPI mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
            Call<ResponseBody> call = mAPI.getComments(mActivity.getRecipe().getId());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String jsonStr = "";

                    if (response.body() != null) {
                        try {
                            jsonStr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jObject = new JSONObject(jsonStr);
                            JSONArray results = jObject.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                Comment c = new Comment(results.getJSONObject(i));
                                mComments.add(c);
                            }

                            showComments();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }

            });
        }
    }

    private void showComments() {
        if (mComments != null) {
            for (int c = 0; c < mComments.size(); c++) {
                LinearLayout commentLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(
                    R.layout.item_detail_comment, mScroll, false);

                TextView userTextView = (TextView) commentLayout.findViewById(R.id.user);
                TextView timestampTextView = (TextView) commentLayout.findViewById(R.id.timestamp);
                TextView commentTextView = (TextView) commentLayout.findViewById(R.id.comment);

                userTextView.setText(mComments.get(c).getUser());
                timestampTextView.setText(mComments.get(c).getTimestamp().toString());
                commentTextView.setText(mComments.get(c).getComment());

                mCommentsView.addView(commentLayout);
            }
        }

        mLoadingIndicator.setVisibility(View.GONE);
        mScroll.setVisibility(View.VISIBLE);
    }

    private void sendComment(String comment) {
        AmuseAPI mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class, mActivity.getToken());
        Call<ResponseBody> call = mAPI.addComment(mActivity.getRecipe().getId(), comment);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 201) {
                    // Reset comment text and reload comments
                    mCommentTextView.setText("");
                    reloadComments();
                } else {
                    // TODO: Show error
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO: Show error
            }
        });}

}
