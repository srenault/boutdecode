/**
 * boutdecode.js
 */

$(document).ready(function() {
    window.BoutDeCode = new Simrou({
        '/' : {
            get: function() {
                console.log('Welcome BoutDeCode !');
                var uri = jsRoutes.controllers.Application.feeds().absoluteURL();
                var eventSource = new EventSource(uri);
                eventSource.onmessage = function(msg) {
                    console.log(JSON.parse(msg.data));
                };
                eventSource.onerror = function() {
                    console.log('Error while getting feeds');
                };
            }
        }
    });
    BoutDeCode.start('/');
});