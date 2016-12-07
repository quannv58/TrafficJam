package gemvietnam.com.trafficjam.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import gemvietnam.com.trafficjam.R;

/**
 * Created by Stork on 06/12/2016.
 */

public class GridViewAdapter extends BaseAdapter {
    private Activity mContext;

    public GridViewAdapter(Activity context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mContext.getLayoutInflater().inflate(R.layout.item_gridview, parent, false);
        }
        TextView mTextView = (TextView) convertView.findViewById(R.id.item_gridview_tv);
        convertView.setBackgroundResource(mThumbIds[position]);
        int screenHeight = Util.getScreenSize(mContext).y;
        int cellHeight = screenHeight / 20;
        convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, cellHeight));
        return convertView;
    }

    private int[] mThumbIds = {
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1,
            R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1, R.color.color_5, R.color.color_4, R.color.color_3, R.color.color_2, R.color.color_1
    };
}
