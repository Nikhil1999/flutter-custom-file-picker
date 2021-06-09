package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;

import java.io.InputStream;
import java.util.Arrays;

import io.flutter.plugin.common.EventChannel;

public class CustomEventCallHandler implements EventChannel.StreamHandler {
    private Activity activity;
    private final Uri uri;

    CustomEventCallHandler(Activity activity, Uri uri) {
        this.activity = activity;
        this.uri = uri;
    }

    void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onListen(Object arguments, final EventChannel.EventSink events) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                try {
                    inputStream = activity.getContentResolver().openInputStream(uri);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        if (read != buffer.length) {
                            byte[] buffer_array = Arrays.copyOfRange(buffer, 0, read);
                            events.success(buffer_array);
                        } else {
                            events.success(buffer);
                        }
                    }
                } catch (Exception e) {
                    events.error("101", e.getMessage(), null);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            events.error("101", e.getMessage(), null);
                        }
                    }
                    events.endOfStream();
                }
            }
        });
    }

    @Override
    public void onCancel(Object arguments) {

    }
}
