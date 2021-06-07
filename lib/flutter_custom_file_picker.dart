
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterCustomFilePicker {
  static const MethodChannel _methodChannel =
      const MethodChannel('in.lazymanstudios.customfilepickerplugin/customfilepicker');

  static Future<bool> test() async {
    bool result = await _methodChannel.invokeMethod('test');
    return result;
  }
}
