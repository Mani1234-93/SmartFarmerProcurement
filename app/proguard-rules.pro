# Proguard rules for Smart Farmer Procurement
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.Entity *;
    @androidx.room.Dao *;
}
-keep class com.smartfarmer.procurement.data.models.** { *; }
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
