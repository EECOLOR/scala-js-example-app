var testsFailed = false

requirejs.onError = function(e) {
	if (e === ScalaJS.modules.test_JasmineTestFailed()) {
		testsFailed = true
	} else {
		throw e
	}
}

requirejs({
	deps : [ "tests" ],
	baseUrl : "",
	shim : {
		"jasmine" : {
			exports : "jasmine"
		}
	},
	callback : function() {
		// Make sure the runtime is stopped with an error, otherwise 
		// the run is marked a success. We should add a catch for some 
		// exception in the runner. That would allow us to exit with a 
		// failure and not give those stack trace methods.
		//
		// Specs2 for example ends with:
		//   [error] Total time: 27 s, completed Dec 2, 2013 3:12:30 PM
		if (testsFailed)
			throw "tests_failed"
	}
});