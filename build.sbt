import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease.Version

name := "scala-hello-world"
organization := "com.shyshkov"

lazy val root = (project in file("."))
  .settings(
    scalaVersion in ThisBuild := "2.12.11",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.1.1" % Test,
      "org.scalamock" %% "scalamock" % "4.4.0" % Test,
      "org.mockito" %% "mockito-scala" % "1.11.3" % Test
    ),

    homepage := Some(url("https://github.com/adrian-shyshkovskyi/scala-hello-world")),

    licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),

    scmInfo := Some(ScmInfo(
      url("https://github.com/adrian-shyshkovskyi/scala-hello-world"),
      "scm:git:git@github.com/adrian-shyshkovskyi/scala-hello-world.git",
      Some("scm:git:git@github.com/adrian-shyshkovskyi/scala-hello-world.git"))),

    publishMavenStyle := true,

    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    pomExtra :=
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
          <distribution>repo</distribution>
        </license>
      </licenses>,

    developers := List(
      Developer("adrian-shyshkovskyi", "Adrian Shyshkovskyi", "adrian.shyshkovskyi@gmail.com", url("https://github.com/adrian-shyshkovskyi"))
    ),
    pomIncludeRepository := { _ => false },
    useGpg := true,

    releaseTagName := (version in ThisBuild).value,
    releaseTagComment := s"Release ${(version in ThisBuild).value} from build ${sys.env.getOrElse("TRAVIS_BUILD_ID", "None")}",
    releaseNextVersion := {
      ver => Version(sys.props.getOrElse("currentVersion", ver))
        .map(_.bump(releaseVersionBump.value).string).getOrElse(sbtrelease.versionFormatError(ver))
    }
  )

// Defines the release process
releaseIgnoreUntrackedFiles := true

commands += Command.command("prepareRelease")((state: State) => {
  println("Preparing release...")
  val extracted = Project extract state
  val customState = extracted.appendWithoutSession(Seq(releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    setNextVersion,
    runClean,
    runTest,
    tagRelease
  )), state)
  Command.process("release with-defaults", customState)
})