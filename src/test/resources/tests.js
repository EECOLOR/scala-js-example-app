define(function() {
	var reporter = ScalaJS.modules.test_JasmineRhinoReporter();

	reporter.runTests(function() {

		ScalaJS.modules.test_Test();

	});
});