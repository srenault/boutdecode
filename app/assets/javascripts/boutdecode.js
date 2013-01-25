/**
 * boutdecode.js
 */

$(document).ready(function() {
    window.BoutDeCode = new Simrou({
        '/' : {
            get: function() {
                console.log('Welcome BoutDeCode !');
                var uri = jsRoutes.controllers.Application.progress(
                    data.username,
                    data.fullname,
                    data.language
                ).absoluteURL();

                var eventSource = new EventSource(uri);
                eventSource.onmessage = function(msg) {
                    var progress = JSON.parse(msg.data);
                    console.log(data);
                    if(data.classic) {
                        CoderSide.search.progress(progress);
                    } else {
                        CoderSide.loading.progress(progress);
                    }
                };
                eventSource.onerror = function() {
                    console.log('Error while getting progress update');
                };
            }
        }
    });
    BoutDeCode.start('/');
});