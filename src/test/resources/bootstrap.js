/**
 * This file is loaded first and used for patching the JS environment.
 */
console.log("Bootstrapping...")

/*
 * Stub-out timer methods used by Jasmine and not provided by Rhino.
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

var baseUrl;

try {
	undefined.getSourceName
} catch (e) {
	baseUrl = "" + e.rhinoException.sourceName().replaceAll("/target/.+$", "")
}

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
		} else if (absolutePath.endsWith(file)) {
			return "" + absolutePath;
		}
	}

	return foundFile;
}

function handleError(url, e) {
	console.log("error in " + url)
	console.log(e)
	console.log(e.stack)
}

importScripts = function(url) {
	try {
		var file = findFileIn(baseUrl, url);
		var fileContents = java.util.Scanner(new java.io.File(file))
				.useDelimiter('$').next();
		eval.call(this, "try {\n " + fileContents
				+ " \n} catch(e) { handleError('" + url + "', e); }");
	} catch (e) {
		console.log("Error on importScripts while loading " + url + ": " + e);
	}
}

require = {
	deps : [ "test" ],
	baseUrl : "",
	shim : {
		"jasmine" : {
			exports : "jasmine"
		}
	}
};

require.onError = function() {
	console.log("require error")
}