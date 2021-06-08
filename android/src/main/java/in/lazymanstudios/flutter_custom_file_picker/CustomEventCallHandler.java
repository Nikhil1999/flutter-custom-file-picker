package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import io.flutter.plugin.common.EventChannel;

public class CustomEventCallHandler implements EventChannel.StreamHandler {
    private Activity activity;
    private Uri uri;

    CustomEventCallHandler(Activity activity, Uri uri) {
        this.activity = activity;
        this.uri = uri;
    }

    void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        InputStream inputStream = null;
        try {
            inputStream = activity.getContentResolver().openInputStream(uri);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                events.success(buffer);
            }
        } catch (Exception e) {
            events.error("101", e.getMessage(), e.getStackTrace());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    events.error("101", e.getMessage(), e.getStackTrace());
                }
            }
            events.endOfStream();
        }
    }

    @Override
    public void onCancel(Object arguments) {

    }
}
