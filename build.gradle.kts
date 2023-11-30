

buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.0")
        classpath ("com.google.gms:google-services:4.3.8")

    }
    repositories {
        google()
        jcenter()
    }


}



plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false

}
