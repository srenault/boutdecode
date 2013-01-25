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
                    var commit = JSON.parse(msg.data);
                    console.log(commit);
                    var commitAsHTML =
                    ('<div class="commit">' +
                        '<span class="author">%author%</span>' +
                        '<span class="date">%date%</span>' +
                        '<pre>%diff%</pre>' +
                        '<span class="hash">%hash%</span>' +
                        '<span class="message">%message%</span>' +
                     '</div>').replace('%author%', commit.author)
                              .replace('%date%', commit.date)
                              .replace('%diff%', commit.diff)
                              .replace('%hash%', commit.hash)
                              .replace('%message%', commit.message);

                    console.log(commitAsHTML);
                    $('.drop-zone').html($(commitAsHTML));
                };
                eventSource.onerror = function() {
                    console.log('Error while getting feeds');
                };
            }
        }
    });
    BoutDeCode.start('/');
});

(function() {
    window.ansi2html = function(ansi) {
        return ansi.replace(/\[(\d+)m/g,'<span class="color_$1">').replace(/\[m/g,"</span>")
    }
})()