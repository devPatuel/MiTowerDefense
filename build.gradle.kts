plugins {
    id("java")
}

group = "com.germangascon.gametemplate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Driver de MongoDB
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
}

tasks.test {
    useJUnitPlatform()
}