import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.util.*

//const val lifecycleVersion = "2.4.0-beta01"
const val lifecycleVersion = "2.2.0"

private val Project.android get() = extensions.getByName<BaseExtension>("android")

private val flavorRegex = "(assemble|generate)\\w*(Release|Debug)".toRegex()
val Project.currentFlavor
    get() = gradle.startParameter.taskRequests.toString().let { task ->
        flavorRegex.find(task)?.groupValues?.get(2)?.toLowerCase(Locale.ROOT) ?: "debug".also {
            println("Warning: No match found for $task")
        }
    }

fun Project.setupCommon() {
    android.apply {
        buildToolsVersion("31.0.0")
        compileSdkVersion(31)
        defaultConfig {
            minSdk = 23
            targetSdk = 31
        }
        val javaVersion = JavaVersion.VERSION_1_8
        compileOptions {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
        lintOptions {
            warning("ExtraTranslation")
            warning("ImpliedQuantity")
            informational("MissingTranslation")
        }
        (this as ExtensionAware).extensions.getByName<KotlinJvmOptions>("kotlinOptions").jvmTarget =
            javaVersion.toString()
    }
}

fun Project.setupCore() {
    setupCommon()
    android.apply {
        defaultConfig {
            versionCode = 4
            versionName = "1.0.4"
        }
        compileOptions.isCoreLibraryDesugaringEnabled = true
        lintOptions {
            disable("BadConfigurationProvider")
            warning("RestrictedApi")
            disable("UseAppTint")
        }
        ndkVersion = "21.4.7075529"
    }
    dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:1.1.5")
}

fun Project.setupApp() {
    setupCore()

    android.apply {
        lintOptions.disable("RemoveWorkManagerInitializer")
        packagingOptions {
            excludes.add("**/*.kotlin_*")
            jniLibs.useLegacyPackaging = true
        }
        dataBinding {
            isEnabled = true
        }
        buildTypes {
            getByName("debug") {
                isPseudoLocalesEnabled = true
            }
            getByName("release") {
                isShrinkResources = true
                isMinifyEnabled = true
                proguardFile(getDefaultProguardFile("proguard-android.txt"))
            }
        }
    }

    dependencies.add("implementation", project(":core"))
}
