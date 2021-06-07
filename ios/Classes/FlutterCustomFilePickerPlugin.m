#import "FlutterCustomFilePickerPlugin.h"
#if __has_include(<flutter_custom_file_picker/flutter_custom_file_picker-Swift.h>)
#import <flutter_custom_file_picker/flutter_custom_file_picker-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_custom_file_picker-Swift.h"
#endif

@implementation FlutterCustomFilePickerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterCustomFilePickerPlugin registerWithRegistrar:registrar];
}
@end
