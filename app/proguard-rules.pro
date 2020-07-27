# Optimizations
-optimizationpasses 5
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-dontobfuscate
-allowaccessmodification
-repackageclasses ''
-verbose


#Keep all wulkanowy files
-keep class io.github.wulkanowy.** {*;}


#Config for anallitycs
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** {*;}
-keep public class * extends java.lang.Exception
-dontwarn com.crashlytics.**


#Config for OkHttp
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn javax.annotation.**


#Config for MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }


#Config for Material Components
-keep class com.google.android.material.tabs.** { *; }


#Config for About Libraries
-keep class .R
-keep class **.R$* {
    <fields>;
}
