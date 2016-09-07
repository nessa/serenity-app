package com.amusebouche.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amusebouche.activities.DetailActivity;
import com.amusebouche.activities.MediaActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.services.AppData;
import com.amusebouche.ui.CustomNumberPicker;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * Recipe detail third tab fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of detail activity.
 * It's used inside a list of tabs.
 * It contains and shows the recipe directions.
 *
 * Related layouts:
 * - Content: fragment_detail_third_tab.xml
 */
public class RecipeDetailThirdTabFragment extends Fragment {

    // Father activity
    private DetailActivity mDetailActivity;
    
    // Data variables
    private Integer mPresentDescriptionIndex;

    // Behaviour variables
    private boolean mOngoingMode;

    // Services variables
    private TextToSpeech mTTS;

    // UI
    private RecyclerAdapterWithHeader mDirectionsAdapter;
    private Dialog mDirectionDialog;

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
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");

        mPresentDescriptionIndex = 0;

        if (mTTS == null) {
            mTTS = new TextToSpeech(this.getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        Locale loc = new Locale(mDetailActivity.getRecipe().getLanguage().toLowerCase(),
                            AppData.getLocaleCountryFromCode(
                                mDetailActivity.getRecipe().getLanguage().toLowerCase()));
                        mTTS.setLanguage(loc);
                    }
                }
            });
        }

        onReloadView();
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

        // Get recipe from activity
        mDetailActivity = (DetailActivity) getActivity();

        RecyclerView mLayout = (RecyclerView) inflater.inflate(R.layout.fragment_detail_third_tab,
                container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mLayout.setLayoutManager(linearLayoutManager);

        mDirectionsAdapter = new RecyclerAdapterWithHeader();
        mLayout.setAdapter(mDirectionsAdapter);

        return mLayout;
    }


    /**
     * Reload all UI elements with the mRecipe data.
     */
    public void onReloadView() {
        mDirectionsAdapter.notifyDataSetChanged();
    }
    
    // TEXT TO SPEECH AND DIALOGS

    /**
     * Exit from 'ongoing mode'
     */
    public void exitFromOngoingMode() {
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
        if (mDetailActivity.getRecipe().getDirections().size() > mPresentDescriptionIndex) {
            final RecipeDirection dir = mDetailActivity.getRecipe().getDirections().get(mPresentDescriptionIndex);
            CharSequence text = dir.getDescription();

            if (text != "") {
                // Show direction
                showDirectionDialog();

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
                                RecipeDetailThirdTabFragment.this.mDirectionDialog.dismiss();

                                if (mOngoingMode) {
                                    if (dir.getTime() > 0) {

                                        // No need to make the listener work
                                        mTTS.speak(getString(R.string.detail_direction_speak_start_timer_message),
                                                TextToSpeech.QUEUE_FLUSH, null, null);

                                        RecipeDetailThirdTabFragment.this.showTimerDialog();
                                    } else {
                                        RecipeDetailThirdTabFragment.this.showCommandsDialog();
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
                mTTS.speak(String.format(getString(R.string.detail_direction_label_with_number),
                    dir.getSortNumber()) + ". " + text, TextToSpeech.QUEUE_FLUSH, null,
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

    public void setDirectionDialog(Integer number, String description) {
        mDirectionDialog = new Dialog(getActivity());
        mDirectionDialog.getWindow().setWindowAnimations(R.style.LateralDialogAnimation);
        mDirectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDirectionDialog.setContentView(R.layout.dialog_detail_direction);

        final TextView numberTextView = (TextView) mDirectionDialog.findViewById(R.id.number);
        final TextView descriptionTextView = (TextView) mDirectionDialog.findViewById(R.id.description);

        numberTextView.setText(String.format(getString(R.string.detail_direction_label_with_number),
                number));
        descriptionTextView.setText(description);
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

        minutesTextView.setText(String.valueOf((time / 60)));
        secondsTextView.setText(String.valueOf((time % 60)));

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
                            RecipeDetailThirdTabFragment.this.showCommandsDialog();
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
                    RecipeDetailThirdTabFragment.this.showCommandsDialog();
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
     * Show direction dialog set up in setDirectionDialog
     */
    public void showDirectionDialog() {
        RecipeDirection dir = mDetailActivity.getRecipe().getDirections().get(mPresentDescriptionIndex);

        this.setDirectionDialog(dir.getSortNumber(), dir.getDescription());

        mDirectionDialog.show();
    }

    /**
     * Show timer dialog set up in setTimerDialog
     */
    public void showTimerDialog() {
        RecipeDirection dir = mDetailActivity.getRecipe().getDirections().get(mPresentDescriptionIndex);

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
                mCommandsDialog.findViewById(R.id.listening_fab);

        final TextView mainText = (TextView) mCommandsDialog.findViewById(R.id.mainText);
        final TextView errorText = (TextView) mCommandsDialog.findViewById(R.id.errorText);

        final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        // Disable timer button if there's no time specified
        final RecipeDirection dir = mDetailActivity.getRecipe().getDirections().get(mPresentDescriptionIndex);
        if (dir.getTime() == 0) {
            timerButton.setVisibility(View.GONE);
        }

        repeatButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommandsDialog.dismiss();

                // Repeat present direction
                RecipeDetailThirdTabFragment.this.readDescription();
            }
        });

        timerButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommandsDialog.dismiss();

                // Show timer
                RecipeDetailThirdTabFragment.this.showTimerDialog();
            }
        });

        nextButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommandsDialog.dismiss();

                // Read next direction
                mPresentDescriptionIndex = mPresentDescriptionIndex + 1;
                RecipeDetailThirdTabFragment.this.readDescription();
            }
        });


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());

        // Set recognizer data
        final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Set recognizer language
        if (mDetailActivity.getRecognizerLanguageSetting()) {
            // Device language
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault().getLanguage());
        } else {
            // Recipe language
            Locale loc = new Locale(mDetailActivity.getRecipe().getLanguage().toLowerCase(),
                AppData.getLocaleCountryFromCode(mDetailActivity.getRecipe().getLanguage().toLowerCase()));
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, loc);
        }

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
                String message = getString(R.string.voice_commands_list);
                if (dir.getTime() > 0) {
                    message = message + " " + getString(R.string.voice_command_repeat) +
                            ", " + getString(R.string.voice_command_timer) + ", " +
                            getString(R.string.voice_command_next) +
                            getString(R.string.voice_or) + " " +
                            getString(R.string.voice_command_exit);
                } else {
                    message = message + " " + getString(R.string.voice_command_repeat) +
                            ", " + getString(R.string.voice_command_next) +
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
                        RecipeDetailThirdTabFragment.this.readDescription();
                    } else {
                        if (matches.contains(getString(R.string.voice_command_next))) {

                            mainText.setText(getString(R.string.detail_commands_success_message));
                            mCommandsDialog.dismiss();

                            // Read next direction
                            mPresentDescriptionIndex = mPresentDescriptionIndex + 1;
                            RecipeDetailThirdTabFragment.this.readDescription();

                        } else {
                            if (matches.contains(getString(R.string.voice_command_timer)) &&
                                    dir.getTime() > 0) {

                                mainText.setText(getString(R.string.detail_commands_success_message));
                                mCommandsDialog.dismiss();

                                // Show timer
                                RecipeDetailThirdTabFragment.this.showTimerDialog();

                            } else {
                                if (!matches.contains(getString(R.string.voice_command_exit))) {
                                    keepGoing = true;
                                } else {
                                    Log.d("INFO", "End of automatic speech");
                                    mCommandsDialog.dismiss();
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
                    String message = getString(R.string.voice_error_not_understood) + " " +
                            getString(R.string.voice_commands_list);
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
                        message = getString(R.string.voice_error_speech_timeout) + " " +
                                getString(R.string.voice_commands_list);
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
                        message = getString(R.string.voice_error_not_understood) + " " +
                                getString(R.string.voice_commands_list);
                        if (dir.getTime() > 0) {
                            message = message + " " + getString(R.string.voice_command_repeat) +
                                    ", " + getString(R.string.voice_command_timer) + ", " +
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

    public boolean isInOngoingMode() {
        return mOngoingMode;
    }


    /**
     * Adapter for a recycler view with header that shows the list of directions
     */
    public class RecyclerAdapterWithHeader extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        public RecyclerAdapterWithHeader() {}

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_detail_direction, parent, false);
                return new VHItem(v);
            } else if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_detail_third_tab_header, parent, false);
                return new VHHeader(v);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof VHItem) {
                VHItem itemHolder = (VHItem) holder;

                final RecipeDirection presentDirection = getItem(position);

                itemHolder.number.setText(String.format(getString(R.string.detail_direction_label_with_number),
                        presentDirection.getSortNumber()));

                itemHolder.description.setText(presentDirection.getDescription());

                itemHolder.readDirectionButton.setTag(position - 1);
                itemHolder.showDirectionImageButton.setTag(position - 1);
                itemHolder.showDirectionVideoButton.setTag(position - 1);
                itemHolder.directionTimerButton.setTag(position - 1);

                itemHolder.readDirectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOngoingMode = false;

                        RecipeDirection dir = mDetailActivity.getRecipe().getDirections().get(
                            (int) v.getTag());
                        CharSequence text = dir.getDescription();

                        if (text != "") {
                            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                });

                if (presentDirection.getImage().equals("")) {
                    itemHolder.showDirectionImageButton.setVisibility(View.GONE);
                } else {
                    itemHolder.showDirectionImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecipeDirection dir = mDetailActivity.getRecipe().getDirections().get(
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
                    itemHolder.showDirectionVideoButton.setVisibility(View.GONE);
                } else {
                    itemHolder.showDirectionVideoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(presentDirection.getVideo()));
                            startActivity(intent);
                        }
                    });
                }

                if (presentDirection.getTime() == 0) {
                    itemHolder.directionTimerButton.setVisibility(View.GONE);
                } else {
                    itemHolder.directionTimerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecipeDirection dir = mDetailActivity.getRecipe().getDirections().get(
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

                            hoursTextView.setText(String.valueOf(mTimerHours));
                            minutesTextView.setText(String.valueOf(mTimerMinutes));
                            secondsTextView.setText(String.valueOf(mTimerSeconds));

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
                                    RecipeDetailThirdTabFragment.this.mTimerHours = newVal;
                                    hoursTextView.setText(String.valueOf(mTimerHours));
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
                                    RecipeDetailThirdTabFragment.this.mTimerMinutes = newVal;
                                    minutesTextView.setText(String.valueOf(mTimerMinutes));
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
                                    RecipeDetailThirdTabFragment.this.mTimerSeconds = newVal;
                                    secondsTextView.setText(String.valueOf(mTimerSeconds));
                                }
                            });

                            // Buttons
                            Button cancelButton = (Button) selectTimeDialog.findViewById(R.id.buttonCancel);
                            Button setButton = (Button) selectTimeDialog.findViewById(R.id.buttonSet);

                            setButton.setOnClickListener(new Button.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    RecipeDetailThirdTabFragment.this.mTimerHours = hoursPicker.getValue();
                                    RecipeDetailThirdTabFragment.this.mTimerMinutes = minutesPicker.getValue();
                                    RecipeDetailThirdTabFragment.this.mTimerSeconds = secondsPicker.getValue();

                                    RecipeDetailThirdTabFragment.this.setTimerDialog(
                                        RecipeDetailThirdTabFragment.this.mTimerHours * 3600 +
                                            RecipeDetailThirdTabFragment.this.mTimerMinutes * 60 +
                                            RecipeDetailThirdTabFragment.this.mTimerSeconds);

                                    selectTimeDialog.dismiss();
                                    RecipeDetailThirdTabFragment.this.mTimerDialog.show();
                                    RecipeDetailThirdTabFragment.this.mCountDownTimer.start();
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

            } else if (holder instanceof VHHeader) {
                VHHeader headerHolder = (VHHeader) holder;

                headerHolder.startSpeakingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (RecipeDetailThirdTabFragment.this.mDetailActivity.getRecipe().getDirections().size() > 0) {
                            mOngoingMode = true;
                            mPresentDescriptionIndex = 0;
                            RecipeDetailThirdTabFragment.this.readDescription();
                        } else {
                            Log.d("INFO", "No directions");
                        }
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return mDetailActivity.getRecipe().getDirections().size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;

            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        private RecipeDirection getItem(int position) {
            return mDetailActivity.getRecipe().getDirections().get(position - 1);
        }

        class VHItem extends RecyclerView.ViewHolder {
            public TextView number;
            public TextView description;
            public ImageButton readDirectionButton;
            public ImageButton showDirectionImageButton;
            public ImageButton showDirectionVideoButton;
            public ImageButton directionTimerButton;

            public VHItem(View itemView) {
                super(itemView);

                number = (TextView) itemView.findViewById(R.id.number);
                description = (TextView) itemView.findViewById(R.id.description);

                readDirectionButton = (ImageButton) itemView.findViewById(
                    R.id.readDescription);
                showDirectionImageButton = (ImageButton) itemView.findViewById(
                    R.id.showPhoto);
                showDirectionVideoButton = (ImageButton) itemView.findViewById(
                    R.id.showVideo);
                directionTimerButton = (ImageButton) itemView.findViewById(
                    R.id.timer);
            }
        }

        class VHHeader extends RecyclerView.ViewHolder {
            public Button startSpeakingButton;

            public VHHeader(View itemView) {
                super(itemView);

                startSpeakingButton = (Button) itemView.findViewById(R.id.startSpeakingButton);
            }
        }
    }
}
