package com.amusebouche.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amusebouche.activities.R;


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
    private Context mContext = null;

    public LeftMenuAdapter(Context context) {
        this.mContext = context;
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
            case 0:
                return mContext.getResources().getString(R.string.lateral_menu_login);
            case 1:
                return mContext.getResources().getString(R.string.lateral_menu_new_recipes);
            case 2:
                return mContext.getResources().getString(R.string.lateral_menu_downloaded_recipes);
            case 3:
                return mContext.getResources().getString(R.string.lateral_menu_my_recipes);
            case 4:
                return mContext.getResources().getString(R.string.lateral_menu_settings);
            case 5:
                return mContext.getResources().getString(R.string.lateral_menu_info);
        }
    }

    private int getSectionIcon(int position) {
        switch (position) {
            default:
            case 0:
                return R.drawable.ic_user_white_48dp;
            case 1:
                return R.drawable.ic_cook_white_48dp;
            case 2:
                return R.drawable.ic_download_white_48dp;
            case 3:
                return R.drawable.ic_tag_white_48dp;
            case 4:
                return R.drawable.ic_settings_white_48dp;
            case 5:
                return R.drawable.ic_info_white_48dp;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new DrawerView(mContext);
        }

        ((DrawerView) convertView).setTag(getSectionString(position), getSectionIcon(position));

        return convertView;
    }

    public class DrawerView extends RelativeLayout implements Checkable {
        private TextView mTitleTextView;
        private ImageView mImageView;
        private View mColoredView;
        private boolean isChecked;

        public DrawerView(Context context) {
            super(context);
            inflate(context, R.layout.cell_left_menu, this);

            mTitleTextView = (TextView) findViewById(R.id.title);
            mImageView = (ImageView) findViewById(R.id.image);
            mColoredView = findViewById(R.id.line);
        }

        public void setTag(String title, int imageId) {
            mTitleTextView.setText(title);
            mImageView.setImageResource(imageId);
        }

        private void highlightCell(boolean checked) {
            if (checked) {
                mTitleTextView.setTextColor(getResources().getColor(R.color.theme_default_accent));
                mImageView.setColorFilter(getResources().getColor(R.color.theme_default_accent));
                mColoredView.setVisibility(View.VISIBLE);
            } else {
                mTitleTextView.setTextColor(getResources().getColor(R.color.drawer_text_color));
                mImageView.setColorFilter(getResources().getColor(R.color.drawer_text_color));
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
            isChecked = checked;
            highlightCell(checked);

        }

        @Override
        public void toggle() {
            this.isChecked = !this.isChecked;
            highlightCell(isChecked);
        }
    }
}