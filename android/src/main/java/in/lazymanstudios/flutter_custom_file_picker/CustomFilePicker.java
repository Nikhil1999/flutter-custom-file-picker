package in.lazymanstudios.flutter_custom_file_picker;

import android.app.Activity;
import android.content.Context;

public class CustomFilePicker {
    private Context context;
    private Activity activity;

    public CustomFilePicker(Context context, Activity activity) {
        this.activity = activity;
    }

    void setActivity(Activity activity) {
        this.activity = activity;
    }
}
