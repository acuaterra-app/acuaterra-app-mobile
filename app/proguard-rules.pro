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

# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep Parcelable implementations for all model classes
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Keep the Farm class and all its fields
-keep class com.example.monitoreoacua.business.models.Farm { *; }

# Keep all model classes
-keep class com.example.monitoreoacua.business.models.** { *; }

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep all GSON annotations
-keepattributes *Annotation*
-keepattributes Signature
-keep class com.google.gson.** { *; }

# Keep Retrofit service interfaces
-keep class com.example.monitoreoacua.service.** { *; }

# Keep any classes used in bundles
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
