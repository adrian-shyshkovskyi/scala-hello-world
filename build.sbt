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
    releaseTagName := (version in ThisBuild).value,
    releaseTagComment := s"Release ${(version in ThisBuild).value} from build ${sys.env.getOrElse("TRAVIS_BUILD_ID", "None")}",
    releaseNextVersion := {
      ver => Version(sys.props.getOrElse("currentVersion", ver))
        .map(_.bump(releaseVersionBump.value).string).getOrElse(sbtrelease.versionFormatError(ver))
    }
  )

pomExtra :=
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>

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