package gemvietnam.com.trafficjam.library;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import gemvietnam.com.trafficjam.MainActivity;

/**
 * Created by Stork on 07/12/2016.
 */

public class TouchableWrapper extends FrameLayout {
    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                MainActivity.mMapIsTouched = true;
                break;

            case MotionEvent.ACTION_UP:
                MainActivity.mMapIsTouched = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
