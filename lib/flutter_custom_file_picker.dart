import 'dart:async';

import 'package:flutter/services.dart';

class FlutterCustomFilePicker {
  static const MethodChannel _channel = const MethodChannel(
      'in.lazymanstudios.customfilepickerplugin/customfilepicker');

  static Future<String> pickFile() async {
    return await _channel.invokeMethod('pickFile');
  }

  static Future<String> readFile(String uri) async {
    return await _channel.invokeMethod('readFile', {"uri": uri});
  }

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
