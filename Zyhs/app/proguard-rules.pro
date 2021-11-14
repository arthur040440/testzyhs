# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# glide 的混淆代码
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# banner 的混淆代码
-keep class com.youth.banner.** {
    *;
 }
 #--------------- BEGIN: okhttp ----------
 -keepattributes Signature
 -keepattributes *Annotation*
 -keep class com.squareup.okhttp.** { *; }
 -keep interface com.squareup.okhttp.** { *; }
 -dontwarn com.squareup.okhttp.**
 #--------------- END: okhttp ----------
 #--------------- BEGIN: okio ----------
 -keep class sun.misc.Unsafe { *; }
 -dontwarn java.nio.file.*
 -dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 -dontwarn okio.**
 #--------------- END: okio
 #---------------避免混淆Bugly BEGIN: bugly
 -dontwarn com.tencent.bugly.**
 -keep public class com.tencent.bugly.**{*;}
 #---------------避免混淆Bugly END: bugly
  #--------------百度语音合成BEGIN
 -keep class com.baidu.tts.**{*;}
 -keep class com.baidu.speechsynthesizer.**{*;}
  #--------------百度语音合成END



