define(function (require) {
    var loader = document.getElementById('loader');
    var container = document.getElementById('container');
    return {
//        info: 'Show Loader',
//        group: 'service',
        params: {
            show: { required: true, type: 'boolean', info: 'show or not loader' },
            text: { required: false, type: 'string', info: 'loader text' },
            id:   { required: false, type: 'number', info: 'if you try to stop loader please provide previous method id' }
        },
        action: function(params, output) {
            console.log('loader action');
            if (params.text) {
                document.querySelector('#loader .ui-processing-text').innerText = params.text;
            }
            if (params.show) {
                this.show();
            } else {
                this.hide();
            }
            output.success(true);
        },
        show: function() {
            console.log('loader show');
            loader.style.visibility = 'visible';
            container.style.visibility = 'hidden';
        },
        hide: function() {
            console.log('loader hide');
            loader.style.visibility = 'hidden';
            container.style.visibility = 'visible';
        }
    };
});