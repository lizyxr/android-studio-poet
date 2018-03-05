package com.google.androidstudiopoet.generators.android_modules

import com.google.androidstudiopoet.input.AndroidBuildGradleBlueprint
import com.google.androidstudiopoet.models.BuildType
import com.google.androidstudiopoet.models.Flavor
import com.google.androidstudiopoet.models.LibraryDependency
import com.google.androidstudiopoet.testutils.mock
import com.google.androidstudiopoet.writers.FileWriter
import com.nhaarman.mockito_kotlin.*
import org.junit.Test

class AndroidModuleBuildGradleGeneratorTest {
    private val fileWriter: FileWriter = mock()
    private val androidModuleBuildGradleGenerator = AndroidModuleBuildGradleGenerator(fileWriter)

    @Test
    fun `generator applies plugins from the blueprint`() {
        val blueprint = getAndroidBuildGradleBlueprint(plugins = setOf("plugin1", "plugin2"))
        androidModuleBuildGradleGenerator.generate(blueprint)
        val expected = """apply plugin: 'plugin1'
apply plugin: 'plugin2'
android {
    compileSdkVersion 0
    defaultConfig {
        minSdkVersion 0
        targetSdkVersion 0
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}"""
        verify(fileWriter).writeToFile(expected, "path")
    }

    @Test
    fun `generator applies compileSdkVersion from the blueprint`() {
        val blueprint = getAndroidBuildGradleBlueprint(compileSdkVersion = 27)
        androidModuleBuildGradleGenerator.generate(blueprint)
        val expected = """android {
    compileSdkVersion 27
    defaultConfig {
        minSdkVersion 0
        targetSdkVersion 0
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}"""
        verify(fileWriter).writeToFile(expected, "path")
    }

    @Test
    fun `generator applies minSdkVersion from the blueprint`() {
        val blueprint = getAndroidBuildGradleBlueprint(minSdkVersion = 27)
        androidModuleBuildGradleGenerator.generate(blueprint)
        val expected = """android {
    compileSdkVersion 0
    defaultConfig {
        minSdkVersion 27
        targetSdkVersion 0
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}"""
        verify(fileWriter).writeToFile(expected, "path")
    }

    @Test
    fun `generator applies targetSdkVersion from the blueprint`() {
        val blueprint = getAndroidBuildGradleBlueprint(targetSdkVersion = 27)
        androidModuleBuildGradleGenerator.generate(blueprint)
        val expected = """android {
    compileSdkVersion 0
    defaultConfig {
        minSdkVersion 0
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}"""
        verify(fileWriter).writeToFile(expected, "path")
    }

    @Test
    fun `generator applies libraries from the blueprint`() {
        val blueprint = getAndroidBuildGradleBlueprint(libraries = setOf(
                LibraryDependency("implementation", "library1"),
                LibraryDependency("testApi", "library2"),
                LibraryDependency("kapt", "library3")
        ))
        androidModuleBuildGradleGenerator.generate(blueprint)
        val expected = """android {
    compileSdkVersion 0
    defaultConfig {
        minSdkVersion 0
        targetSdkVersion 0
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "library1"
    testApi "library2"
    kapt "library3"
}"""
        verify(fileWriter).writeToFile(expected, "path")
    }

    @Test
    fun `generator applies flavors and dimensions from the blueprint`() {
        val blueprint = getAndroidBuildGradleBlueprint(
                flavorDimensions = setOf("dim1", "dim2", "dim3"),
                productFlavors = setOf(Flavor("flav1", "dim1"), Flavor("flav2")))
        androidModuleBuildGradleGenerator.generate(blueprint)
        val expected = """android {
    compileSdkVersion 0
    defaultConfig {
        minSdkVersion 0
        targetSdkVersion 0
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
    flavorDimensions "dim1", "dim2", "dim3"
    productFlavors {
        flav1 {
            dimension "dim1"
        }
        flav2 {

        }
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}"""
        verify(fileWriter).writeToFile(expected, "path")
    }

    @Test
    fun `generator applies buildTypes from the blueprint`() {
        val blueprint = getAndroidBuildGradleBlueprint(buildTypes = setOf(
                BuildType("buildType1"),
                BuildType("buildType2", "line1\nline2")
        ))
        androidModuleBuildGradleGenerator.generate(blueprint)
        val expected = """android {
    compileSdkVersion 0
    defaultConfig {
        minSdkVersion 0
        targetSdkVersion 0
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
        buildType1 {

        }
        buildType2 {
            line1
            line2
        }
    }
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}"""
        verify(fileWriter).writeToFile(expected, "path")
    }

    private fun getAndroidBuildGradleBlueprint(
            plugins: Set<String> = setOf(),
            compileSdkVersion: Int  = 0,
            minSdkVersion: Int = 0,
            targetSdkVersion: Int = 0,
            libraries: Set<LibraryDependency> = setOf(),
            flavorDimensions: Set<String> = setOf(),
            productFlavors: Set<Flavor> = setOf(),
            buildTypes: Set<BuildType> = setOf()
    ): AndroidBuildGradleBlueprint {
        val blueprint = mock<AndroidBuildGradleBlueprint>()
        whenever(blueprint.plugins).thenReturn(plugins)
        whenever(blueprint.compileSdkVersion).thenReturn(compileSdkVersion)
        whenever(blueprint.minSdkVersion).thenReturn(minSdkVersion)
        whenever(blueprint.targetSdkVersion).thenReturn(targetSdkVersion)
        whenever(blueprint.libraries).thenReturn(libraries)
        whenever(blueprint.flavorDimensions).thenReturn(flavorDimensions)
        whenever(blueprint.productFlavors).thenReturn(productFlavors)
        whenever(blueprint.buildTypes).thenReturn(buildTypes)
        whenever(blueprint.path).thenReturn("path")
        return blueprint
    }
}