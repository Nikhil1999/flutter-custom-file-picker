package in.lazymanstudios.flutter_custom_file_picker;

import androidx.annotation.NonNull;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class CustomMethodCallHandler implements MethodChannel.MethodCallHandler {
    private CustomFilePicker customFilePicker;

    CustomMethodCallHandler(CustomFilePicker customFilePicker) {
        this.customFilePicker = customFilePicker;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        String methodName = call.method;

        switch (methodName) {
            case "test":
                result.success(true);
                break;
            default:
                handleDefault(result);
                break;
        }
    }

    private void handleDefault(MethodChannel.Result result) {
        result.notImplemented();
    }
}