asyncTest("Test CodeSystemVersionRead By Name", function() {
	var url = "http://localhost:8080/cts2/codesystem/Automobiles/version/Automobiles-1.0";
	rest_test_jsonp(url);
});

asyncTest("Test CodeSystemVersionRead By Official VersionID", function() {
	var url = "http://localhost:8080/cts2/codesystem/Automobiles/version/Automobiles-1.0sss";
	rest_test_jsonp(url);
});

function rest_test_jsonp(url, callback) {
	$.jsonp({
	      "url": url+"?format=json&callback=?",
	      "success": function(data) {
	          ok(data != null, JSON.stringify(data));
	          start();
	      },
	      "error": function(d,msg) {
	          ok(false, msg);
	          start();
	      }
	    });
}