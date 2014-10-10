var GearConsoleSDKDemo = function() {
	this.D = true;
	this.DV = false;
	this.SAAgent = null;
	this.SASocket = null;
	this.CHANNELID = 134;
	this.ProviderAppName = 'GearConsoleSDKDemoProvider';
	this.appId = '5t13HmJcA3.GearConsoleSDKDemo';
	this.inputType = 'default';
	//this.last_x = 0; this.last_y = 0; this.last_z = 0;
	this.SHAKE_THRESHOLD = 60;
	this.SPEED_TIMEOUT = 200;
	this.lastUpdate = 0;
	this.receivedData = null;
	this.connectInterval = -1;
	this.visibilityState = '';
	this.logo = null;
	this.connectIcon = null;
	this.loader = null;
};

GearConsoleSDKDemo.prototype.inputs = {};

GearConsoleSDKDemo.prototype.getAgentCallback = function() {
    return {
        onconnect: function(socket) {
            this.SASocket = socket;
            if (this.D) console.log('Accessory Connection established with RemotePeer');
//            createHTML('startConnectingProcess');
            this.SASocket.setDataReceiveListener(this.onReceive.bind(this));
            this.SASocket.setSocketStatusListener(function(reason) {
                if (this.D) console.log('Service connection lost, Reason : [' + reason + ']');
                this.disconnect();
            }.bind(this));
            this.registerEvents();
        }.bind(this),
        onerror: this.onError.bind(this)
    }
};

GearConsoleSDKDemo.prototype.onError = function(error) {
	if (this.D) console.log('An error occured: '+error);
	this.startConnectingProcess();
};

GearConsoleSDKDemo.prototype.getPeerAgentFindCallback = function() {
    //README: could not modify this way because of platform
	return {
	    onpeeragentfound: function(peerAgent) {
	        try {
	            if (peerAgent.appName == this.ProviderAppName) {
	                this.SAAgent.setServiceConnectionListener(this.getAgentCallback());
	                this.SAAgent.requestServiceConnection(peerAgent);
	            } else {
	            	if (this.D) console.log('Not expected app!! : ' + peerAgent.appName);
	            }
	        } catch (err) {
	            if (this.D) console.log('exception [' + err.name + '] msg[' + err.message + ']');
	        }
	    }.bind(this),
	    onerror: this.onError.bind(this)
    }
};

GearConsoleSDKDemo.prototype.onSuccess = function(agents) {
	if (this.D) console.log('onSuccess');
    try {
        if (agents.length > 0) {
            this.SAAgent = agents[0];
            this.SAAgent.setPeerAgentFindListener(this.getPeerAgentFindCallback());
            this.SAAgent.findPeerAgents();
        } else {
        	if (this.D) console.log('Not found SAAgent!!');
        }
        this.clearConnectingProcess();
    } catch (err) {
        if (this.D) console.log('exception [' + err.name + '] msg[' + err.message + ']');
    }
};

GearConsoleSDKDemo.prototype.connect = function() {
	if (this.D) console.log('Trying to connect');
    if (this.SASocket) {
        if (this.D) {
        	console.log('Already connected!');
        	this.clearConnectingProcess();
        }
        return false;
    }
    try {
        webapis.sa.requestSAAgent(this.onSuccess.bind(this), this.onError.bind(this));
    } catch (err) {
        if (this.D) console.log('exception [' + err.name + '] msg[' + err.message + ']');
    }
}

GearConsoleSDKDemo.prototype.disconnect = function() {
	if (this.D) console.log('disconnect');
    try {
        if (this.SASocket != null) {
        	this.SASocket.close();
        	this.SASocket = null;
        }
    } catch (err) {
        if (this.D) console.log('exception [' + err.name + '] msg[' + err.message + ']');
    }
};

////TODO delete
//GearConsoleSDKDemo.prototype.fetch = function() {
//    if (this.D) console.log('fetch');
//    try {
//        this.sendData('{config:{},batch:[{action:0,code:96},{action:1,code:96}]}');
//    } catch (err) {
//        if (this.D) console.log('exception [' + err.name + '] msg[' + err.message + ']');
//    }
//}

GearConsoleSDKDemo.prototype.onReceive = function(channelId, data) {
    if (this.D) console.log('onreceive channelId=' + channelId + ' data=' + data);
    data = data.replace(/(?:\r\n|\r|\n)/g,'');
    try {
    	this.receivedData = JSON.parse(data);
    	
    	if (data && grpc && gmethods) {
    		grpc.gateway({ schema: gmethods }).input({
    	        textInput: data,
    	        callback: function (output) {
    	        	this.sendData(JSON.stringify(output));
    	            console.log(JSON.stringify(output));
    	        }.bind(this)
    	    });
    	}
    	
    	if (this.receivedData && this.receivedData.config) {
	    	if (this.receivedData.config.iconBase64) {
	    		var src = 'data:image/gif;base64,'+this.receivedData.config.iconBase64;
	    		this.logo.src = src;
//	    		localStorage.setItem('src', src);
//	    		setTimeout(function(){
//		    		g.logo.style.visibility = 'visible';
//		    		g.loader.style.visibility = 'hidden';
//	    		}, 0);
	    	}
	    	if (this.receivedData.config.inputType) {
	    		this.inputType = this.receivedData.config.inputType;
	    	}
	    	tizen.power.turnScreenOn();
	    	//TODO make checks has app already run
	    	
//            var alarm = new tizen.AlarmAbsolute(new Date(+(new Date()) + 1000));
//	    	tizen.alarm.add(alarm, this.appId);
            
            if (this.visibilityState == 'hidden') {
                tizen.application.launch(this.appId);
            }
    	}
    	if (this.receivedData && this.receivedData.loader) {
    		if (this.D) console.log('onreceive loader!');
    		this.logo.style.visibility = 'hidden';
    		this.loader.style.visibility = 'visible';
    		this.logo.src = '';
    	}
    } catch (err) {
    	if (this.D) console.log('onReceive exception [' + err.name + '] msg[' + err.message + ']');
    	this.startConnectingProcess();
    }
};

GearConsoleSDKDemo.prototype.sendData = function(data) {
	if (!this.SASocket) {
		this.startConnectingProcess();
		return;
	}
    try {
        this.SASocket.sendData(this.CHANNELID, data);
    } catch (err) {
        if (this.D) console.log('exception [' + err.name + '] msg[' + err.message + ']');
    }
};

GearConsoleSDKDemo.prototype.pressKeyCode = function(action, code) {
	if (!this.SASocket) {
		this.startConnectingProcess();
		return;
	}
    try {
        this.SASocket.sendData(this.CHANNELID, '{batch:[{action:'+action+',code:'+code+'}]}');
    } catch (err) {
        if (this.D) console.log('exception [' + err.name + '] msg[' + err.message + ']');
        this.startConnectingProcess();
    }
};

GearConsoleSDKDemo.prototype.setLogo = function(object) {
	this.logo = object;
};

GearConsoleSDKDemo.prototype.setConnectIcon = function(object) {
	this.connectIcon = object;
};

GearConsoleSDKDemo.prototype.onDeviceMotionListener = function(e) {
	var DEBUG = false;
    if (DEBUG) {
        console.log('acceleration value ' + e.acceleration.x.toFixed(2).toString() + ' ' + e.acceleration.y.toFixed(2).toString() + ' ' + e.acceleration.z.toFixed(2).toString());
        console.log('accelerationIncludingGravity [%s %s %s]', e.accelerationIncludingGravity.x.toFixed(2).toString(),
            e.accelerationIncludingGravity.y.toFixed(2).toString(), e.accelerationIncludingGravity.z.toFixed(2).toString());
        console.log('rotationRate [%s %s %s]', e.rotationRate.alpha ? e.rotationRate.alpha.toFixed(2).toString() : null,
            e.rotationRate.beta ? e.rotationRate.beta.toFixed(2).toString() : null, e.rotationRate.gamma ? e.rotationRate.gamma
            .toFixed(2).toString() : null);
    }
    
    this.inputs[this.inputType].bind(this)(e);
};

//todo delete and removeEventListener
GearConsoleSDKDemo.prototype.inputs['default'] = function(e) {
}

GearConsoleSDKDemo.prototype.onScreenStateChanged = function(previousState, changedState) {
    console.log('Screen state changed from ' + previousState + ' to ' + changedState);
    if (changedState === 'SCREEN_OFF') {
        console.log('window SCREEN_OFF ala onblur');
    }
    if (changedState === 'SCREEN_NORMAL') {
        console.log('window SCREEN_NORMAL ala onfocus');
    }
};

GearConsoleSDKDemo.prototype.onVisibilityChangeListener = function() {
	this.visibilityState = document.visibilityState;
    if (this.visibilityState == 'visible') {
        tizen.power.request('SCREEN', 'SCREEN_NORMAL');
    } else {
        tizen.power.release('SCREEN');
    }
};

GearConsoleSDKDemo.prototype.startConnectingProcess = function() {
	if (this.D) console.log('startConnectingProcess');
	this.connectInterval = setTimeout(this.connect.bind(this), 300);
	this.connectIcon.src = 'img/presence_busy.png';
};

GearConsoleSDKDemo.prototype.clearConnectingProcess = function() {
	if (this.D) console.log('clearConnectingProcess');
	clearTimeout(this.connectInterval);
	this.connectIcon.src = 'img/presence_online.png';
};

GearConsoleSDKDemo.prototype.registerEvents = function() {
	if (this.D) console.log('registerEvents');
	window.addEventListener('devicemotion', this.onDeviceMotionListener.bind(this));
	tizen.power.request('SCREEN', 'SCREEN_NORMAL');
    tizen.power.setScreenStateChangeListener(this.onScreenStateChanged.bind(this));
    webapis.sa.setDeviceStatusListener(this.onDeviceStatus.bind(this));
    document.addEventListener('visibilitychange', this.onVisibilityChangeListener.bind(this));
};

GearConsoleSDKDemo.prototype.unregisterEvents = function() {
	if (this.D) console.log('unregisterEvents');
	window.removeEventListener('devicemotion', this.onDeviceMotionListener.bind(this));
};


GearConsoleSDKDemo.prototype.init = function() {
};

//onDeviceStatus type=TRANSPORT_BT, status=DETACHED
//enum DeviceStatus {'ATTACHED', 'DETACHED'};
GearConsoleSDKDemo.prototype.onDeviceStatus = function(type, status){
	console.log('onDeviceStatus type='+type+', status='+status);
};

var g;
var logo;
window.onload = function() {
	console.log('onload');
    logo = document.getElementById('logo');
    document.addEventListener('tizenhwkey', function(e) {
        if (e.keyName == 'back')
            tizen.application.getCurrentApplication().exit();
    });
    
    var c = document.getElementById('container');
    c.style.top = (screen.height - c.offsetHeight) / 2 + 'px';
    
    g = new GearConsoleSDKDemo();
    g.setLogo(logo);
    g.setConnectIcon(document.getElementById('connect'));
    g.init();
    g.connect();
    g.loader = document.getElementById('loader');
    g.loader.style.visibility = 'hidden';
    g.logo.addEventListener('load', l = function() {
    	console.log('logo onload');
    	g.logo.style.visibility = 'visible';
		g.loader.style.visibility = 'hidden';
	});
    
    logo.addEventListener('click', function() {
    	tizen.power.request('SCREEN', 'SCREEN_NORMAL');
    });
};

window.onblur = function() {
    console.log('window onblur');
};

window.onfocus = function() {
    console.log('window onfocus');
};

window.onunload = function() {
    console.log('window onunload');
};

