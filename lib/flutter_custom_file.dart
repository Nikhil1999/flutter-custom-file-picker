import 'dart:async';

import 'package:flutter/services.dart';

class FlutterCustomFile {
  EventChannel? _eventChannel;
  String? _uri, _name, _streamID, _size;

  FlutterCustomFile.fromMap(Map map) {
    this._uri = map['uri'];
    this._name = map['name'];
    this._streamID = map["streamID"];
    this._size = map["size"];

    _eventChannel = EventChannel(_streamID.toString());
  }

  Stream<List<int>>? openRead() {
    StreamTransformer<dynamic, List<int>> bytesTransformer =
        StreamTransformer.fromHandlers(
      handleData: (data, sink) {
        sink.add(List<int>.from(data));
      },
    );

    return _eventChannel
        ?.receiveBroadcastStream()
        .asBroadcastStream()
        .transform(bytesTransformer)
        .asBroadcastStream();
  }

  String? get uri => _uri;
  String? get name => _name;
  int? get size => int.parse(_size!);
}
