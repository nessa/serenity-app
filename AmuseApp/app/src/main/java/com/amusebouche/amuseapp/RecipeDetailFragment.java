package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.ui.CustomNumberPicker;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * Recipe detail fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 * <p/>
 * Android fragment class, part of main activity.
 * It contains and shows the recipe detailed information.
 * <p/>
 * Related layouts:
 * - Content: fragment_recipe_detail.xml
 */
public class RecipeDetailFragment extends Fragment
        implements ObservableScrollViewCallbacks {

    // Data variables
    private Recipe mRecipe;
    private Bitmap mMainImage;
    private Integer mPresentDescriptionIndex;

    // Behaviour variables
    private boolean mOngoingMode;

    // Services variables
    private TextToSpeech mTTS;

    // UI variables
    private FrameLayout mLayout;
    private ObservableScrollView mScrollView;
    private View mOverlayView;
    private TextView mRecipeName;
    private TextView mRecipeOwner;
    private ImageView mRecipeImage;
    private FloatingActionButton mFab;

    // Calc UI size variables
    private Integer mFlexibleSpaceImageHeight;
    private Integer mFlexibleSpaceShowFabOffset;
    private Integer mFabMargin;
    private Integer mActionBarSize;

    // Timer dialog variables
    private Dialog mTimerDialog;
    private CountDownTimer mCountDownTimer;
    private Integer mTimerHours;
    private Integer mTimerMinutes;
    private Integer mTimerSeconds;

    // Commands dialog variables
    private Dialog mCommandsDialog;
    private SpeechRecognizer mSpeechRecognizer;
    private CountDownTimer mSpeechRecognizerTimer;
    private boolean mSpeechRecognizerTimerIsRunning;


    // LIFECYCLE METHODS

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
     * Called to do initial creation of a fragment. This is called after onAttach and before
     * onCreateView.
     *
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        // Get image bitmap from file
        mMainImage = null;
        String FILENAME = "presentRecipeImage.png";
        try {
            FileInputStream is = getActivity().openFileInput(FILENAME);
            mMainImage = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");

        mPresentDescriptionIndex = 0;

        mTTS = new TextToSpeech(this.getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locSpanish = new Locale("spa", "ESP");
                    mTTS.setLanguage(locSpanish);
                }
            }
        });
    }


    /**
     * Called when the Fragment is no longer started.
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop()");

        if (mTTS != null) {
            mTTS.shutdown();
        }

        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (mSpeechRecognizerTimer != null) {
            mSpeechRecognizerTimer.cancel();
        }
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

        /* TODO: Try to prevent Skipped XX frames! The application may be doing too
         * much work on its main thread. */

        // Get recipe from activity
        DetailActivity x = (DetailActivity) getActivity();
        mRecipe = x.getRecipe();

        mLayout = (FrameLayout) inflater.inflate(R.layout.fragment_recipe_detail,
                container, false);

        mScrollView = (ObservableScrollView) mLayout.findViewById(R.id.scroll);

        mRecipeImage = (ImageView) mLayout.findViewById(R.id.recipe_image);
        mRecipeImage.setImageBitmap(mMainImage);

        mRecipeName = (TextView) mLayout.findViewById(R.id.recipe_name);
        mRecipeName.setText(mRecipe.getTitle());

        mRecipeOwner = (TextView) mLayout.findViewById(R.id.recipe_owner);
        mRecipeOwner.setText(mRecipe.getOwner());


        mLayout.post(new Runnable() {
            @Override
            public void run() {
                // This code will position texts and FAB correctly
                // It must be launched only when the layout was finally drawed
                RecipeDetailFragment.this.onScrollChanged(
                        RecipeDetailFragment.this.mScrollView.getCurrentScrollY(), true, false);
            }
        });


        // Set view sizes
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(
                R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(
                R.dimen.flexible_space_show_fab_offset);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        mActionBarSize = getResources().getDimensionPixelSize(
                R.dimen.abc_action_bar_default_height_material);


        // Get UI elements
        mOverlayView = mLayout.findViewById(R.id.overlay);
        mScrollView.setScrollViewCallbacks(this);
        mFab = (FloatingActionButton) mLayout.findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RecipeDetailFragment.this.mRecipe.getDirections().size() > 0) {
                    mOngoingMode = true;
                    mPresentDescriptionIndex = 0;
                    RecipeDetailFragment.this.readDescription();
                } else {
                    Log.d("INFO", "No directions");
                }
            }
        });

        // Overlay view transparent
        ViewHelper.setAlpha(mOverlayView, 0);


        // Set data
        TextView typeOfDishTextView = (TextView) mLayout.findViewById(R.id.type_of_dish);
        typeOfDishTextView.setText(this.getTypeOfDish(mRecipe.getTypeOfDish()));

        TextView difficultyTextView = (TextView) mLayout.findViewById(R.id.difficulty);
        difficultyTextView.setText(this.getDifficulty(mRecipe.getDifficulty()));

        TextView cookingTimeTextView = (TextView) mLayout.findViewById(R.id.cooking_time);
        cookingTimeTextView.setText(this.getCookingTime(mRecipe.getCookingTime()));

        TextView servingsTextView = (TextView) mLayout.findViewById(R.id.servings);
        servingsTextView.setText(Objects.toString(mRecipe.getServings()));

        TextView sourceTextView = (TextView) mLayout.findViewById(R.id.source);
        sourceTextView.setText(mRecipe.getSource());

        if (mRecipe.getSource().startsWith("http://") || mRecipe.getSource().startsWith("https://")) {
            sourceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchWebIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mRecipe.getSource()));
                    startActivity(launchWebIntent);
                }
            });
        }

        float rating = 0;
        if (mRecipe.getUsersRating() != 0) {
            rating = (float) mRecipe.getTotalRating() / (float) mRecipe.getUsersRating();
        }

        TextView ratingTextView = (TextView) mLayout.findViewById(R.id.rating);
        ratingTextView.setText(Objects.toString(rating));

        // Ingredients
        LinearLayout ingredientsLayout = (LinearLayout) mLayout.findViewById(R.id.ingredients);

        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            RecipeIngredient presentIngredient = (RecipeIngredient) mRecipe.getIngredients().get(i);

            LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(
                    R.layout.fragment_recipe_detail_ingredient, mLayout, false);

            TextView quantity = (TextView) ingredientLayout.findViewById(R.id.quantity);
            quantity.setText(this.getIngredientQuantity(presentIngredient.getQuantity(),
                    presentIngredient.getMeasurementUnit()));

            TextView name = (TextView) ingredientLayout.findViewById(R.id.name);
            name.setText(presentIngredient.getName());

            ingredientsLayout.addView(ingredientLayout);
        }

        // Directions
        LinearLayout directionsLayout = (LinearLayout) mLayout.findViewById(R.id.directions);

        for (int d = 0; d < mRecipe.getDirections().size(); d++) {
            RecipeDirection presentDirection = (RecipeDirection) mRecipe.getDirections().get(d);

            LinearLayout directionLayout = (LinearLayout) inflater.inflate(
                    R.layout.fragment_recipe_detail_direction, mLayout, false);
            directionLayout.setTag("direction" + d);

            TextView number = (TextView) directionLayout.findViewById(R.id.number);
            number.setText(String.format("%s %d", getString(R.string.detail_direction_label),
                    presentDirection.getSortNumber()));

            TextView description = (TextView) directionLayout.findViewById(R.id.description);
            description.setText(presentDirection.getDescription());

            LinearLayout extraLayout = (LinearLayout) directionLayout.findViewById(R.id.extra);

            ImageButton readDirectionButton = (ImageButton) extraLayout.findViewById(
                    R.id.readDescription);
            ImageButton showDirectionImageButton = (ImageButton) extraLayout.findViewById(
                    R.id.showPhoto);
            ImageButton showDirectionVideoButton = (ImageButton) extraLayout.findViewById(
                    R.id.showVideo);
            ImageButton directionTimerButton = (ImageButton) extraLayout.findViewById(
                    R.id.timer);


            readDirectionButton.setTag(d);
            showDirectionImageButton.setTag(d);
            showDirectionVideoButton.setTag(d);
            directionTimerButton.setTag(d);

            readDirectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOngoingMode = false;

                    RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(
                            (int) v.getTag());
                    CharSequence text = dir.getDescription();

                    if (text != "") {
                        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });

            if (presentDirection.getImage().equals("")) {
                showDirectionImageButton.setVisibility(View.GONE);
            } else {
                showDirectionImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(
                                (int) v.getTag());

                        // Send selected recipe to the next activity
                        Intent i = new Intent(getActivity(), MediaActivity.class);
                        i.putExtra("mediaType", "IMAGE");
                        i.putExtra("elementUri", dir.getImage());
                        i.putExtra("directionNumber", Objects.toString(dir.getSortNumber()));

                        ActivityCompat.startActivity(getActivity(), i, null);
                    }
                });
            }

            if (presentDirection.getVideo().equals("")) {
                showDirectionVideoButton.setVisibility(View.GONE);
            } else {
                showDirectionVideoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("INFO", "CLICK VIDEO BUTTON");
                    }
                });
            }

            if (presentDirection.getTime() == 0) {
                directionTimerButton.setVisibility(View.GONE);
            } else {
                directionTimerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(
                                (int) v.getTag());

                        // Calc time variables
                        Integer time = (int) dir.getTime().floatValue();

                        mTimerHours = time / 3600;
                        mTimerMinutes = (time / 60) % 60;
                        mTimerSeconds = time % 60;

                        // Set dialog attributes
                        final Dialog selectTimeDialog = new Dialog(getActivity());
                        selectTimeDialog.getWindow().setWindowAnimations(R.style.LateralDialogAnimation);
                        selectTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        selectTimeDialog.setContentView(R.layout.dialog_detail_timer_set_time);

                        final TextView hoursTextView = (TextView) selectTimeDialog.findViewById(R.id.hours);
                        final TextView minutesTextView = (TextView) selectTimeDialog.findViewById(R.id.minutes);
                        final TextView secondsTextView = (TextView) selectTimeDialog.findViewById(R.id.seconds);

                        hoursTextView.setText(String.format("%d", mTimerHours));
                        minutesTextView.setText(String.format("%d", mTimerMinutes));
                        secondsTextView.setText(String.format("%d", mTimerSeconds));

                        // Number pickers for hours, minutes and seconds
                        final CustomNumberPicker hoursPicker = (CustomNumberPicker)
                                selectTimeDialog.findViewById(R.id.hoursPicker);
                        hoursPicker.setMaxValue(10);
                        hoursPicker.setMinValue(0);
                        hoursPicker.setWrapSelectorWheel(false);
                        hoursPicker.setValue(mTimerHours);

                        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                RecipeDetailFragment.this.mTimerHours = newVal;
                                hoursTextView.setText(String.format("%d", mTimerHours));
                            }
                        });

                        final CustomNumberPicker minutesPicker = (CustomNumberPicker)
                                selectTimeDialog.findViewById(R.id.minutesPicker);
                        minutesPicker.setMaxValue(59);
                        minutesPicker.setMinValue(0);
                        minutesPicker.setWrapSelectorWheel(true);
                        minutesPicker.setValue(mTimerMinutes);

                        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                RecipeDetailFragment.this.mTimerMinutes = newVal;
                                minutesTextView.setText(String.format("%d", mTimerMinutes));
                            }
                        });

                        final CustomNumberPicker secondsPicker = (CustomNumberPicker)
                                selectTimeDialog.findViewById(R.id.secondsPicker);
                        secondsPicker.setMaxValue(59);
                        secondsPicker.setMinValue(0);
                        secondsPicker.setWrapSelectorWheel(false);
                        secondsPicker.setValue(mTimerSeconds);

                        secondsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                RecipeDetailFragment.this.mTimerSeconds = newVal;
                                secondsTextView.setText(String.format("%d", mTimerSeconds));
                            }
                        });

                        // Buttons
                        Button cancelButton = (Button) selectTimeDialog.findViewById(R.id.buttonCancel);
                        Button setButton = (Button) selectTimeDialog.findViewById(R.id.buttonSet);

                        setButton.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecipeDetailFragment.this.mTimerHours = hoursPicker.getValue();
                                RecipeDetailFragment.this.mTimerMinutes = minutesPicker.getValue();
                                RecipeDetailFragment.this.mTimerSeconds = secondsPicker.getValue();

                                RecipeDetailFragment.this.setTimerDialog(
                                        RecipeDetailFragment.this.mTimerHours * 3600 +
                                                RecipeDetailFragment.this.mTimerMinutes * 60 +
                                                RecipeDetailFragment.this.mTimerSeconds);

                                selectTimeDialog.dismiss();
                                RecipeDetailFragment.this.mTimerDialog.show();
                                RecipeDetailFragment.this.mCountDownTimer.start();
                            }
                        });

                        cancelButton.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectTimeDialog.dismiss();
                            }
                        });

                        selectTimeDialog.show();
                    }
                });
            }

            directionsLayout.addView(directionLayout);
        }

        return mLayout;
    }


    // DATA USER-FRIENDLY

    /**
     * Translate typeOfDish code to an understandable string
     *
     * @param code API type of dish code
     * @return User-friendly string
     */
    private String getTypeOfDish(String code) {
        switch (code) {
            case "APPETIZER":
                return getString(R.string.type_of_dish_appetizer);
            case "FIRST-COURSE":
                return getString(R.string.type_of_dish_first_course);
            case "SECOND-COURSE":
                return getString(R.string.type_of_dish_second_course);
            case "MAIN-DISH":
                return getString(R.string.type_of_dish_main_dish);
            case "DESSERT":
                return getString(R.string.type_of_dish_dessert);
            default:
            case "OTHER":
                return getString(R.string.type_of_dish_other);
        }
    }

    /**
     * Translate difficulty code to an understandable string
     *
     * @param code API difficulty code
     * @return User-friendly string
     */
    private String getDifficulty(String code) {
        switch (code) {
            case "HIGH":
                return getString(R.string.difficulty_high);
            case "LOW":
                return getString(R.string.difficulty_low);
            default:
            case "MEDIUM":
                return getString(R.string.difficulty_medium);
        }
    }

    /**
     * Translate cookingTime code to an understandable string
     *
     * @param time Float time
     * @return User-friendly string
     */
    private String getCookingTime(float time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);

        String completeTime = seconds + getString(R.string.detail_seconds);
        if (minutes > 0) {
            completeTime = minutes + getString(R.string.detail_minutes) + " " + completeTime;
        }

        return completeTime;
    }

    /**
     * Translate ingredien's quantity code to an understandable string
     *
     * @param quantity  Float quantity
     * @param unit_code Code of measurement unit
     * @return User-friendly string
     */
    private String getIngredientQuantity(float quantity, String unit_code) {
        String q = "", u = "";
        boolean plural = true;

        if (quantity > 0) {
            float result = quantity - (int) quantity;
            if (result != 0) {
                q = String.format("%.2f", quantity) + " ";
            } else {
                q = String.format("%.0f", quantity) + " ";
            }

            if (quantity <= 1) {
                plural = false;
            }

            if (quantity == 0.25) {
                q = "1/4 ";
            }
            if (quantity == 0.5) {
                q = "1/2 ";
            }
            if (quantity == 0.75) {
                q = "3/4 ";
            }
        }

        if (!unit_code.equals("unit")) {
            switch (unit_code) {
                case "g":
                    if (plural) {
                        u = getString(R.string.measurement_unit_g_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_g) + " ";
                    }
                    break;
                case "kg":
                    if (plural) {
                        u = getString(R.string.measurement_unit_kg_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_kg) + " ";
                    }
                    break;
                case "ml":
                    if (plural) {
                        u = getString(R.string.measurement_unit_ml_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_ml) + " ";
                    }
                    break;
                case "l":
                    if (plural) {
                        u = getString(R.string.measurement_unit_l_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_l) + " ";
                    }
                    break;
                case "cup":
                    if (plural) {
                        u = getString(R.string.measurement_unit_cup_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_cup) + " ";
                    }
                    break;
                case "tsp":
                    if (plural) {
                        u = getString(R.string.measurement_unit_tsp_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_tsp) + " ";
                    }
                    break;
                case "tbsp":
                    if (plural) {
                        u = getString(R.string.measurement_unit_tbsp_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_tbsp) + " ";
                    }
                    break;
                case "rasher":
                    if (plural) {
                        u = getString(R.string.measurement_unit_rasher_plural) + " ";
                    } else {
                        u = getString(R.string.measurement_unit_rasher) + " ";
                    }
                    break;
                default:
                case "unit":
                    break;
            }
        }

        if (!u.equals("")) {
            u = u + getString(R.string.measurement_unit_of) + " ";
        }

        return q + u;
    }


    // TEXT TO SPEECH AND DIALOGS

    /**
     * Exit from 'ongoing mode'
     */
    private void exitFromOngoingMode() {
        mOngoingMode = false;

        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (mSpeechRecognizerTimer != null) {
            mSpeechRecognizerTimer.cancel();
        }
    }


    /**
     * Make TextToSpeech read the direction given by mPresentDescriptionIndex
     */
    public void readDescription() {
        if (mRecipe.getDirections().size() > mPresentDescriptionIndex) {
            final RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(
                    mPresentDescriptionIndex);
            CharSequence text = dir.getDescription();

            // Move view to direction box
            final LinearLayout directionsLayout = (LinearLayout) mLayout.findViewById(R.id.directions);
            final View directionLayout = mLayout.findViewWithTag("direction" +
                    mPresentDescriptionIndex);

            mScrollView.smoothScrollTo(0, directionsLayout.getTop() + directionLayout.getTop() +
                    (int) getResources().getDimension(R.dimen.detail_direction_box_offset));

            if (text != "") {

                mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.d("INFO", "START");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mOngoingMode) {
                                    if (dir.getTime() > 0) {

                                        // No need to make the listener work
                                        mTTS.speak(getString(R.string.detail_direction_speak_start_timer_message),
                                                TextToSpeech.QUEUE_FLUSH, null, null);

                                        RecipeDetailFragment.this.showTimerDialog();
                                    } else {
                                        RecipeDetailFragment.this.showCommandsDialog();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e("ERROR", "error on " + utteranceId);
                    }
                });

                // Utterance ID is needed to make the listener work
                mTTS.speak(getString(R.string.detail_direction_label) + " " + dir.getSortNumber() +
                                ". " + text, TextToSpeech.QUEUE_FLUSH, null,
                        TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
            }

        } else {
            // Ended ongoing mode
            exitFromOngoingMode();

            // No need to make the listener work
            mTTS.speak(getString(R.string.detail_direction_speak_end_of_recipe_message),
                    TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    /**
     * Set timer dialog
     *
     * @param time Time to count down
     */
    public void setTimerDialog(Integer time) {
        mTimerDialog = new Dialog(getActivity());
        mTimerDialog.getWindow().setWindowAnimations(R.style.LateralDialogAnimation);
        mTimerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTimerDialog.setContentView(R.layout.dialog_detail_timer);

        final TextView minutesTextView = (TextView) mTimerDialog.findViewById(R.id.minutes);
        final TextView secondsTextView = (TextView) mTimerDialog.findViewById(R.id.seconds);
        Button skipButton = (Button) mTimerDialog.findViewById(R.id.buttonSkip);
        final ProgressBar progressBar = (ProgressBar) mTimerDialog.findViewById(R.id.progressBar);

        minutesTextView.setText(String.format("%d", (time / 60)));
        secondsTextView.setText(String.format("%d", (time % 60)));

        progressBar.setMax(time);
        progressBar.setProgress(time);

        mCountDownTimer = new CountDownTimer(time * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (millisUntilFinished / 1000));

                Integer m = (int) ((millisUntilFinished / 1000) / 60);
                Integer s = (int) (millisUntilFinished / 1000) % 60;
                String mString = m + "";
                String sString = s + "";
                if (sString.length() == 1) {
                    sString = "0" + sString;
                }
                minutesTextView.setText(String.format("%s", mString));
                secondsTextView.setText(String.format("%s", sString));
            }

            public void onFinish() {
                progressBar.setProgress(0);

                minutesTextView.setText(String.format("%s", "0"));
                secondsTextView.setText(String.format("%s", "00"));

                // TODO: Pause before dismiss & play an alarm during x seconds

                MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.alarm);
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();

                        // Hide dialog
                        mTimerDialog.dismiss();

                        // Wait for command
                        if (mOngoingMode) {
                            RecipeDetailFragment.this.showCommandsDialog();
                        }
                    }
                });
            }
        };

        skipButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.cancel();
                mTimerDialog.dismiss();

                // Wait for command
                if (mOngoingMode) {
                    RecipeDetailFragment.this.showCommandsDialog();
                }
            }
        });

        mTimerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mCountDownTimer.cancel();
                    mTimerDialog.dismiss();
                }
                return true;
            }
        });
    }

    /**
     * Show timer dialog set up in setTimerDialog
     */
    public void showTimerDialog() {
        RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(mPresentDescriptionIndex);

        if (dir.getTime() > 0) {
            this.setTimerDialog((int) dir.getTime().floatValue());

            // Show timer directly
            mTimerDialog.show();
            mCountDownTimer.start();
        }
    }


    /**
     * Set and show get commands dialog
     */
    public void showCommandsDialog() {
        mCommandsDialog = new Dialog(getActivity());
        mCommandsDialog.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
        mCommandsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCommandsDialog.setContentView(R.layout.dialog_detail_commands);

        ImageButton repeatButton = (ImageButton) mCommandsDialog.findViewById(R.id.repeat);
        ImageButton timerButton = (ImageButton) mCommandsDialog.findViewById(R.id.timer);
        ImageButton nextButton = (ImageButton) mCommandsDialog.findViewById(R.id.next);

        final FloatingActionButton fab = (FloatingActionButton)
                mCommandsDialog.findViewById(R.id.listeningFab);

        final TextView mainText = (TextView) mCommandsDialog.findViewById(R.id.mainText);
        final TextView errorText = (TextView) mCommandsDialog.findViewById(R.id.errorText);

        final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        // Disable timer button if there's no time specified
        final RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(mPresentDescriptionIndex);
        if (dir.getTime() == 0) {
            timerButton.setVisibility(View.GONE);
        }

        repeatButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommandsDialog.dismiss();

                // Repeat present direction
                RecipeDetailFragment.this.readDescription();
            }
        });

        timerButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommandsDialog.dismiss();

                // Show timer
                RecipeDetailFragment.this.showTimerDialog();
            }
        });

        nextButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommandsDialog.dismiss();

                // Read next direction
                mPresentDescriptionIndex = mPresentDescriptionIndex + 1;
                RecipeDetailFragment.this.readDescription();
            }
        });


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());

        final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // TODO: Set user language
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getActivity().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        // This is not working (it waits a random number of seconds)
        // 'Fixed' with a countdown timer (mSpeechRecognitionTimer)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 100000);


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(final int errorCode) {
                // Cancel animation and show errors
                anim.cancel();
                mainText.setText(getString(R.string.detail_commands_error_message));
                errorText.setText(getErrorText(errorCode));

                // Restart timer before restart speech recognition
                restartSpeechRecognizerTimer();
            }

            @Override
            public void onEvent(int arg0, Bundle arg1) {
            }

            @Override
            public void onPartialResults(Bundle arg0) {
            }

            @Override
            public void onReadyForSpeech(Bundle arg0) {

                // Restart animation and default texts
                String message = getString(R.string.voice_error_not_understood);
                if (dir.getTime() > 0) {
                    message = message + " " + getString(R.string.voice_command_repeat) +
                            ", " + getString(R.string.voice_command_timer) + " " +
                            getString(R.string.voice_command_next) +
                            getString(R.string.voice_or) + " " +
                            getString(R.string.voice_command_exit);
                } else {
                    message = message + " " + getString(R.string.voice_command_repeat) +
                            getString(R.string.voice_command_next) +
                            " " + getString(R.string.voice_or) + " " +
                            getString(R.string.voice_command_exit);
                }

                mainText.setText(getString(R.string.detail_commands_listening_message));
                errorText.setText(message);
                fab.startAnimation(anim);


                // Cancel timer
                if (mSpeechRecognizerTimer != null) {
                    mSpeechRecognizerTimer.cancel();
                    mSpeechRecognizerTimerIsRunning = false;
                }
            }

            @Override
            public void onResults(final Bundle results) {
                boolean keepGoing = false;

                // Cancel timer
                if (mSpeechRecognizerTimer != null) {
                    mSpeechRecognizerTimer.cancel();
                }

                // Cancel animation
                anim.cancel();

                // Get voice results
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {
                    if (matches.contains(getString(R.string.voice_command_repeat))) {

                        mainText.setText(getString(R.string.detail_commands_success_message));
                        mCommandsDialog.dismiss();

                        // Repeat present direction
                        RecipeDetailFragment.this.readDescription();
                    } else {
                        if (matches.contains(getString(R.string.voice_command_next))) {

                            mainText.setText(getString(R.string.detail_commands_success_message));
                            mCommandsDialog.dismiss();

                            // Read next direction
                            mPresentDescriptionIndex = mPresentDescriptionIndex + 1;
                            RecipeDetailFragment.this.readDescription();

                        } else {
                            if (matches.contains(getString(R.string.voice_command_timer)) &&
                                    dir.getTime() > 0) {

                                mainText.setText(getString(R.string.detail_commands_success_message));
                                mCommandsDialog.dismiss();

                                // Show timer
                                RecipeDetailFragment.this.showTimerDialog();

                            } else {
                                if (!matches.contains(getString(R.string.voice_command_exit))) {
                                    keepGoing = true;
                                } else {
                                    Log.d("INFO", "End of automatic speech");
                                    exitFromOngoingMode();
                                }
                            }
                        }
                    }
                } else {
                    keepGoing = true;
                }

                if (keepGoing) {
                    // Continue waiting for orders
                    String message = getString(R.string.voice_error_not_understood);
                    if (dir.getTime() > 0) {
                        message = message + " " + getString(R.string.voice_command_repeat) +
                                ", " + getString(R.string.voice_command_timer) + " " +
                                getString(R.string.voice_command_next) +
                                getString(R.string.voice_or) + " " +
                                getString(R.string.voice_command_exit);
                    } else {
                        message = message + " " + getString(R.string.voice_command_repeat) +
                                getString(R.string.voice_command_next) +
                                " " + getString(R.string.voice_or) + " " +
                                getString(R.string.voice_command_exit);
                    }

                    mainText.setText(getString(R.string.detail_commands_error_message));
                    errorText.setText(message);

                    // Restart timer before restart speech recognition
                    restartSpeechRecognizerTimer();
                }
            }

            /*
             * Define timer basic methods if it's not setted up
             * Reload timer if it's not running yet
             */
            private void restartSpeechRecognizerTimer() {
                Log.d("INFO", "after waiting 5 seconds");
                Log.d("Speech", "onResults: Start a timer");
                if (mSpeechRecognizerTimer == null) {
                    mSpeechRecognizerTimer = new CountDownTimer(2000, 500) {
                        @Override
                        public void onTick(long l) {
                            mSpeechRecognizerTimerIsRunning = true;
                        }

                        @Override
                        public void onFinish() {
                            Log.d("Speech", "Timer.onFinish: Timer Finished, Restart recognizer");
                            mSpeechRecognizer.cancel();
                            mSpeechRecognizer.startListening(recognizerIntent);

                            mSpeechRecognizerTimerIsRunning = false;
                        }
                    };
                }

                if (!mSpeechRecognizerTimerIsRunning) {
                    mSpeechRecognizerTimer.start();
                }
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            public String getErrorText(int errorCode) {
                String message;
                switch (errorCode) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = getString(R.string.voice_error_audio);
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = getString(R.string.voice_error_client);
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = getString(R.string.voice_error_insufficient_permissions);
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = getString(R.string.voice_error_network);
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = getString(R.string.voice_error_network_timeout);
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = getString(R.string.voice_error_no_match);
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = getString(R.string.voice_error_recognizer_busy);
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = getString(R.string.voice_error_server);
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = getString(R.string.voice_error_speech_timeout);
                        if (dir.getTime() > 0) {
                            message = message + " " + getString(R.string.voice_command_repeat) +
                                    ", " + getString(R.string.voice_command_timer) + " " +
                                    getString(R.string.voice_command_next) +
                                    getString(R.string.voice_or) + " " +
                                    getString(R.string.voice_command_exit);
                        } else {
                            message = message + " " + getString(R.string.voice_command_repeat) +
                                    getString(R.string.voice_command_next) +
                                    " " + getString(R.string.voice_or) + " " +
                                    getString(R.string.voice_command_exit);
                        }
                        break;
                    default:
                        message = getString(R.string.voice_error_not_understood);
                        if (dir.getTime() > 0) {
                            message = message + " " + getString(R.string.voice_command_repeat) +
                                    ", " + getString(R.string.voice_command_timer) + " " +
                                    getString(R.string.voice_command_next) +
                                    getString(R.string.voice_or) + " " +
                                    getString(R.string.voice_command_exit);
                        } else {
                            message = message + " " + getString(R.string.voice_command_repeat) +
                                    getString(R.string.voice_command_next) +
                                    " " + getString(R.string.voice_or) + " " +
                                    getString(R.string.voice_command_exit);
                        }
                        break;
                }
                return message;
            }
        });


        mCommandsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mSpeechRecognizer.cancel();
                    mSpeechRecognizer.destroy();
                    mCommandsDialog.dismiss();
                }
                return true;
            }
        });

        mCommandsDialog.show();
        mSpeechRecognizer.startListening(recognizerIntent);
    }


    // SCROLL METHODS

    public ObservableScrollView getScrollView() {
        return mScrollView;
    }

    public void scrollUp() {
        mScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();

        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY,
                minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mRecipeImage, ScrollUtils.getFloat(-scrollY / 2,
                minOverlayTransitionY, 0));

        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange,
                0, 1));

        // Move recipe text views
        ViewHelper.setPivotX(mRecipeName, 0);
        ViewHelper.setPivotY(mRecipeName, 0);
        ViewHelper.setPivotX(mRecipeOwner, 0);
        ViewHelper.setPivotY(mRecipeOwner, 0);


        int maxTextTranslationY = mFlexibleSpaceImageHeight - mRecipeName.getHeight() -
                mRecipeOwner.getHeight();
        int textTranslationY = maxTextTranslationY - scrollY;

        ViewHelper.setTranslationY(mRecipeName, textTranslationY);
        ViewHelper.setTranslationY(mRecipeOwner, textTranslationY);

        // Move FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            // Hide FAB
            mFab.animate().cancel();
            mFab.animate().scaleX(0).alpha(0).setDuration(150).start();
        } else {
            // Show FAB
            mFab.animate().cancel();
            mFab.animate().scaleX(1).scaleY(1).alpha(1).setDuration(150).start();
        }
    }

    // Needed to prevent an error
    @Override
    public void onDownMotionEvent() {}

    // Needed to prevent an error
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {}
}
