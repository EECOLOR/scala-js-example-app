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

libraryDependencies += "org.webjars" % "requirejs" % "2.1.8" % "test"

sources in (Test, Keys.test) ~= { srcs =>
  srcs.filter {file =>
    val pattern = """\d+-[^/]+\.js"""
    file.name.matches(pattern) ||
    file.name == "scalajs-corejslib.js" ||
  	file.name == "require.js" || file.name == "bootstrap.js"
  }
}

// And a hack to make sure bootstrap.js is included before jasmine.js
sources in (Test, Keys.test) ~= { srcs =>
  val bootstrap: File = srcs.find(_.name == "bootstrap.js").get
  val requirejs: File = srcs.find(_.name == "require.js").get
  val others = srcs.filterNot(file => file == bootstrap || file == requirejs)
  others :+ bootstrap :+ requirejs
}
