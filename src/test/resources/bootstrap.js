/**
 * This file is loaded first and used for patching the JS environment.
 */
console.log("Bootstrapping...")

/*
 * Stub-out timer methods used by Jasmine, RequireJs, etc.
 */

function scalaJSStub(name) {
	return function() {
		console.log("Stub for " + name + " called with arguments:");
	}
};

if (typeof setTimeout == 'undefined') {
	var setTimeout = function(fn) {
		scalaJSStub('setTimeout')
		fn();
	};
	var clearTimeout = scalaJSStub('clearTimeout');
	var setInterval = scalaJSStub('setInterval');
	var clearInterval = scalaJSStub('clearInterval');
}

// determine baseUrl (for searching files
var baseUrl;

try {
	undefined.getSourceName
} catch (e) {
	baseUrl = "" + e.rhinoException.sourceName().replaceAll("/target/.+$", "")
}

// utility method to find files
function findFileIn(path, file) {

	var root = new java.io.File(path);
	var list = root.listFiles();

	if (list == null)
		return;

	var foundFile;

	for ( var i in list) {
		var f = list[i];
		var absolutePath = f.getAbsolutePath();

		if (f.isDirectory()) {
			foundFile = findFileIn(absolutePath, file);
			if (foundFile)
				break;
		} else if (absolutePath.endsWith(file))
			return "" + absolutePath;
	}

	return foundFile;
}

// is used by requirejs to determine if the environment is useful
importScripts = function(url) {
	try {
		var file = findFileIn(baseUrl, url);
		var fileContents = java.util.Scanner(new java.io.File(file))
				.useDelimiter('$').next();

		// we need to turn the file contents into a javascript string, there
		// seems no other way
		eval.call(this, "" + fileContents);
	} catch (e) {
		console.error("Error on importScripts while loading " + url + ": " + e);
	}
}