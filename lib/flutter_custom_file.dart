class FlutterCustomFile {
  String? _uri, _name, _size;

  FlutterCustomFile.fromMap(Map map) {
    this._uri = map['uri'];
    this._name = map['name'];
    this._size = map["size"];
  }

  String? get uri => _uri;
  String? get name => _name;
  int? get size => int.parse(_size!);
}
