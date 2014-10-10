var methods = ['loader', 'custom_click'];
methods.push('new_method');
methods.push('load_image');
methods.push('screen');
//Add your new mthods here

var load_methods = ['require'];
for (var i in methods) {
    //pre load
//    require('gearconsole/methods/'+methods[i]);
    load_methods.push('gearconsole/methods/'+methods[i]);
}
console.log(load_methods);

define(load_methods, function (require) {
    console.log(arguments);
    var result = {
    methods: {
        subscribe: {
            params: {
                name: { required: true, type: 'string', info: 'service name' },
                data: { required: false, type: 'object', info: 'parameters for the service' },
            },
            action: function(params, output) {
                console.log('Call subscribe name='+params.name);
                switch (params.name) {
                    case 'devicemotion':
//                        var result = 'all done';
//                        output.success(result);
                        break;
                    case 'gesture':
                        var result = 'all done';
                        output.success(result);
                        break;
                    case 'click':
                        var logo = document.getElementById('logo');
                        window._gearconsole_click = {};
                        window._gearconsole_click.listener = function (e) {
                            console.log('click', e);
                        };
                        logo.addEventListener('click', window._gearconsole_click.listener);
                        break;
                    default:
                        output.failure(500, 'Subscribe: service name "' + params.name + '" not found');
                }
            }
        },
        unsubscribe: {
            params: {
                name: { required: true, type: 'string', info: 'service name' },
                id: { required: false, type: 'number', info: 'request id' }
            },
            action: function(params, output) {
                console.log('Call unsubscribe name='+params.name);
                switch (params.name) {
                    case 'devicemotion':
                        var result = "all done";
                        output.success(result);
                        break;
                    case 'gesture':
                        var result = "all done";
                        output.success(result);
                        break;
                    case 'click':
                        try {
                            var logo = document.getElementById('logo');
                            logo.removeEventListener('click', window._gearconsole_click.listener);
                            delete window._gearconsole_click;
                            output.success(true);
                        } catch (e) {
                            output.failure(500, e.toString());
                        }
                        break;
                    default:
                        output.failure(500, 'Unsubscribe: service name "' + params.name + '" not found');
                }
            }
        }
    }
    };
    for (var i in methods) {
        result.methods[methods[i]] = require('gearconsole/methods/'+methods[i]);
    }
    console.log(result);
    return result;
});
