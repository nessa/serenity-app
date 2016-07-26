package com.amusebouche.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amusebouche.activities.R;
import com.amusebouche.services.AppData;

import java.util.ArrayList;

/**
 * Languages dialog class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * It contains a list with all languages to let the user select one or more.
 *
 * Layouts:
 * - dialog_languages.xml
 * - dialog_languages_item.xml
 */
public class LanguagesDialog extends Dialog {

    // UI elements
    protected ListView list;
    protected LanguagesArrayAdapter adapter;
    protected Button acceptButton;
    protected Button cancelButton;


    /**
     * Dialog constructor
     *
     * @param context Application context
     * @param preferences Present active preferences
     */
    public LanguagesDialog(final Context context, final SharedPreferences preferences) {
        // Set your theme here
        super(context);

        this.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_languages);

        // Get languages from shared preferences
        String mSelectedLanguage = preferences.getString(AppData.PREFERENCE_RECIPES_LANGUAGE, "");

        // Get layout elements
        list = (ListView) findViewById(R.id.languages);
        cancelButton = (Button) findViewById(R.id.cancel);
        acceptButton = (Button) findViewById(R.id.accept);

        // Set list
        adapter = new LanguagesArrayAdapter(context, mSelectedLanguage, AppData.LANGUAGES);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
                if (adapter.getSelected().equals("")) {
                    Toast.makeText(context, context.getString(R.string.settings_language_not_selected_error),
                        Toast.LENGTH_SHORT).show();
                } else {
                    // Set languages as preference
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(AppData.PREFERENCE_RECIPES_LANGUAGE,
                            adapter.getSelected());
                    editor.apply();

                    LanguagesDialog.this.dismiss();
                }
            }
        });

    }

    /**
     * Languages list adapter
     */
    public class LanguagesArrayAdapter extends BaseAdapter {

        private Context mContext;

        // Data
        private String mSelected;
        private ArrayList<Pair<String, Integer>> mLanguages;

        /**
         * Constructor
         * @param selected Selected languages arraylist
         * @param languages Existing languages arraylist
         */
        public LanguagesArrayAdapter(Context context, String selected,
                                     ArrayList<Pair<String, Integer>> languages) {
            mContext = context;
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

        public String getSelected() {
            return mSelected;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public String getItemLanguage(int i) {
            return mLanguages.get(i).first;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new LanguageView(getContext());
            }

            ((LanguageView) convertView).setLanguage(mContext.getString(getItem(position).second));
            convertView.setTag(position);
            ((LanguageView) convertView).setLanguageTag(getItem(position).first);
            ((LanguageView) convertView).setChecked(mSelected.equals(getItemLanguage(position)));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelected = getItemLanguage((int)v.getTag());
                    notifyDataSetChanged();
                }
            });

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

            /**
             * Constructor
             * @param context Application context, needed to inflate the view
             */
            public LanguageView(Context context) {
                super(context);

                inflate(context, R.layout.dialog_languages_item, this);
                mLanguageTextView = (TextView) findViewById(R.id.language);
                mCheckbox = (CheckBox) findViewById(R.id.checkbox);
            }


            // GETTERS

            /** Get checkbox value
             * @return Boolean checkbox present state
             */
            @Override
            public boolean isChecked() {
                return mCheckbox.isChecked();
            }

            /** Unused method
             * It was being called randomly
             */
            @Override
            public void setChecked(boolean checked) {
                mCheckbox.setChecked(mSelected.equals(mLanguageTag));
            }

            // SETTERS

            /** Toogle the checkbox value */
            @Override
            public void toggle() {}

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