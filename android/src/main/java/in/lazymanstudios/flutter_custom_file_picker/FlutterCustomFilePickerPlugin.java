package in.lazymanstudios.flutter_custom_file_picker;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/** FlutterCustomFilePickerPlugin */
public class FlutterCustomFilePickerPlugin implements FlutterPlugin, ActivityAware {
  private static final String METHOD_CHANNEL = "in.lazymanstudios.customfilepickerplugin/customfilepicker";
  private CustomFilePicker customFilePicker;
  private MethodChannel customMethodChannel;
  private ActivityPluginBinding activityPluginBinding;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    setUpChannel(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    customMethodChannel.setMethodCallHandler(null);
    customMethodChannel = null;
    customFilePicker = null;
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    activityPluginBinding = binding;
    activityPluginBinding.addActivityResultListener(customFilePicker);
    customFilePicker.setActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivity() {
    activityPluginBinding.removeActivityResultListener(customFilePicker);
    customFilePicker.setActivity(null);
    activityPluginBinding = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NotNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  private void setUpChannel(Context context, BinaryMessenger messenger) {
    customMethodChannel = new MethodChannel(messenger, METHOD_CHANNEL);
    customFilePicker = new CustomFilePicker(context, null);
    CustomMethodCallHandler customMethodCallHandler = new CustomMethodCallHandler(customFilePicker);
    customMethodChannel.setMethodCallHandler(customMethodCallHandler);
  }

  static class CustomMethodCallHandler implements MethodChannel.MethodCallHandler {
    private final CustomFilePicker customFilePicker;

    CustomMethodCallHandler(CustomFilePicker customFilePicker) {
      this.customFilePicker = customFilePicker;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
      String methodName = call.method;

      switch (methodName) {
        case "pickFile":
          customFilePicker.pickFile(result);
          break;
        case "shareFile":
          customFilePicker.shareFile(result, (String) call.argument("filePath"), (String) call.argument("title"));
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
}
