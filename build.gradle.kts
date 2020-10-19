import net.minecraftforge.gradle.user.UserExtension

buildscript {
    repositories {
        mavenCentral()
        maven("http://files.minecraftforge.net/maven") {
            name = "forge"
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "sonatype"
        }
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.GTNH2:ForgeGradle:FG_1.2-SNAPSHOT")
    }
}

plugins {
    idea
    java
    signing
}

apply(plugin = "forge")
val gt_version: String by project
version = gt_version
group = "gregtech"

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<UserExtension> {
    version = "1.7.10-10.13.4.1614-1.7.10"
    runDir = "run"
}

repositories {
    mavenLocal()
    maven("http://files.minecraftforge.net/maven") { name = "forge" }
    maven("https://gregtech.overminddl1.com/") { name = "GT6Maven" }
    maven("http://maven.ic2.player.to/") { name = "ic2" }
    maven("http://jenkins.usrv.eu:8081/nexus/content/repositories/releases/") { name = "UsrvDE/GTNH" }
    maven("http://maven.cil.li/") { name = "OpenComputers" }
    maven("http://default.mobiusstrip.eu/maven") { name = "Jabba" }
    maven("http://chickenbones.net/maven/") { name = "CodeChicken" }
    maven("http://www.ryanliptak.com/maven/") { name = "appleCore" }
    ivy {
        name = "gtnh_download_source_stupid_underscore_typo"
        artifactPattern("http://downloads.gtnewhorizons.com/Mods_for_Jenkins/[module]_[revision].[ext]")
    }
    ivy {
        name = "gtnh_download_source"
        artifactPattern("http://downloads.gtnewhorizons.com/Mods_for_Jenkins/[module]-[revision].[ext]")
    }
    ivy {
        name = "BuildCraft"
        artifactPattern("http://www.mod-buildcraft.com/releases/BuildCraft/[revision]/[module]-[revision](-[classifier]).[ext]")
    }
    mavenCentral()
}

dependencies {
    val yamcore_version:String by project
    val tconstruct_version:String by project
    val ae2_version:String by project
    val codechickenlib_version:String by project
    val codechickencore_version:String by project
    val nei_version:String by project
    val translocators_version:String by project
    val ic2_version:String by project
    val forestry_version:String by project
    val applecore_version:String by project
    val enderiocore_version:String by project
    val enderio_version:String by project
    val gc_version:String by project
    val railcraft_version:String by project
    val cofhlib_version:String by project
    val nc_version:String by project
    compileOnly("eu.usrv:YAMCore:1.7.10-${yamcore_version}:deobf")
    compileOnly("tconstruct:TConstruct:1.7.10-${tconstruct_version}:deobf")
    compileOnly ("appeng:appliedenergistics2:${ae2_version}:dev") {
        exclude (module = "*")
    }
    compileOnly ("codechicken:CodeChickenLib:1.7.10-${codechickenlib_version}:dev")
    compileOnly ("codechicken:CodeChickenCore:1.7.10-${codechickencore_version}:dev")
    compileOnly ("codechicken:NotEnoughItems:1.7.10-${nei_version}:dev")
    compileOnly ("codechicken:Translocator:1.7.10-${translocators_version}:dev")
    compile ("net.industrial-craft:industrialcraft-2:${ic2_version}:dev")
    compileOnly ("net.sengir.forestry:forestry_1.7.10:${forestry_version}:dev")
    compileOnly ("applecore:AppleCore:${applecore_version}:api")
    compileOnly ("com.enderio.core:EnderCore:${enderiocore_version}:dev")
    compileOnly ("com.enderio:EnderIO:${enderio_version}:dev") {
        isTransitive = false
    }
    compileOnly (files("libs/Galacticraft-API-1.7-${gc_version}.jar"))
    compileOnly (files("libs/GalacticraftCore-Dev-${gc_version}.jar"))
    compileOnly (group = "", name = "Galacticraft-API", version = gc_version, ext = "jar")
    compileOnly (group = "", name = "GalacticraftCore-Dev", version = gc_version, ext = "jar")
    compileOnly (group = "", name = "CoFHLib", version = cofhlib_version, ext = "jar")
    compileOnly (group = "", name = "Railcraft", version = railcraft_version, ext = "jar")
    compileOnly (group = "", name = "IC2NuclearControl", version = nc_version, ext = "jar")
}
val Project.minecraft: UserExtension
    get() = extensions.getByName<UserExtension>("minecraft")

tasks.withType<Jar> {
    // this will ensure that this task is redone when the versions change.
    this.inputs.properties += "version" to project.version
    this.inputs.properties += "mcversion" to project.minecraft.version
    this.archiveBaseName.set("gregtech")

    // replace stuff in mcmod.info, nothing else
    this.filesMatching("/mcmod.info") {
        this.expand(
                mapOf(
                        "version" to project.version,
                        "mcversion" to project.minecraft.version
                )
        )
    }
    exclude("**/Thumbs.db","speiger/**")
}

val sourceJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

val devJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().output)
    archiveClassifier.set("dev")
}

val apiJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().output)
    include("gregtech/api/**")
    archiveClassifier.set("api")
}

artifacts {
    archives(devJar)
    archives(sourceJar)
    archives(apiJar)
}

signing {
    this.isRequired = false
    useInMemoryPgpKeys(
            findProperty("keyStore") as String?,
            findProperty("keyStorePass") as String?
    )

    sign(configurations.archives.get())
}