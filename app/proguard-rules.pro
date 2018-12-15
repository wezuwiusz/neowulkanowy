# Optimizations
-optimizationpasses 5
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-allowaccessmodification
-repackageclasses ''
-verbose


#Config for anallitycs
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keep class com.crashlytics.** {*;}
-keep public class * extends java.lang.Exception
-dontwarn com.crashlytics.**


#Config for OkHttp
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn javax.annotation.**


#Config for ReactiveNetwork
-dontwarn com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
-dontwarn io.reactivex.functions.Function
-dontwarn rx.internal.util.**
-dontwarn sun.misc.Unsafe

#Confi for API
-keep class io.github.wulkanowy.api.** {*;}
