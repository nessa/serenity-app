package com.amusebouche.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amusebouche.activities.DetailActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.Comment;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.CustomDateFormat;
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

    // Parent activity
    private DetailActivity mDetailActivity;

    // Data variables
    private ArrayList<Comment> mComments;

    private RecyclerAdapterWithHeader mAdapter;
    private EditText mCommentTextView;

    // LIFECYCLE METHODS

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

        mDetailActivity = (DetailActivity) getActivity();
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

        RecyclerView mLayout = (RecyclerView) inflater.inflate(R.layout.fragment_detail_fourth_tab,
            container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mLayout.setLayoutManager(linearLayoutManager);

        mAdapter = new RecyclerAdapterWithHeader();
        mLayout.setAdapter(mAdapter);

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

        getComments();
    }

    private void getComments() {
        if (mDetailActivity.getRecipe().getId() != null &&
            !mDetailActivity.getRecipe().getId().equals("0")) {

            mDetailActivity.showLoadingSnackbar("Loading...");

            // Make request
            AmuseAPI mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
            Call<ResponseBody> call = mAPI.getComments(mDetailActivity.getRecipe().getId());

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

                            // Show comments
                            mAdapter.notifyDataSetChanged();
                            mDetailActivity.hideLoadingSnackbar();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mDetailActivity.hideLoadingSnackbar();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mDetailActivity.hideLoadingSnackbar();
                }

            });
        }
    }

    private void sendComment(String comment) {
        AmuseAPI mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class,
            mDetailActivity.getToken());
        Call<ResponseBody> call = mAPI.addComment(mDetailActivity.getRecipe().getId(), comment);

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
        });
    }

    /**
     * Adapter for a recycler view with header that shows the list of comments
     */
    public class RecyclerAdapterWithHeader extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        public RecyclerAdapterWithHeader() {}

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_detail_comment, parent, false);
                return new VHItem(v);
            } else if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_detail_fourth_tab_header, parent, false);
                return new VHHeader(v);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof VHItem) {
                VHItem itemHolder = (VHItem) holder;

                final Comment presentComment = getItem(position);

                itemHolder.userTextView.setText(presentComment.getUser());
                itemHolder.timestampTextView.setText(CustomDateFormat.getDateTimeString(
                    presentComment.getTimestamp()));
                itemHolder.commentTextView.setText(presentComment.getComment());
            } else if (holder instanceof VHHeader) {
                final VHHeader headerHolder = (VHHeader) holder;

                headerHolder.commentTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        headerHolder.sendCommentButton.setEnabled(!s.toString().equals("") &&
                            mDetailActivity.getRecipe().getId() != null &&
                            !mDetailActivity.getRecipe().getId().equals("0"));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                headerHolder.sendCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!headerHolder.commentTextView.getText().toString().equals("")) {
                            sendComment(headerHolder.commentTextView.getText().toString());
                        }
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return mComments.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)) {
                return TYPE_HEADER;
            }

            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        private Comment getItem(int position) {
            return mComments.get(position - 1);
        }

        class VHItem extends RecyclerView.ViewHolder {
            private TextView userTextView;
            private TextView timestampTextView;
            private TextView commentTextView;

            public VHItem(View itemView) {
                super(itemView);

                userTextView = (TextView) itemView.findViewById(R.id.user);
                timestampTextView = (TextView) itemView.findViewById(R.id.timestamp);
                commentTextView = (TextView) itemView.findViewById(R.id.comment);
            }
        }

        class VHHeader extends RecyclerView.ViewHolder {
            private EditText commentTextView;
            public Button sendCommentButton;

            public VHHeader(View itemView) {
                super(itemView);

                commentTextView = (EditText) itemView.findViewById(R.id.comment_text);
                sendCommentButton = (Button) itemView.findViewById(R.id.send_comment_button);

                mCommentTextView = commentTextView;
            }
        }
    }
}
