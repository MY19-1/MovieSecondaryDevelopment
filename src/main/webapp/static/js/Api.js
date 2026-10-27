var apiScript = document.querySelector('script[src*="/static/js/Api.js"]');
var url = window.location.origin;

if (apiScript) {
    var apiScriptUrl = new URL(apiScript.src, window.location.href);
    var apiScriptPath = apiScriptUrl.pathname;
    var apiScriptSuffix = "/static/js/Api.js";
    var apiScriptIndex = apiScriptPath.lastIndexOf(apiScriptSuffix);

    if (apiScriptIndex >= 0) {
        url = apiScriptUrl.origin + apiScriptPath.substring(0, apiScriptIndex);
    }
}