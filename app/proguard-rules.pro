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

# Keep the Farm class from being obfuscated
-keep class com.example.monitoreoacua.business.models.Farm { *; }
-keepnames class com.example.monitoreoacua.business.models.Farm
-keepclassmembers class com.example.monitoreoacua.business.models.Farm { *; }

# Keep Parcelable classes
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep all model classes
-keep class com.example.monitoreoacua.business.models.** { *; }

# Reglas para Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

# Additional rule to fix ClassNotFoundException for Farm
-keepattributes *Annotation*,Signature
-keepclassmembers class com.example.monitoreoacua.business.models.Farm {
    public static final android.os.Parcelable$Creator *;
    public <fields>;
    private <fields>;
}
