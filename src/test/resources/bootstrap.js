/**
 * This file is loaded first and used for patching the JS environment.
 */

function scalaJSStub(name) {
	return function() {
		console.log("Stub for " + name + " called with arguments:");
		console.log(arguments[0] + "," + arguments[1] + "," + arguments[2]
				+ "," + arguments[3]);
	}
};

/* Stub-out timer methods used by Jasmine and not provided by Rhino. */
if (typeof setTimeout == 'undefined') {
	var setTimeout = scalaJSStub('setTimeout');
	var clearTimeout = scalaJSStub('clearTimeout');
	var setInterval = scalaJSStub('setInterval');
	var clearInterval = scalaJSStub('clearInterval');
}