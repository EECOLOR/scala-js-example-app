console.log("Starting tests");

ScalaJS.modules.test_Test();

var jasmineEnv = jasmine.getEnv();
jasmineEnv.addReporter(ScalaJS.modules.test_JasmineRhinoReporter());
jasmineEnv.updateInterval = 0;
jasmineEnv.execute();

// if (jasmineEnv.currentRunner().results().failedCount > 0)
// throw new Error("Some tests failed")
