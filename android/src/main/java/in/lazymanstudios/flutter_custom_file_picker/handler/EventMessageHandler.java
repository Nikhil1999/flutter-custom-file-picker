package in.lazymanstudios.flutter_custom_file_picker.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import in.lazymanstudios.flutter_custom_file_picker.model.EventMessage;

public class EventMessageHandler{
    private static final int MESSAGE_DELAY = 10;

    private static Handler handler;

    private EventMessageHandler() {}

    private static synchronized Handler getHandler() {
        if(handler == null) {
            handler = new Handler(Looper.getMainLooper(), new EventMessageHandlerCallback());
        }
        return handler;
    }

    public static synchronized boolean isAvailable() {
        return !getHandler().hasMessages(EventMessage.TYPE_MESSAGE);
    }

    public static synchronized void sendMessage(EventMessage eventMessage) {
        Message message = Message.obtain();
        message.obj = eventMessage;
        getHandler().sendMessage(message);
        try {
            Thread.sleep(MESSAGE_DELAY);
        } catch (Exception e) {}
    }

    static class EventMessageHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if(message.obj instanceof EventMessage) {
                EventMessage eventMessage = (EventMessage) message.obj;
                eventMessage.emitEvent();
            }
            return false;
        }
    }
}