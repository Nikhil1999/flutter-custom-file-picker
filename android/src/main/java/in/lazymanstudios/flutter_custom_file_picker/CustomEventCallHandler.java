package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.util.Arrays;

import androidx.annotation.NonNull;
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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
//                    @Override
//                    public boolean handleMessage(@NonNull Message message) {
//                        new Handler().post(new Runnable() {
//                            @Override
//                            public void run() {
//                                events.success("Hello");
//                            }
//                        });
//                        return true;
//                    }
//                });
//                while (true) {
//                    while (!handler.hasMessages(1)) {
//                        handler.sendMessageDelayed(handler.obtainMessage(1), 10);
//                    }
//                }
//
//            }
//        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message message) {
                        switch (message.what) {
                            case 1: {
                                events.success(message.obj);
                                message.obj = null;
                                break;
                            }
                            case 2: {
                                Exception e = (Exception) message.obj;
                                events.error("101", e.getMessage(), null);
                                break;
                            }
                            case 3: {
                                events.endOfStream();
                            }
                        }
                        return true;
                    }
                });


                InputStream inputStream = null;
                try {
                    inputStream = activity.getContentResolver().openInputStream(uri);

                    byte[] buffer = new byte[65536];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        while (handler.hasMessages(1)) {}
                        if (read != buffer.length) {
                            byte[] buffer_array = Arrays.copyOfRange(buffer, 0, read);
                            handler.sendMessageDelayed(handler.obtainMessage(1, buffer_array.clone()), 20);
//                            handler.obtainMessage(1, buffer_array.clone()).sendToTarget();
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(1, buffer.clone()), 20);
//                            handler.obtainMessage(1, buffer.clone()).sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    handler.obtainMessage(2, e).sendToTarget();
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            handler.obtainMessage(2, e).sendToTarget();
                        }
                    }
                    handler.obtainMessage(3).sendToTarget();
                }
            }
        }).start();
    }

    @Override
    public void onCancel(Object arguments) {

    }
}
