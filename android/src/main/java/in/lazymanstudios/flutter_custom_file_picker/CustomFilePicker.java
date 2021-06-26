package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import androidx.core.content.FileProvider;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

 public class CustomFilePicker implements PluginRegistry.ActivityResultListener {
    private static final int PICK_FILE = 555;

    private Context context;
    private WeakReference<Activity> activityWeakReference;

    private MethodChannel.Result pickFileResult;

    public CustomFilePicker(Context context, Activity activity) {
        this.context = context;
        this.activityWeakReference = new WeakReference<>(activity);
    }

    public void setActivity(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    public void pickFile(MethodChannel.Result result) throws IllegalStateException {
        pickFileResult = result;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        Activity activity = activityWeakReference.get();
        if(activity != null) {
            activity.startActivityForResult(intent, PICK_FILE);
        } else {
            result.success(null);
        }
    }

    public void shareFile(MethodChannel.Result result, String filePath, String title) {
        try {
            Activity activity = activityWeakReference.get();
            if(activity != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(activity, activity.getPackageName(), new File(filePath)));
                shareIntent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(filePath.substring(filePath.lastIndexOf('.') + 1)));
                Intent chooserIntent = Intent.createChooser(shareIntent, title);
                List<ResolveInfo> resInfoList =
                        activity
                                .getPackageManager()
                                .queryIntentActivities(chooserIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity
                            .grantUriPermission(
                                    packageName,
                                    FileProvider.getUriForFile(activity, activity.getPackageName(), new File(filePath)),
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                activity.startActivity(chooserIntent);
                result.success(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FILE) {
            if(resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    String name = getNameFromUri(uri);
                    String size = getSizeFromUri(uri);

                    if(name != null && size != null) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("uri", uri.toString());
                        hashMap.put("name", name);
                        hashMap.put("size", size);

                        pickFileResult.success(hashMap);
                    } else {
                        pickFileResult.success(null);
                    }
                } else {
                    pickFileResult.success(null);
                }
            } else {
                pickFileResult.success(null);
            }
            return true;
        }
        return false;
    }

    private String getNameFromUri(Uri uri) {
        Activity activity = activityWeakReference.get();
        if(activity != null) {
            try (Cursor cursor = activity.getContentResolver()
                    .query(uri, null, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        return null;
    }

    private String getSizeFromUri(Uri uri) {
        Activity activity = activityWeakReference.get();
        if(activity != null) {
            try (Cursor cursor = activity.getContentResolver()
                    .query(uri, null, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (!cursor.isNull(sizeIndex)) {
                        return cursor.getString(sizeIndex);
                    }
                }
            }
        }
        return null;
    }
}
