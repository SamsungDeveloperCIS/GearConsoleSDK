/**
 * 
 */
var message = "";
GearConsoleSDKDemo.prototype.inputs["sdk1"] = function(e) {
//    message = "{\"mode\":\"motion\",\"accelerationIncludingGravity\":{\"x\":\""
//    	+e.accelerationIncludingGravity.x+"\",\"y\":\""
//    	+e.accelerationIncludingGravity.y+"\",\"z\":\""
//    	+e.accelerationIncludingGravity.z+"\"},\"acceleration\":{\"x\":\""
//    	+e.acceleration.x+"\",\"y\":\""
//    	+e.acceleration.y+"\",\"z\":\""
//    	+e.acceleration.z+"\"},\"rotationRate\":{\"x\":\""
//    	+e.rotationRate.alpha+"\",\"y\":\""
//    	+e.rotationRate.beta+"\",\"z\":\""
//    	+e.rotationRate.gamma+"\"}}";
	message = "{\"mode\":\"motion\",\"rotationRate\":{\"x\":\""
    	+e.accelerationIncludingGravity.x+"\",\"y\":\""
    	+e.accelerationIncludingGravity.y+"\",\"z\":\""
    	+e.accelerationIncludingGravity.z+"\"}}";
    this.sendData(message);
}