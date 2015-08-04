package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.Objects;

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
public class RecipeDetailFragment extends Fragment
        implements ObservableScrollViewCallbacks {

    private FrameLayout mLayout;
    private ObservableScrollView mScrollView;
    private Recipe mRecipe;
    private Bitmap mMainImage;
    private View mOverlayView;
    private TextView mRecipeName;
    private ImageView mRecipeImage;
    private FloatingActionButton mFab;
    private TextToSpeech mTTS;
    private Dialog mChronometerDialog;

    private Integer mPresentDescriptionIndex;
    private Integer mChronoHours;
    private Integer mChronoMinutes;
    private Integer mChronoSeconds;

    private Integer mFlexibleSpaceImageHeight;
    private Integer mFlexibleSpaceShowFabOffset;
    private Integer mFabMargin;
    private Integer mActionBarSize;
    private boolean mFabIsShown;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        // Get recipe from activity
        DetailActivity x = (DetailActivity)getActivity();
        mRecipe = x.getRecipe();

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

    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop()");

        if (mTTS != null) {
            mTTS.shutdown();
        }
    }

    public ObservableScrollView getScrollView() {
        return mScrollView;
    }

    public void scrollUp() {
        mScrollView.smoothScrollTo(0, 0);
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

        /* TODO: Try to prevent Skipped XX frames! The application may be doing too
         * much work on its main thread. */

        mLayout = (FrameLayout) inflater.inflate(R.layout.fragment_recipe_detail,
                container, false);

        mScrollView = (ObservableScrollView) mLayout.findViewById(R.id.scroll);

        mRecipeImage = (ImageView) mLayout.findViewById(R.id.recipe_image);
        mRecipeImage.setImageBitmap(mMainImage);

        mRecipeName = (TextView) mLayout.findViewById(R.id.recipe_name);
        mRecipeName.setText(mRecipe.getTitle());

        // Set view sizes
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(
                R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(
                R.dimen.flexible_space_show_fab_offset);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        mActionBarSize = getResources().getDimensionPixelSize(
                R.dimen.abc_action_bar_default_height_material);

        mOverlayView = mLayout.findViewById(R.id.overlay);
        mScrollView.setScrollViewCallbacks(this);
        mFab = (FloatingActionButton) mLayout.findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RecipeDetailFragment.this.mRecipe.getDirections().size() > 0) {
                    mPresentDescriptionIndex = 0;
                    RecipeDetailFragment.this.readDescription();
                } else {
                    Log.d("INFO", "No directions");
                }
            }
        });

        // Overlay view transparent
        ViewHelper.setAlpha(mOverlayView, 0);

        // Show and position FAB
        ViewHelper.setScaleX(mFab, 1);
        ViewHelper.setScaleY(mFab, 1);

        int maxTitleTranslationY = mFlexibleSpaceImageHeight - mRecipeName.getHeight();
        int titleTranslationY = maxTitleTranslationY - mActionBarSize;
        ViewHelper.setTranslationY(mRecipeName, titleTranslationY);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int windowWidth = size.x;

        int maxFabTranslationY = mFlexibleSpaceImageHeight - (int)getResources().getDimension(R.dimen.fab_size_normal) / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                mFlexibleSpaceImageHeight - (int)getResources().getDimension(R.dimen.fab_size_normal) / 2,
                mActionBarSize - (int)getResources().getDimension(R.dimen.fab_size_normal) / 2,
                maxFabTranslationY);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = windowWidth - mFabMargin -
                    (int)getResources().getDimension(R.dimen.fab_size_normal);
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, windowWidth - mFabMargin -
                    (int)getResources().getDimension(R.dimen.fab_size_normal));
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

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
            rating = (float)mRecipe.getTotalRating()/ (float)mRecipe.getUsersRating();
        }

        TextView ratingTextView = (TextView) mLayout.findViewById(R.id.rating);
        ratingTextView.setText(Objects.toString(rating));

        // Ingredients
        LinearLayout ingredientsLayout = (LinearLayout) mLayout.findViewById(R.id.ingredients);

        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            RecipeIngredient presentIngredient = (RecipeIngredient)mRecipe.getIngredients().get(i);

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
            RecipeDirection presentDirection = (RecipeDirection)mRecipe.getDirections().get(d);

            LinearLayout directionLayout = (LinearLayout) inflater.inflate(
                    R.layout.fragment_recipe_detail_direction, mLayout, false);
            directionLayout.setTag("direction"+d);

            TextView number = (TextView) directionLayout.findViewById(R.id.number);
            number.setText(getString(R.string.detail_direction_label) + " " +
                    Objects.toString(presentDirection.getSortNumber()));

            TextView description = (TextView) directionLayout.findViewById(R.id.description);
            description.setText(presentDirection.getDescription());

            LinearLayout extraLayout = (LinearLayout) directionLayout.findViewById(R.id.extra);

            ImageButton readDirectionButton = (ImageButton) extraLayout.findViewById(
                    R.id.readDescription);
            ImageButton showDirectionImageButton = (ImageButton) extraLayout.findViewById(
                    R.id.showPhoto);
            ImageButton showDirectionVideoButton = (ImageButton) extraLayout.findViewById(
                    R.id.showVideo);
            ImageButton directionChronometerButton = (ImageButton) extraLayout.findViewById(
                    R.id.chronometer);


            readDirectionButton.setTag(d);
            showDirectionImageButton.setTag(d);
            showDirectionVideoButton.setTag(d);
            directionChronometerButton.setTag(d);

            readDirectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(
                            (int)v.getTag());
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
                                (int)v.getTag());

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
                directionChronometerButton.setVisibility(View.GONE);
            } else {
                directionChronometerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(
                                (int) v.getTag());

                        // Calc time variables
                        Integer time = (int)dir.getTime().floatValue();

                        mChronoHours = time/3600;
                        mChronoMinutes = (time/60 ) % 60;
                        mChronoSeconds = time % 60;

                        // Set dialog attributes
                        final Dialog selectTimeDialog = new Dialog(getActivity());
                        selectTimeDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                        selectTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        selectTimeDialog.setContentView(R.layout.dialog_detail_chronometer_set_time);

                        final TextView hoursTextView = (TextView) selectTimeDialog.findViewById(R.id.hours);
                        final TextView minutesTextView = (TextView) selectTimeDialog.findViewById(R.id.minutes);
                        final TextView secondsTextView = (TextView) selectTimeDialog.findViewById(R.id.seconds);

                        hoursTextView.setText(mChronoHours + "");
                        minutesTextView.setText(mChronoMinutes + "");
                        secondsTextView.setText(mChronoSeconds + "");

                        // Number pickers for hours, minutes and seconds
                        final NumberPicker hoursPicker = (NumberPicker) selectTimeDialog.findViewById(R.id.hoursPicker);
                        hoursPicker.setMaxValue(10);
                        hoursPicker.setMinValue(0);
                        hoursPicker.setWrapSelectorWheel(false);
                        hoursPicker.setValue(mChronoHours);

                        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                RecipeDetailFragment.this.mChronoHours = newVal;
                                hoursTextView.setText(mChronoHours + "");
                            }
                        });

                        final NumberPicker minutesPicker = (NumberPicker) selectTimeDialog.findViewById(R.id.minutesPicker);
                        minutesPicker.setMaxValue(59);
                        minutesPicker.setMinValue(0);
                        minutesPicker.setWrapSelectorWheel(true);
                        minutesPicker.setValue(mChronoMinutes);

                        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                RecipeDetailFragment.this.mChronoMinutes = newVal;
                                minutesTextView.setText(mChronoMinutes + "");
                            }
                        });

                        final NumberPicker secondsPicker = (NumberPicker) selectTimeDialog.findViewById(R.id.secondsPicker);
                        secondsPicker.setMaxValue(59);
                        secondsPicker.setMinValue(0);
                        secondsPicker.setWrapSelectorWheel(false);
                        secondsPicker.setValue(mChronoSeconds);

                        secondsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                RecipeDetailFragment.this.mChronoSeconds = newVal;
                                secondsTextView.setText(mChronoSeconds + "");
                            }
                        });

                        // Buttons
                        Button cancelButton = (Button) selectTimeDialog.findViewById(R.id.buttonCancel);
                        Button setButton = (Button) selectTimeDialog.findViewById(R.id.buttonSet);

                        setButton.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecipeDetailFragment.this.mChronoHours = hoursPicker.getValue();
                                RecipeDetailFragment.this.mChronoMinutes = minutesPicker.getValue();
                                RecipeDetailFragment.this.mChronoSeconds = secondsPicker.getValue();

                                RecipeDetailFragment.this.setChronometer(
                                        RecipeDetailFragment.this.mChronoHours * 3600 +
                                                RecipeDetailFragment.this.mChronoMinutes * 60 +
                                                RecipeDetailFragment.this.mChronoSeconds);

                                selectTimeDialog.dismiss();
                                RecipeDetailFragment.this.mChronometerDialog.show();
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

    /**
     * Make TextToSpeech read the direction given by mPresentDescriptionIndex
     */
    public void readDescription() {
        if (mRecipe.getDirections().size() > mPresentDescriptionIndex) {
            RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(
                    mPresentDescriptionIndex);
            CharSequence text = dir.getDescription();

            // Move view to direction box
            final LinearLayout directionsLayout = (LinearLayout) mLayout.findViewById(R.id.directions);
            final View directionLayout = mLayout.findViewWithTag("direction" +
                    mPresentDescriptionIndex);

            // Hide FAB if necessary
            if (mPresentDescriptionIndex == 0) {
                this.hideFab();
            }
            mScrollView.smoothScrollTo(0, directionsLayout.getTop() + directionLayout.getBottom());

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
                                // Check chrono
                                RecipeDetailFragment.this.showChronometer();

                                // Read next description
                                mPresentDescriptionIndex = mPresentDescriptionIndex + 1;
                                RecipeDetailFragment.this.readDescription();
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
            // No need to make the listener work
            mTTS.speak(getString(R.string.detail_direction_speak_end_of_recipe_message),
                    TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    /**
     * Set chronometer dialog
     */
    public void setChronometer(Integer time) {
        mChronometerDialog = new Dialog(getActivity());
        mChronometerDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        mChronometerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mChronometerDialog.setContentView(R.layout.dialog_detail_chronometer);


        final TextView minutesTextView = (TextView) mChronometerDialog.findViewById(R.id.minutes);
        final TextView secondsTextView = (TextView) mChronometerDialog.findViewById(R.id.seconds);
        Button skipButton = (Button) mChronometerDialog.findViewById(R.id.buttonSkip);
        final ProgressBar progressBar = (ProgressBar) mChronometerDialog.findViewById(R.id.progressBar);

        minutesTextView.setText((time/60) + "");
        secondsTextView.setText((time % 60) + "");

        progressBar.setMax(time);
        progressBar.setProgress(time);

        CountDownTimer countDown =  new CountDownTimer(time * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (millisUntilFinished/1000));

                Integer m = (int) ((millisUntilFinished/1000)/60);
                Integer s = (int) (millisUntilFinished/1000) % 60;
                String mString = m + "";
                String sString = s + "";
                if (sString.length() == 1) {
                    sString = "0" + sString;
                }
                minutesTextView.setText(mString + "");
                secondsTextView.setText(sString + "");
            }

            public void onFinish() {
                progressBar.setProgress(0);

                minutesTextView.setText("0");
                secondsTextView.setText("00");

                // TODO: Pause before dismiss & play an alarm during x seconds

                // Hide dialog
                mChronometerDialog.dismiss();

                // TODO: Next direction??
            }
        };
        countDown.start();

        skipButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChronometerDialog.dismiss();
            }
        });
    }

    public void showChronometer() {
        RecipeDirection dir = (RecipeDirection) mRecipe.getDirections().get(mPresentDescriptionIndex);

        if (dir.getTime() > 0) {
            Log.d("INFO", "CHRONO: " + dir.getTime());
            this.setChronometer((int) dir.getTime().floatValue());

            // TODO: Show chronometer directly
        }
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
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private String getTypeOfDish(String code) {
        switch(code) {
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

    private String getDifficulty(String code) {
        switch(code) {
            case "HIGH":
                return getString(R.string.difficulty_high);
            case "LOW":
                return getString(R.string.difficulty_low);
            default:
            case "MEDIUM":
                return getString(R.string.difficulty_medium);
        }
    }

    private String getCookingTime(float time) {
        int intTime = (int)time;
        return Objects.toString(intTime) + " " + getString(R.string.detail_minutes);
    }

    private String getIngredientQuantity(float quantity, String unit_code) {
        String q = "", u = "";
        boolean plural = true;

        if (quantity > 0) {
            float result = quantity - (int)quantity;
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
            switch(unit_code) {
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

    @Override
    public void onDownMotionEvent() {}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

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

        // Move recipe name text
        ViewHelper.setPivotX(mRecipeName, 0);
        ViewHelper.setPivotY(mRecipeName, 0);

        int maxTitleTranslationY = mFlexibleSpaceImageHeight - mRecipeName.getHeight();
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mRecipeName, titleTranslationY);

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
            hideFab();
        } else {
            showFab();
        }
    }

    private void showFab() {
        if (!mFabIsShown) {
            mFab.animate().cancel();
            mFab.animate().scaleX(1).scaleY(1).alpha(1).setDuration(200).start();

            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            mFab.animate().cancel();
            mFab.animate().scaleX(0).alpha(0).setDuration(200).start();

            mFabIsShown = false;
        }
    }
}
