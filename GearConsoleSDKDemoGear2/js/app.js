requirejs.config({
    baseUrl: 'js',
    paths: {
        app: './'
    }
});

var gsap, grpc, gmethods;

function send(text) {
    grpc.gateway({ schema: gmethods })
    .input({
        textInput: text,
        callback: function (output) {
            console.log(JSON.stringify(output));
        }
    });
}
requirejs(['./gearconsole/sap','./gearconsole/jsonrpc-server','./gearconsole/jsonrpc-methods'], function (sap, rpc, methods) {
    gsap = sap; grpc = rpc; gmethods = methods;
    console.log('app.js');
    }
 );
