package com.amusebouche.amuseapp;

import android.content.Context;
import android.content.res.Resources;
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
    private List<String> sectionsList;
    private boolean locked = false;
    private Context mContext = null;

    public LeftMenuAdapter(Context context) {
        this.mContext = context;
        String[] sections = {"RECIPES", "MY_RECIPES", "MY_FAVOURITES", "SETTINGS", "INFO"};
        sectionsList = Arrays.asList(sections);
    }

    @Override
    public int getCount() {
        return sectionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return sectionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private String getSectionString(int position) {
        switch (position) {
            default:
            case 0:
                return mContext.getResources().getString(R.string.RECIPES);
            case 1:
                return mContext.getResources().getString(R.string.MY_RECIPES);
            case 2:
                return mContext.getResources().getString(R.string.MY_FAVOURITES);
            case 3:
                return mContext.getResources().getString(R.string.SETTINGS);
            case 4:
                return mContext.getResources().getString(R.string.INFO);
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
                return R.drawable.ic_add_white_48dp;//ic_drawer_info;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerHolder drawerHolder;
        if (convertView == null) {
            drawerHolder = new DrawerHolder();
            convertView = new DrawerView(mContext);
            drawerHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            drawerHolder.drawer_image = (ImageView) convertView.findViewById(R.id.drawer_image);
            drawerHolder.layout = (RelativeLayout) convertView.findViewById(R.id.drawer_relative);
            ((DrawerView) convertView).setTag(drawerHolder, sectionsList.get(position),
                getSectionIcon(position));
        } else {
            drawerHolder = (DrawerHolder) convertView.getTag();
            ((DrawerView) convertView).setTag(drawerHolder, sectionsList.get(position),
                getSectionIcon(position));
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
        private TextView titleTextView;
        private RelativeLayout layout;
        private boolean isChecked;

        public DrawerView(Context context) {
            super(context);
            inflate(context, R.layout.cell_left_menu, this);
        }

        public void setTag(DrawerHolder tag, String title, int imageId) {
            super.setTag(tag);
            this.titleTextView = tag.titleTextView;
            ImageView drawer_image = tag.drawer_image;
            this.layout = tag.layout;
            titleTextView.setText(title);
            Context mContext = getContext();
            if (mContext != null) {
                drawer_image.setImageResource(imageId);
            }
        }

        private void highlighCell(boolean checked) {
            Resources resources = getResources();
            if (resources != null) {
                // TODO: Change color.
                if (checked) {
                    layout.setBackgroundColor(resources.getColor(R.color.theme_default_accent));
                } else {
                    layout.setBackgroundColor(resources.getColor(android.R.color.white));
                }
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