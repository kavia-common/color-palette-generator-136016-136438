androidApplication {
    namespace = "org.example.app"

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.cardview:cardview:1.0.0")
        implementation("com.google.android.gms:play-services-base:18.5.0")
        implementation("androidx.test.espresso:espresso-core:3.5.1")
        implementation("androidx.test.ext:junit:1.1.5")
        implementation("androidx.test:runner:1.5.2")
        implementation("androidx.test:rules:1.5.0")
        implementation("junit:junit:4.13.2")
    }
}
