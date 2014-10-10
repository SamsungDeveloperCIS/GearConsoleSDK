
define(function (require) {
    var result = true;
    var loader = require('gearconsole/methods/loader');
    var logo = document.getElementById('logo');
    return {
        params: {
            base64string: { required: true, type: 'string', info: 'base64 coded string of image' },
            hide_loader: { required: false, type: 'boolean', info: 'hide loader after finish loading' }
        },
        action: function(params, output) {
            console.log('load_image action');
            if (params.hide_loader) {
                logo.addEventListener("load", l = function() {
                    console.log("logo onload");
                    loader.hide();
                });
            }
            logo.src = ''; //if we store the same 'src' again the event 'load' do not fire
            var src = 'data:image/gif;base64,'+params.base64string;
            logo.src = src;
            output.success(result);
        }
    };
});