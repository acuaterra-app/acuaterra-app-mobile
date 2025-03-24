plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.monitoreoacua"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.monitoreoacua"
        minSdk = 24
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
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
}

dependencies {
    //implementation("com.squareup.retrofit2:retrofit:2.9.0") //<!-- INSTALAR DEPENDENCIAS-->
    //implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.material) // Asegúrate de que `libs.material` tenga la versión correcta
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    // Evitar versiones duplicadas de Material Design
    implementation ("com.google.android.material:material:1.12.0") // Si quieres usar una versión específica
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

    // GSON y Retrofit deben ser consistentes
    implementation (libs.okhttp)
    implementation ("com.google.code.gson:gson:2.10.1")
}