import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_custom_file_picker/flutter_custom_file.dart';

class FlutterCustomFilePicker {
  static const MethodChannel _channel = const MethodChannel(
      'in.lazymanstudios.customfilepickerplugin/customfilepicker');

  static Future<FlutterCustomFile?> pickFile() async {
    Map? map = await _channel.invokeMethod('pickFile');
    if (map != null) {
      return FlutterCustomFile.fromMap(map);
    } else {
      return null;
    }
  }

  static Future<void> shareFile(String filePath, String title) async {
    return await _channel
        .invokeMethod('shareFile', {"filePath": filePath, "title": title});
  }

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
