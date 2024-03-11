package io.github.wulkanowy.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import java.io.Serializable

// Even though API was introduced in 33, we use 34 as 33 is bugged in some scenarios.

inline fun <reified T : Serializable> Bundle.serializable(key: String): T = when {
    Build.VERSION.SDK_INT >= 34 -> getSerializable(key, T::class.java)!!
    else -> @Suppress("DEPRECATION") getSerializable(key) as T
}

inline fun <reified T : Serializable> Bundle.nullableSerializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 34 -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as T?
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Parcelable> Bundle.parcelableArray(key: String): Array<T>? =
    BundleCompat.getParcelableArray(this, key, T::class.java) as Array<T>?

inline fun <reified T : Serializable> Intent.serializable(key: String): T = when {
    Build.VERSION.SDK_INT >= 34 -> getSerializableExtra(key, T::class.java)!!
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as T
}

inline fun <reified T : Serializable> Intent.nullableSerializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 34 -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as T?
}
