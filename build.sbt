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

// And a hack to make sure things are loaded in order
sources in (Test, Keys.test) ~= { srcs =>
	val scalaJsFiles = 
	  srcs.filter { file =>
	    val scalaJsFiles = """\d+-[^/]+\.js"""
	    file.name.matches(scalaJsFiles) ||
	    file.name == "scalajs-corejslib.js"
	  }
  val bootstrap: File = srcs.find(_.name == "bootstrap.js").get
  val requirejs: File = srcs.find(_.name == "require.js").get
  val main: File = srcs.find(_.name == "main.js").get
  scalaJsFiles :+ bootstrap :+ requirejs :+ main
}
