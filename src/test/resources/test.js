define([ "jasmine"], function(jasmine) {
	var reporter = ScalaJS.modules.test_JasmineRhinoReporter();
	
	reporter.onStart(function(reporter) {
		try {
			ScalaJS.modules.test_Test();

			var jasmineEnv = jasmine.getEnv();
			jasmineEnv.addReporter(reporter);
			jasmineEnv.updateInterval = 0;
			jasmineEnv.execute();

		} catch (e) {
			console.error("Problem executing code in test: " + e)
			reporter.displayStackTrace(e.stack)
		}
	});
});