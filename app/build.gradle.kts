plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.luoyangwei.localclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.luoyangwei.localclient"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    // PhotoView
    // https://github.com/Baseflow/PhotoView
    implementation(libs.photoview.v230)

    // Disk LRU Cache
    // A cache that uses a bounded amount of space on a filesystem. Each cache entry has a string key and a fixed number of values.
    // Each key must match the regex [a-z0-9_-]{1,120}.
    // Values are byte sequences, accessible as streams or files. Each value must be between 0 and Integer.MAX_VALUE bytes in length.
    implementation(libs.disklrucache)

    // EventBus is a publish/subscribe event bus for Android and Java.
    // https://mvnrepository.com/artifact/org.greenrobot/eventbus
    implementation(libs.eventbus)

    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation(libs.commons.io.commons.io)

    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation(libs.apache.commons.lang3)

    // 权限请求框架：https://github.com/getActivity/XXPermissions
    implementation(libs.xxpermissions)

    // https://github.com/bm-x/PhotoView
    implementation(libs.photoview.library)

    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly(libs.lombok)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
    implementation(libs.com.squareup.retrofit2.converter.gson2)

    // https://github.com/bumptech/glide
    implementation(libs.glide)

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation(libs.gson)

    // Room components
    implementation(libs.room.runtime)
    implementation(libs.room.rxjava)
    annotationProcessor(libs.room.compiler)
    androidTestImplementation(libs.room.testing)

    // https://github.com/zetbaitsu/Compressor
    implementation(libs.compressor)

    // 1. Use Guava in your implementation only:
    implementation(libs.guava.v3331jre)

    // 2. Use Guava types in your public API:
    api(libs.google.guava.v3331jre)

    // 3. Android - Use Guava in your implementation only:
    implementation(libs.guava)

    // 4. Android - Use Guava types in your public API:
    api(libs.google.guava)

    implementation(libs.luban)

    // https://mvnrepository.com/artifact/io.reactivex.rxjava3/rxjava
    implementation(libs.rxjava)

    // https://mvnrepository.com/artifact/io.reactivex.rxjava3/rxandroid
    implementation(libs.rxandroid.v300)

    // https://mvnrepository.com/artifact/androidx.work/work-runtime
    implementation(libs.work.runtime)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}