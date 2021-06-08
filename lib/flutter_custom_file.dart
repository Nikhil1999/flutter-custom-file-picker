class FlutterCustomFile {
  String? _uri, _name;

  FlutterCustomFile.fromMap(Map map) {
    this._uri = map['uri'];
    this._name = map['name'];
  }

  String? get uri => _uri;
  String? get name => _name;
}
