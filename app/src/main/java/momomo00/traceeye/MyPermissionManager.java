package momomo00.traceeye;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

/**
 * ランタイムパーミッションを管理する
 * Created by songo_000 on 2016/11/21.
 */
public class MyPermissionManager extends PermissionManager {

    // アクティビティ
    private Activity    mActivity;

    // 許可得られた時の動作
    private WhenGrantedListener mWhenGrantedListener;
    // 許可を得られず「二度と表示しない」を押された場合の動作
    private NotGetPermissionListener mNotGetPermissionListener;

    /**
     * コンストラクタ
     * @param activity アクティビティ
     */
    public MyPermissionManager(Activity activity) {
        super(activity);
        mActivity = activity;
        mWhenGrantedListener = null;
        mNotGetPermissionListener = null;

        setNotGetPermissionListener(new NotGetPermissionListener() {
            @Override
            public void notGetPermission() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                intent.setData(uri);
                mActivity.startActivity(intent);
            }
        });
    }

    /**
     * すべてのパーミッションが満たされた場合
     */
    @Override
    void whenAllGranted() {
        executionWhenGranted();
    }

    /**
     * 2回目以降の要求メッセージ
     */
    @Override
    void afterSecondMessage() {
        new AlertDialog.Builder(mActivity)
                .setTitle("パーミッションの追加説明")
                .setMessage("このアプリは位置情報を必要とします")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }

    /**
     * 1つでも拒否が選択された場合
     */
    @Override
    void onSelectRequestDenied() {
        new AlertDialog.Builder(mActivity)
                .setTitle("パーミッション取得エラー")
                .setMessage("再試行する場合は、再度Requestボタンを押してください")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }

    /**
     * 1つでも「今後表示しない」が選択された場合
     */
    @Override
    void onSelectDoNotDisplayAgain() {
        new AlertDialog.Builder(mActivity)
                .setTitle("パーミッション取得エラー")
                .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        executionNotGetPermission();
                    }
                })
                .create()
                .show();
    }

    /**
     * パーミッションがすべて許可された場合に実行する処理のインターフェース
     */
    public interface WhenGrantedListener {
        void whenGranted();
    }

    /**
     * パーミッションがすべて許可された場合に実行する処理のインターフェースを登録する
     *
     * @param listener リスナー
     * @return 自分
     */
    public PermissionManager setWhenGrantedListener(WhenGrantedListener listener) {
        mWhenGrantedListener = listener;
        return this;
    }

    /**
     * パーミッションがすべて許可された場合に実行する処理
     */
    private void executionWhenGranted() {
        if(mWhenGrantedListener == null) {
            return;
        }
        mWhenGrantedListener.whenGranted();
    }

    /**
     * 「今後表示しない」を設定された場合に実行するインターフェース
     */
    public interface NotGetPermissionListener {
        void notGetPermission();
    }

    /**
     * 「今後表示しない」を設定された場合に実行するインターフェースを登録する
     * @param listener リスナー
     * @return 自分
     */
    public PermissionManager setNotGetPermissionListener(NotGetPermissionListener listener) {
        mNotGetPermissionListener = listener;
        return this;
    }

    /**
     * 「今後表示しない」を設定された場合に実行する処理
     */
    private void executionNotGetPermission() {
        if(mNotGetPermissionListener == null) {
            return;
        }
        mNotGetPermissionListener.notGetPermission();
    }

}