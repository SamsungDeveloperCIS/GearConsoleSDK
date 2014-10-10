define(function (require) {
    return {
        params: {
            state: { required: false, type: 'string', info: 'Set state ["SCREEN_NORMAL", "SCREEN_OFF", "SCREEN_DIM"]' },
            turnOn: { required: false, type: 'boolean', info: 'Set turnOn screen' },
            turnOff: { required: false, type: 'boolean', info: 'Set turnOff' }
        },
        action: function(params, output) {
            console.log('screen action');
            if (params.state && ["SCREEN_NORMAL", "SCREEN_OFF", "SCREEN_DIM"].indexOf(params.state)) {
            	tizen.power.request('SCREEN', params.state);
            }
            if (params.turnOff) {
            	tizen.power.turnScreenOff();
            }
            if (params.turnOn) {
            	tizen.power.turnScreenOn();
            }
            output.success(true);
        }
    };
});