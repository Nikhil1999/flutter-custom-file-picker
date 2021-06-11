package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;
import java.util.UUID;

import in.lazymanstudios.flutter_custom_file_picker.handler.FileEventChannelHandler;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

 public class CustomFilePicker implements PluginRegistry.ActivityResultListener {
    private static final int PICK_FILE = 555;

    private final Dictionary<String, FileEventChannelHandler> fileEventChannelHandlerDictionary = new Hashtable<>();

    private Context context;
    private WeakReference<Activity> activityWeakReference;
    private final BinaryMessenger binaryMessenger;

    private MethodChannel.Result pickFileResult;

    public CustomFilePicker(Context context, Activity activity, BinaryMessenger binaryMessenger) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.binaryMessenger = binaryMessenger;
    }

    public void setActivity(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        Enumeration<String> e = fileEventChannelHandlerDictionary.keys();
        while(e.hasMoreElements()) {
            FileEventChannelHandler fileEventChannelHandler = fileEventChannelHandlerDictionary.get(e.nextElement());
            fileEventChannelHandler.setActivity(activity);
        }
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

    public void readFile(MethodChannel.Result result, String uriString) {
        Uri uri = Uri.parse(uriString);

        Activity activity = activityWeakReference.get();
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
                    String size = getSizeFromUri(uri);
                    String streamID = UUID.randomUUID().toString();

                    if(name != null) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("uri", uri.toString());
                        hashMap.put("name", name);
                        hashMap.put("size", size);
                        hashMap.put("streamID", streamID);

                        FileEventChannelHandler fileEventChannelHandler = new FileEventChannelHandler(activityWeakReference.get(), uri, new EventChannel(binaryMessenger, streamID));
                        fileEventChannelHandlerDictionary.put(streamID, fileEventChannelHandler);

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

    public void clearEventListeners() {
        Enumeration<String> e = fileEventChannelHandlerDictionary.keys();
        while(e.hasMoreElements()) {
            String id = e.nextElement();
            FileEventChannelHandler fileEventChannelHandler = fileEventChannelHandlerDictionary.get(id);
            fileEventChannelHandler.clearListeners();
            fileEventChannelHandlerDictionary.remove(id);
        }
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
        return "Unknown";
    }
}
