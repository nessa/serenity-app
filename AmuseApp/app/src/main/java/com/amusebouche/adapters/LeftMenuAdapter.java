package com.amusebouche.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amusebouche.activities.MainActivity;
import com.amusebouche.activities.R;
import com.amusebouche.services.AppData;
import com.amusebouche.services.DatabaseHelper;


/**
 * Left menu cell adapter class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * It declares the view of each navigation drawer cells that contains:
 * - Icon.
 * - Name.
 *
 * Related layouts:
 * - Content: cell_left_menu
 */
public class LeftMenuAdapter extends BaseAdapter {
    private MainActivity mContext = null;

    private DatabaseHelper mDatabaseHelper;

    public LeftMenuAdapter(MainActivity context) {
        this.mContext = context;
        mDatabaseHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String getSectionString(int position) {
        switch (position) {
            default:
            case AppData.PROFILE:
                String user = mDatabaseHelper.getAppData(AppData.USER_SHOW_TEXT);
                if (user.equals("")) {
                    return mContext.getResources().getString(R.string.lateral_menu_login);
                } else {
                    return user;
                }
            case AppData.NEW_RECIPES:
                return mContext.getResources().getString(R.string.lateral_menu_new_recipes);
            case AppData.DOWNLOADED_RECIPES:
                return mContext.getResources().getString(R.string.lateral_menu_downloaded_recipes);
            case AppData.MY_RECIPES:
                return mContext.getResources().getString(R.string.lateral_menu_my_recipes);
            case AppData.SETTINGS:
                return mContext.getResources().getString(R.string.lateral_menu_settings);
            case AppData.INFO:
                return mContext.getResources().getString(R.string.lateral_menu_info);
        }
    }

    private int getSectionIcon(int position) {
        switch (position) {
            default:
            case AppData.PROFILE:
                return R.drawable.ic_user_white_48dp;
            case AppData.NEW_RECIPES:
                return R.drawable.ic_cook_white_48dp;
            case AppData.DOWNLOADED_RECIPES:
                return R.drawable.ic_download_white_48dp;
            case AppData.MY_RECIPES:
                return R.drawable.ic_tag_white_48dp;
            case AppData.SETTINGS:
                return R.drawable.ic_settings_white_48dp;
            case AppData.INFO:
                return R.drawable.ic_info_white_48dp;
        }
    }

    private boolean getSectionEnabled(int position) {
        return !((position == AppData.PROFILE || position == AppData.NEW_RECIPES) &&
                mContext.getOfflineModeSetting());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new DrawerView(mContext);
        }

        ((DrawerView) convertView).setTag(getSectionString(position), getSectionIcon(position),
            getSectionEnabled(position));

        return convertView;
    }

    public class DrawerView extends RelativeLayout implements Checkable {
        private View mCell;
        private TextView mTitleTextView;
        private ImageView mImageView;
        private View mColoredView;
        private boolean isEnabled;
        private boolean isChecked;

        public DrawerView(Context context) {
            super(context);
            inflate(context, R.layout.cell_left_menu, this);

            mCell = findViewById(R.id.drawer_relative);
            mTitleTextView = (TextView) findViewById(R.id.title);
            mImageView = (ImageView) findViewById(R.id.image);
            mColoredView = findViewById(R.id.line);
        }

        public void setTag(String title, int imageId, boolean enabled) {
            mTitleTextView.setText(title);
            mImageView.setImageResource(imageId);
            isEnabled = enabled;

            mCell.setClickable(!isEnabled);
        }

        private void highlightCell() {
            if (isChecked) {
                mTitleTextView.setTextColor(getResources().getColor(R.color.theme_default_accent));
                mImageView.setColorFilter(getResources().getColor(R.color.theme_default_accent));
                mColoredView.setVisibility(View.VISIBLE);
            } else {
                if (isEnabled) {
                    mTitleTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));
                    mImageView.setColorFilter(getResources().getColor(R.color.drawer_text_color));
                } else {
                    mTitleTextView.setTextColor(getResources().getColor(R.color.secondary_text));
                    mImageView.setColorFilter(getResources().getColor(R.color.secondary_text));
                }
                mColoredView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void setTag(Object tag) {
            super.setTag(tag);
        }

        @Override
        public boolean isChecked() {
            return isChecked;
        }

        @Override
        public void setChecked(boolean checked) {
            isChecked = isEnabled && checked;
            highlightCell();
        }

        @Override
        public void toggle() {
            this.isChecked = !this.isChecked;
            highlightCell();
        }
    }
}