package in.lazymanstudios.flutter_custom_file_picker.handler;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import in.lazymanstudios.flutter_custom_file_picker.model.EventMessage;
import io.flutter.plugin.common.EventChannel;

public class FileEventChannelHandler {
    private EventChannel eventChannel;
    private FileEventChannelStreamHandler streamHandler;

    public FileEventChannelHandler(Activity activity, Uri uri, EventChannel eventChannel) {
        this.eventChannel = eventChannel;
        this.eventChannel.setStreamHandler(new FileEventChannelStreamHandler(activity, uri));
    }

    public void setActivity(Activity activity) {
        streamHandler.setActivity(activity);
    }

    public void clearListeners() {
        streamHandler.clearListener();
        eventChannel.setStreamHandler(null);
    }

    private static class FileEventChannelStreamHandler implements EventChannel.StreamHandler {
        private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

        private WeakReference<Activity> activityWeakReference;
        private Uri uri;

        Dictionary<String, Future<?>> futureDictionary = new Hashtable<>();

        FileEventChannelStreamHandler(Activity activity, Uri uri) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.uri= uri;
        }

        public void setActivity(Activity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public void clearListener() {
            Enumeration<String> e = futureDictionary.keys();
            while(e.hasMoreElements()) {
                Future<?> future = futureDictionary.get(e.nextElement());
                if(future != null) {
                    future.cancel(true);
                }
            }
        }

        @Override
        public void onListen(Object arguments, final EventChannel.EventSink events) {
            final String uuid = (String) arguments;

            Future<?> future = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Log.d("TEST", uuid + " Started");
                    InputStream inputStream = null;
                    try {
                        inputStream = activityWeakReference.get().getContentResolver().openInputStream(uri);

                        byte[] buffer = new byte[65536];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            while (!EventMessageHandler.isAvailable()) {}
                            if (read != buffer.length) {
                                byte[] buffer_array = Arrays.copyOfRange(buffer, 0, read);
                                EventMessageHandler.sendMessage(new EventMessage(events, buffer_array.clone()));
                            } else {
                                EventMessageHandler.sendMessage(new EventMessage(events, buffer.clone()));
                            }
                        }
                    } catch (Exception e) {
                        EventMessageHandler.sendMessage(new EventMessage(events, e));
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Exception e) {
                                EventMessageHandler.sendMessage(new EventMessage(events, e));
                            }
                        }

                        Log.d("TEST", uuid + " Completed");
                        EventMessageHandler.sendMessage(new EventMessage(events));
                        futureDictionary.remove(uuid);
                    }
                }
            });

            futureDictionary.put(uuid, future);
        }

        @Override
        public void onCancel(Object arguments) {
            String uuid = (String) arguments;
            Log.d("TEST", uuid + " Cancelled");
            Future<?> future = futureDictionary.get(uuid);
            if (future != null) {
                future.cancel(true);
                futureDictionary.remove(uuid);
            }
        }
    }
}
