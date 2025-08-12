androidApplication {
    namespace = "com.weatherforecast.app"

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.viewpager2:viewpager2:1.0.0")
        implementation("com.google.android.material:material:1.11.0")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation("com.google.code.gson:gson:2.10.1")
    }
}
