package com.peersafe.edit_exif;

import java.io.IOException;
import java.util.*; 
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import android.media.ExifInterface;
import android.location.Location;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterExifPlugin */
public class FlutterExifPlugin implements FlutterPlugin, MethodCallHandler {

  private MethodChannel channel;
  /** Plugin registration. */
  private Result result;
  private MethodCall call;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "edit_exif");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    this.call = call;
    this.result = result;
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("setExif")) {
      setExif();
    } else if (call.method.equals("getExif")) {
      getExif();
    }else {
      result.notImplemented();
    }
  }


  public void setExif() {
    String filepath = call.argument("path");
    Map<String,String> map = call.argument("exif");
    try {
      ExifInterface exif = new ExifInterface(filepath); // 根据图片的路径获取图片的Exif
      for(String key:map.keySet()){
        Field staticfield=ExifInterface.class.getDeclaredField(key); 
        exif.setAttribute(staticfield.get(null).toString(), map.get(key)); 
      } 
      // 把纬度写进MODEL
      exif.saveAttributes();
      result.success(null); // 最后保存起来
    } catch (Exception e) {
      result.error("error", "IOexception", e);
      e.printStackTrace();
    }
  }


  public void getExif() {
    String filepath = call.argument("path");
    String key = call.argument("key");
    try {
      ExifInterface exif = new ExifInterface(filepath);
      Field staticfield=ExifInterface.class.getDeclaredField(key); 
      String value = exif.getAttribute(staticfield.get(null).toString());
    } catch (Exception e) {
      result.error("error", "IOexception", null);
      e.printStackTrace();
    }
  }

}
