package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Information fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains settings information.
 *
 * Related layouts:
 * - Content: fragment_settings.xml
 */
public class SettingsFragment extends Fragment {

    private SharedPreferences mSharedPreferences;

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
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate()");

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    }


    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * onCreate and onActivityCreated, onViewStateRestored, onStart().
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If this fragment is being re-constructed from a previous saved
     *                           state as given here.
     * @return Return the View for the this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");

        ScrollView mLayout = (ScrollView) inflater.inflate(R.layout.fragment_settings,
                container, false);

        // Get preferences
        boolean downloadImagesSetting = mSharedPreferences.getBoolean(
                getString(R.string.preference_download_images), false);
        boolean recognizerLanguageSetting = mSharedPreferences.getBoolean(
                getString(R.string.preference_recognizer_language), false);

        // Get views and set its values
        Switch downloadImagesSwitch = (Switch) mLayout.findViewById(R.id.setting_download_images_enabled);
        downloadImagesSwitch.setChecked(downloadImagesSetting);

        Switch recognizerLanguageSwitch = (Switch) mLayout.findViewById(R.id.setting_recognizer_language_enabled);
        recognizerLanguageSwitch.setChecked(recognizerLanguageSetting);

        RelativeLayout languageSetting = (RelativeLayout) mLayout.findViewById(R.id.setting_recipes_languages_item);

        // Listeners
        downloadImagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(getString(R.string.preference_download_images), isChecked);
                editor.apply();
            }
        });

        recognizerLanguageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(getString(R.string.preference_recognizer_language), isChecked);
                editor.apply();
            }
        });

        languageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog languages = new LanguagesDialog(getActivity(), mSharedPreferences);
                languages.show();
            }
        });

        return mLayout;
    }

    /**
     * Languages dialog class.
     * Author: Noelia Sales <noelia.salesmontes@gmail.com
     *
     * It contains a list with all languages to let the user select one or more.
     *
     * Layouts:
     * - dialog_languages.xml
     * - dialog_languages_item.xml
     *
     * TODO: Probably needs its own file (show this the first time we launch the app)
     */
    public class LanguagesDialog extends Dialog {

        private String SEPARATOR = ",";

        // Data
        private ArrayList<Pair<String, Integer>> mLanguages = new ArrayList<>(Arrays.asList(
                new Pair<>("es", R.string.language_es),
                new Pair<>("en", R.string.language_en)
        ));
        private ArrayList<String> mSelectedLanguages = new ArrayList<>();

        // UI elements
        protected ListView list;
        protected LanguagesArrayAdapter adapter;
        protected Button acceptButton;
        protected Button cancelButton;


        /**
         * Dialog constructor
         *          *
         * @param context Application context
         * @param preferences Present active preferences
         */
        public LanguagesDialog(final Context context, final SharedPreferences preferences) {
            // Set your theme here
            super(context);

            this.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setContentView(R.layout.dialog_edition_direction);
            this.setContentView(R.layout.dialog_languages);

            // Get languages from shared preferences
            final String languages = preferences.getString(getString(R.string.preference_recipes_languages), "");
            if (languages.length() > 0) {
                mSelectedLanguages = new ArrayList<>(Arrays.asList(languages.split(SEPARATOR)));
            }

            // Get layout elements
            list = (ListView) findViewById(R.id.languages);
            cancelButton = (Button) findViewById(R.id.cancel);
            acceptButton = (Button) findViewById(R.id.accept);

            // Set list
            adapter = new LanguagesArrayAdapter(mSelectedLanguages, mLanguages);
            list.setAdapter(adapter);

            // Set buttons' listeners
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LanguagesDialog.this.dismiss();
                }
            });

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter.getSelected().size() > 0) {
                        // Set languages as preference
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.preference_recipes_languages),
                                android.text.TextUtils.join(SEPARATOR, adapter.getSelected()));
                        editor.apply();

                        LanguagesDialog.this.dismiss();
                    } else {
                        Toast.makeText(context, getString(R.string.settings_language_not_selected_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        /**
         * Languages list adapter
         */
        public class LanguagesArrayAdapter extends BaseAdapter {

            // Data
            private ArrayList<String> mSelected;
            private ArrayList<Pair<String, Integer>> mLanguages;

            /**
             * Constructor
             * @param selected Selected languages arraylist
             * @param languages Existing languages arraylist
             */
            public LanguagesArrayAdapter(ArrayList<String> selected,
                                         ArrayList<Pair<String, Integer>> languages) {
                mSelected = selected;
                mLanguages = languages;
            }

            @Override
            public int getCount() {
                return mLanguages.size();
            }

            @Override
            public Pair<String, Integer> getItem(int i) {
                return mLanguages.get(i);
            }

            public ArrayList<String> getSelected() {
                return mSelected;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = new LanguageView(getContext());
                }

                ((LanguageView) convertView).setLanguage(getString(getItem(position).second));
                ((LanguageView) convertView).setLanguageTag(getItem(position).first);
                ((LanguageView) convertView).setInitialChecked(mSelected.contains(getItem(position).first));

                return convertView;
            }

            /**
             * Languages list item view
             */
            public class LanguageView extends RelativeLayout implements Checkable {

                // UI elements
                private TextView mLanguageTextView;
                private CheckBox mCheckbox;

                // Useful data
                private String mLanguageTag;
                private Boolean checked = false;

                /**
                 * Constructor
                 * @param context Application context, needed to inflate the view
                 */
                public LanguageView(Context context) {
                    super(context);

                    inflate(context, R.layout.dialog_languages_item, this);
                    mLanguageTextView = (TextView) findViewById(R.id.language);
                    mCheckbox = (CheckBox) findViewById(R.id.checkbox);

                    mCheckbox.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LanguageView.this.toggle();
                        }
                    });
                }


                // GETTERS

                /** Get checkbox value
                 * @return Boolean checkbox present state
                 */
                @Override
                public boolean isChecked() {
                    return checked;
                }

                /** Unused method
                 * It was being called randomly
                 */
                @Override
                public void setChecked(boolean checked) {}


                // SETTERS

                /** Set initial checkbox value
                 * @param checked Boolean checkbox state value
                 */
                public void setInitialChecked(boolean checked) {
                    this.checked = checked;
                    mCheckbox.setChecked(checked);
                }

                /** Toogle the checkbox value */
                @Override
                public void toggle() {
                    checked = !checked;
                    mCheckbox.setChecked(checked);

                    if (checked) {
                        if (!mSelected.contains(mLanguageTag)) {
                            mSelected.add(mLanguageTag);
                        }
                    } else {
                        mSelected.remove(mLanguageTag);
                    }
                }

                /** Set initial text view value
                 * @param language Language string
                 */
                public void setLanguage(String language) {
                    mLanguageTextView.setText(language);
                }

                /** Set initial tag value
                 * @param tag Language tag string
                 */
                public void setLanguageTag(String tag) {
                    mLanguageTag = tag;
                }
            }
        }
    }
}
