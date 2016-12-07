package momomo00.traceeye;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ランタイムパーミッションを管理する抽象クラス
 * Created by songo_000 on 2016/11/11.
 */
public abstract class PermissionManager {

    // パーミッションの要求番号
    private final int REQUEST_CODE_LOCATION_PERMISSION = 1000;

    // アクティビティ
    private Activity    mActivity;

    /**
     * コンストラクタ
     */
    public PermissionManager(Activity activity) {
        MyDefine.showLog("PermissionManager", "PermissionManager()");
        mActivity = activity;
    }

    /**
     * パーミッションの許可有無を確認
     *
     * @param permission パーミッション
     * @return 許可されている場合はtrue
     */
    public boolean checkPermission(String permission) {
        MyDefine.showLog("PermissionManager", "checkPermission(String)");
        return checkPermission(mActivity, permission);
    }

    /**
     * パーミッションの許可有無を確認
     *
     * @param context コンテキスト
     * @param permission パーミッション
     * @return 許可されている場合はtrue
     */
    public static boolean checkPermission(Context context, String permission) {
        MyDefine.showLog("PermissionManager", "checkPermission(Context, String)");

        boolean result = true;

        if(ActivityCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
            result = false;
        }

        return result;
    }

    /**
     * パーミッションの許可有無を確認した上で許可されていないパーミッションを要求する
     *
     * @param permissions 必要なパーミッション
     */
    public void requestPermissions(String[] permissions) {
        MyDefine.showLog("PermissionManager", "requestPermissions(String)");

        List<String> deniedPermissionList = new ArrayList<>();
        boolean requestPermissionsResult = true;

        // パーミッションの許可有無の確認
        for(String permission: permissions) {
            boolean checkPermissionResult = checkPermission(permission);
            if(!checkPermissionResult) {
                deniedPermissionList.add(permission);
                requestPermissionsResult = false;
            }
        }

        // すべて許可されていた場合は登録されている処理を実行
        if(requestPermissionsResult) {
            whenAllGranted();
            return;
        }

        // パーミッションの要求
        String[] deniedPermissions = deniedPermissionList.toArray(new String[0]);
        for(String deniedPermission: deniedPermissions) {
            boolean rationaleResult = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, deniedPermission);
            if(rationaleResult) {
                afterSecondMessage();
            }
        }

        ActivityCompat.requestPermissions(mActivity
                , deniedPermissions
                , REQUEST_CODE_LOCATION_PERMISSION);
    }

    /**
     * パーミッションの要求結果
     *
     * @param requestCode リクエストコード
     * @param permissions 要求したパーミッション
     * @param grantResults 要求結果
     * @return 処理すべきリクエストコードの場合true
     */
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MyDefine.showLog("PermissionManager", "onRequestPermissionsResult(int, String[], int[]");
        boolean result = true;

        switch(requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                // 要求したパーミッションすべてが許可された場合は登録されている処理を実行
                if(checkGrantResults(grantResults)) {
                    whenAllGranted();
                    return true;
                }

                // 要求したパーミッションの内、一つでも「今後表示しない」を選択された場合
                if(!checkRequestPermissionsResult(permissions)) {
                    onSelectDoNotDisplayAgain();
                }
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    /**
     * 要求したパーミッションの許可有無を確認
     *
     * @param grantResults 要求結果
     * @return 要求したパーミッションが全て許可されている場合はtrue
     */
    private boolean checkGrantResults(int[] grantResults) {
        MyDefine.showLog("PermissionManager", "checkGrantResults(int[])");

        boolean result = true;
        for(int grantResult: grantResults) {
            if(grantResult != PackageManager.PERMISSION_GRANTED) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 要求したパーミッションに許可されなかったものが一つでもある場合の処理
     *
     * @param permissions 要求したパーミッション
     * @return 一つでも要求したパーミッションで「今後表示しない」を選択されていた場合false
     */
    private boolean checkRequestPermissionsResult(String[] permissions) {
        MyDefine.showLog("PermissionManager", "checkRequestPermissionsResult(String[])");
        boolean result = true;

        for(String permission: permissions) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                result = false;
                break;
            }
        }

        if(result) {
            onSelectRequestDenied();
        }

        return result;
    }

    /**
     * すべてのパーミッションが満たされた場合
     */
    abstract  void whenAllGranted();

    /**
     * 2回目以降の要求メッセージ
     */
    abstract void afterSecondMessage();

    /**
     * 1つでも拒否が選択された場合
     */
    abstract void onSelectRequestDenied();

    /**
     * 1つでも「今後表示しない」が選択された場合
     */
    abstract void onSelectDoNotDisplayAgain();
}