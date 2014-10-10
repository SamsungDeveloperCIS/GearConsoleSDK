var App = function(){
    this.element = null;
};
App.prototype.customEvent = function(){
    console.log('customEvent from App');
    alert('customEvent from App');
};

define(function (require) {
    //initialize variables once
    var app = new App();

    //return will be cached
    
    return {
        params: {
            name: { required: true, type: 'string', info: 'element id' }
        },
        action: function(params, output) {
            console.log('Call custom_event');
            app.element = document.getElementById(params.name);
            app.element.addEventListener('click', app.customEvent);
            
            return {
                getElement: function() {
                    return app.element;
                },
                addEvent: function(name) {
                    
                },
                delEvent: function() {
                    app.element.removeEventListener('click', app.customEvent);
                }
            }
        },
        event: app.customEvent
    };
});