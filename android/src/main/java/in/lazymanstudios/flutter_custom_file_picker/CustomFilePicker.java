package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import io.flutter.plugin.common.MethodChannel;

public class CustomFilePicker extends Activity {
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
            throw new IllegalStateException("Activity is null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FILE) {
            if(resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    pickFileResult.success(uri.toString());
                } else {
                    pickFileResult.success(null);
                }
            } else {
                pickFileResult.success(null);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
