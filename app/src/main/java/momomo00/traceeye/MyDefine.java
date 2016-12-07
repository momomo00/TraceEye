package momomo00.traceeye;

import android.util.Log;

/**
 * Created by songo_000 on 2016/11/21.
 */
public class MyDefine {
    public static final String LOG_TAG = "LocationAddress";

    public static void showLog(String className, String functionName) {
        Log.d(LOG_TAG, className + ": " + functionName);
    }
}