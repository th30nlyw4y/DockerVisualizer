plugins {
    id("java")
}

group = "com.th30nlyw4y"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.docker-java:docker-java-core:3.3.6")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.6")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-swing:3.17.1")
    testImplementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
}

tasks.test {
    useJUnitPlatform()
}