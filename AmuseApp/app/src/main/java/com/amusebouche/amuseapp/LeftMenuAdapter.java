package com.amusebouche.amuseapp;

import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


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
    private boolean locked = false;
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
                return mContext.getResources().getString(R.string.LOGIN);
            case 1:
                return mContext.getResources().getString(R.string.NEW_RECIPES);
            case 2:
                return mContext.getResources().getString(R.string.DOWNLOADED_RECIPES);
            case 3:
                return mContext.getResources().getString(R.string.MY_RECIPES);
            case 4:
                return mContext.getResources().getString(R.string.SETTINGS);
            case 5:
                return mContext.getResources().getString(R.string.INFO);
        }
    }

    private int getSectionColor(int position) {
        switch (position) {
            default:
            case 0:
                return mContext.getResources().getColor(android.R.color.holo_red_dark);
            case 1:
                return mContext.getResources().getColor(android.R.color.holo_blue_dark);
            case 2:
                return mContext.getResources().getColor(android.R.color.holo_green_dark);
            case 3:
                return mContext.getResources().getColor(android.R.color.holo_purple);
            case 4:
                return mContext.getResources().getColor(android.R.color.tertiary_text_dark);
            case 5:
                return mContext.getResources().getColor(android.R.color.holo_orange_dark);
        }
    }


    // TODO: Update this!
    private int getSectionIcon(int position) {
        switch (position) {
            default:
            case 0:
                return R.drawable.ic_add_white_48dp;//ic_drawer_placeline;
            case 2:
                return R.drawable.ic_add_white_48dp;//ic_drawer_around;
            case 1:
                if (!locked)
                    return R.drawable.ic_add_white_48dp;//ic_drawer_location;
                else
                    return R.drawable.ic_add_white_48dp;//ic_drawer_pinned;
            case 3:
                return R.drawable.ic_add_white_48dp;//ic_drawer_inspiration;
            case 4:
                return R.drawable.ic_add_white_48dp;//ic_drawer_settings;
            case 5:
                return android.R.drawable.ic_dialog_info;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerHolder drawerHolder;

        if (convertView == null) {
            drawerHolder = new DrawerHolder();
            convertView = new DrawerView(mContext);

            drawerHolder.titleTextView = (TextView) convertView.findViewById(R.id.title);
            drawerHolder.drawer_image = (ImageView) convertView.findViewById(R.id.image_icon);
            drawerHolder.layout = (RelativeLayout) convertView.findViewById(R.id.drawer_relative);

            ((DrawerView) convertView).setTag(getSectionString(position), getSectionIcon(position),
                    getSectionColor(position));
        } else {
            drawerHolder = (DrawerHolder) convertView.getTag();
            ((DrawerView) convertView).setTag(getSectionString(position), getSectionIcon(position),
                    getSectionColor(position));
        }

        if (position == 0) {
            ((DrawerView) convertView).setChecked(true);
        }
        return convertView;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        notifyDataSetChanged();
    }

    public class DrawerHolder {
        TextView titleTextView;
        ImageView drawer_image;
        RelativeLayout layout;
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
            mColoredView = (View) findViewById(R.id.line);
        }

        public void setTag(String title, int imageId, int color) {
/*
            this.titleTextView = tag.titleTextView;
            ImageView drawer_image = tag.drawer_image;

            this.layout = tag.layout;*/
            mTitleTextView.setText(title);
            mImageView.setImageResource(imageId);
            //mImageView.setColorFilter(color);
            //mColoredView.setBackgroundColor(color);
        }

        private void highlighCell(boolean checked) {
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
            highlighCell(checked);

        }

        @Override
        public void toggle() {
            this.isChecked = !this.isChecked;
            highlighCell(isChecked);
        }
    }
}