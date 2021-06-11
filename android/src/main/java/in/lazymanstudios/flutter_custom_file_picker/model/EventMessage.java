package in.lazymanstudios.flutter_custom_file_picker.model;

import io.flutter.plugin.common.EventChannel;

public class EventMessage {
    private static final String ERROR_CODE = "101";

    public static final int TYPE_MESSAGE = 1, TYPE_ERROR = 2, TYPE_END = 3;

    private int type;
    private EventChannel.EventSink eventEmitter;
    private Exception exception;
    private byte[] bytes;

    private EventMessage() {}

    public EventMessage(EventChannel.EventSink eventEmitter, byte[] bytes) {
        type = TYPE_MESSAGE;
        this.eventEmitter = eventEmitter;
        this.bytes = bytes;
    }

    public EventMessage(EventChannel.EventSink eventEmitter, Exception exception) {
        type = TYPE_ERROR;
        this.eventEmitter = eventEmitter;
        this.exception = exception;
    }

    public EventMessage(EventChannel.EventSink eventEmitter) {
        type = TYPE_END;
        this.eventEmitter = eventEmitter;
    }

    public void emitEvent() {
        switch (type) {
            case TYPE_MESSAGE: {
                eventEmitter.success(bytes);
                break;
            }
            case TYPE_ERROR: {
                eventEmitter.error(ERROR_CODE, exception.getMessage(), null);
                break;
            }
            case TYPE_END: {
                eventEmitter.endOfStream();
                break;
            }
        }
    }
}
