//как работает подписка:
//отличие от других методов в том что после того как мы подписались 
//мы получаем асинхронные данные от метода
//все ситуации связанные с решением проблем с энерого-эффективность, работой в фоне и проч.,
//находятся на стороне sdk, т.е. подписки на DOM-события будут автоматически создаваться и удаляться
//Если пришло 2 или более запросов в разными id, то ответы будут приходить на последний запрос с последним id
//
define(function (require) {
    console.log('method-devicemotion.js');
//    sap = require('./gearconsole/sap', 'test');
    var message = '';
    
    return function(e) {
        message = {
            rotationRate:{
                x:e.rotationRate.alpha,
                y:e.rotationRate.beta,
                z:e.rotationRate.gamma
            },
            accelerationIncludingGravity:{
                x:e.accelerationIncludingGravity.x,
                y:e.accelerationIncludingGravity.y,
                z:e.accelerationIncludingGravity.z
            },
            acceleration:{
                x:e.acceleration.x,
                y:e.acceleration.y,
                z:e.acceleration.z
            }
        };
        this.sendData(message);
    }
});