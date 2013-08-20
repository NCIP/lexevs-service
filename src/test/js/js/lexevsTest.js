var test_settings = 
{
	serviceUrl : "http://bmidev4:5555/cts2",
	csvName : "LNC-229",
	csvVersion : "229",
	csvUri : "urn:oid:2.16.840.1.113883.6.1:229",
	csvCodeSystem : "LNC",
	entityQuery : "heart",
	entityRead : {
		cs : "LNC",
		csv : "229",
		name : "LP41057-8",
		namespace : "LNC",
		uri : "http://id.nlm.nih.gov/cui/C1136323/LP41057-8"
	}
}

run_tests(test_settings);

function run_tests(settings){
	
	/*
	 * Service Metadata
	 */
	asyncTest("Test Service Page", function() {
		var url = settings.serviceUrl + "/service";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
		});
	});
	
	/*
	 * Code System Versions
	 */
	asyncTest("Test Query Code System Versions", function() {
		var url = settings.serviceUrl + "/codesystemversions";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.codeSystemVersionCatalogEntryDirectory.entryList.length > 0);
		});
	});
	
	asyncTest("Test Read Code System Version by Name", function() {
		var url = settings.serviceUrl + "/codesystem/"+settings.csvCodeSystem+"/version/"+settings.csvName;
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.codeSystemVersionCatalogEntryMsg);
		});
	});
	
	asyncTest("Test Read Code System Version by Version ID", function() {
		var url = settings.serviceUrl + "/codesystem/"+settings.csvCodeSystem+"/version/"+settings.csvVersion;
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.codeSystemVersionCatalogEntryMsg);
		});
	});
	
	asyncTest("Test Read Code System Version by URI", function() {
		var url = settings.serviceUrl + "/codesystemversionbyuri?uri="+settings.csvUri;
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.codeSystemVersionCatalogEntryMsg);
		});
	});
	
	/*
	 * Entity
	 */
	asyncTest("Test Query Entities", function() {
		var url = settings.serviceUrl + "/entities";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.entityDirectory.entryList.length > 0);
		});
	});
	
	asyncTest("Test Query Entities Contains", function() {
		var url = settings.serviceUrl + "/entities?q="+settings.entityQuery+"&matchalgorithm=contains";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.entityDirectory.entryList.length > 0);
		});
	});
	
	asyncTest("Test Query Entities Exact", function() {
		var url = settings.serviceUrl + "/entities?q="+settings.entityQuery+"&matchalgorithm=exactMatch";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.entityDirectory.entryList.length > 0);
		});
	});
	
	asyncTest("Test Read Entity", function() {
		var r = settings.entityRead
		var url = settings.serviceUrl + "/codesystem/"+r.cs+"/version/"+r.csv+"/entity/"+r.name;
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.entityDescriptionMsg);
		});
	});
	
	asyncTest("Test Read Entity Not Found", function() {
		var r = settings.entityRead
		var url = settings.serviceUrl + "/codesystem/"+r.cs+"/version/"+r.csv+"/entity/__INVALID__";
		rest_test_jsonp(url, function(data, status){
			ok(status === "error");
		});
	});
	
	asyncTest("Test Read Entity Descriptions by Name", function() {
		var r = settings.entityRead
		var url = settings.serviceUrl + "/entity/"+r.namespace+":"+r.name;
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.entityReferenceMsg);
		});
	});
	
	asyncTest("Test Read Entity Descriptions by Uri", function() {
		var r = settings.entityRead
		var url = settings.serviceUrl + "/entitybyuri?uri="+r.uri;
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.entityReferenceMsg);
		});
	});
	
	/*
	 * Maps
	 */
	asyncTest("Test Query Maps", function() {
		var url = settings.serviceUrl + "/maps";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.mapCatalogEntryDirectory.entryList.length > 0);
		});
	});
	
	/*
	 * Map Versions
	 */
	asyncTest("Test Query Map Versions", function() {
		var url = settings.serviceUrl + "/mapversions";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.mapVersionDirectory.entryList.length > 0);
		});
	});
	
	/*
	 * Value Set Definitions
	 */
	asyncTest("Test Query Value Set Definitions", function() {
		var url = settings.serviceUrl + "/valuesetdefinitions";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.valueSetDefinitionDirectory.entryList.length > 0);
		});
	});
	
	asyncTest("Test Read Value Set Definitions", function() {
		var url = settings.serviceUrl + "/valuesetdefinitions";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.valueSetDefinitionDirectory.entryList.length > 0);
		});
	});
	
	/*
	 * Resolved Value Sets
	 */
	asyncTest("Test Query Resolved Value Sets", function() {
		var url = settings.serviceUrl + "/resolvedvaluesets";
		rest_test_jsonp(url, function(data, status){
			ok(data != null);
			ok(data.resolvedValueSetDirectory.entryList.length > 0);
		});
	});

}

function rest_test_jsonp(url, callback) {
	if(! url.contains("?")){
		url += "?";
	} else {
		url += "&";
	}
	$.jsonp({
	      "url": url+"format=json&callback=?",
	      "success": function(data, status, message) {
	    	  callback(data, status, message);
	          start();
	      },
	      "error": function(data, status, message) {
	    	  callback(data, status, message);
	          start();
	      }
	    });
}