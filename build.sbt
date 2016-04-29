import sbt.Package.ManifestAttributes

name := "sfdc-profile-updater"

version := "0.1"

scalaVersion := "2.11.7"

// add scala-xml dependency when needed (for Scala 2.11 and newer) in a robust way
// this mechanism supports cross-version publishing
// taken from: http://github.com/scala/scala-module-dependency-sample
libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    // if scala 2.11+ is used, add dependency on scala-xml module
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value ++ Seq(
        "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
        "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3",
        "org.scala-lang.modules" %% "scala-swing" % "1.0.1")
    case _ =>
      // or just libraryDependencies.value if you don't depend on scala-swing
      libraryDependencies.value :+ "org.scala-lang" % "scala-swing" % scalaVersion.value
  }
}

libraryDependencies += "com.github.scopt" %% "scopt" % "3.4.0"

proguardSettings

ProguardKeys.proguardVersion in Proguard := "5.2.1"

ProguardKeys.options in Proguard := Seq(
"-injars <user.dir>/target/scala-2.11/sfdc-profile-updater_2.11-0.1.jar",
"-injars <user.home>/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.7.jar",
"-libraryjars <java.home>/lib/rt.jar",
"-outjars <user.dir>/target/scala-2.11/proguard/sfdc-profile-updater-2.11-0.1.jar",
"-dontnote", 
"-dontwarn", 
"-ignorewarnings", 
"-dontoptimize")

ProguardKeys.options in Proguard += ProguardOptions.keepMain("main.scala.ProfileUpdater.ProfileUpdater")

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

packageOptions in packageBin := Seq(
ManifestAttributes(
 ("Main-Class", "main.scala.ProfileUpdater.ProfileUpdater"), 
 ("Built-By","alek.wojdyga@gmail.com")
)
)


resolvers += Resolver.sonatypeRepo("public")
