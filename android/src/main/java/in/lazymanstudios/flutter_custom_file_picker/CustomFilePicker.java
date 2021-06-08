package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class CustomFilePicker implements PluginRegistry.ActivityResultListener {
    private static final int PICK_FILE = 555;

    private Context context;
    private Activity activity;

    private MethodChannel.Result pickFileResult;

    public CustomFilePicker(Context context, Activity activity) {
        this.activity = activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void pickFile(MethodChannel.Result result) throws IllegalStateException {
        pickFileResult = result;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        if(activity != null) {
            activity.startActivityForResult(intent, PICK_FILE);
        } else {
            result.success(null);
        }
    }

    public void readFile(MethodChannel.Result result, String uriString) {
        Uri uri = Uri.parse(uriString);
        if(activity != null) {
            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream =
                         activity.getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result.success(stringBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FILE) {
            if(resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    String name = getNameFromUri(uri);
                    
                    if(name != null) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("uri", uri.toString());
                        hashMap.put("name", name);
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
        try (Cursor cursor = activity.getContentResolver()
                .query(uri, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return null;
    }
}
