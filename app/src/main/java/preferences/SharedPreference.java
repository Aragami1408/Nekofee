package preferences;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by TINH HUYNH on 5/28/2017.
 */

public class SharedPreference {

    private static final String KEY_RADIUS = "radiusKey";

    public static void saveRadius(Context context , int radius){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(KEY_RADIUS, radius)
                .apply();
    }

    public static int getRadius(Context context){
        int radius = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_RADIUS, 0);
        return radius;
    }
}
