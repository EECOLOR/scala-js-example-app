// Turn this project into a Scala.js project by importing these settings
scalaJSSettings

name := "Example"

version := "0.1-SNAPSHOT"

// Specify additional .js file to be passed to package-js and optimize-js
unmanagedSources in (Compile, ScalaJSKeys.packageJS) +=
    baseDirectory.value / "js" / "startup.js"

libraryDependencies += "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.1-SNAPSHOT"
 
libraryDependencies += "org.scala-lang.modules.scalajs" %% "scalajs-jquery" % "0.1-SNAPSHOT"

libraryDependencies += "org.webjars" % "jasmine" % "1.3.1" % "test"

sources in (Test, Keys.test) ~= { srcs =>
  srcs.filterNot(_.name == "jasmine-html.js")
}

// And a hack to make sure bootstrap.js is included before jasmine.js
sources in (Test, Keys.test) ~= { srcs =>
  val bootstrap: File = srcs.find(_.name == "bootstrap.js").get
  val main: File = srcs.find(_.name == "main.js").get
  val (before, after) =
    srcs.filterNot(file => file == bootstrap || file == main).span(_.name != "jasmine.js")
  before ++ Seq(bootstrap) ++ after ++ Seq(main)
}
