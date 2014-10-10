/**
 * Implementation was taken from
 * https://github.com/hugorodrigues/rpc.js
 * 
 * JSON-RPC Specification
 * http://www.jsonrpc.org/specification
 *
 **/

define(function(require) {
    return {
        gateway: function(config) {

            if (config.schema === undefined) {
                console.log('No schema defined');
                return false;
            }

            var schema = config.schema;

            var errors = {
                '-32601': { msg: 'Method not found' },
                '-32700': { msg: 'No JSON was received.' },
                '-32600': { msg: 'The JSON sent is invalid.' },
                '-32602': { msg: 'Invalid method parameter(s)' },
                '-326021': { msg: 'Internal error' }
            };

            if (config.errors)
                for (error in config.errors)
                    errors[error] = config.errors[error];

            var obj = {};

            // Main input flow
            obj.input = function(request) {
                if (request.input === undefined)
                    try {
                        request.input = JSON.parse(request.textInput);
                } catch (err) {
                    return obj.outputError(request, -32600);
                }

                // validate requested method
                if (request.input.method === '' || schema.methods[request.input.method] === undefined)
                    return obj.outputError(request, -32601);

                //hot fix for array params
                if (Array.isArray(request.input.params) && request.input.params[0])
                    request.input.params = request.input.params[0];

                // Validate requested params
                for (param in schema.methods[request.input.method].params)
                    if (schema.methods[request.input.method].params[param].required === true && (request.input.params === undefined || request.input.params[param] === undefined || request.input.params[param] === ''))
                        return obj.outputError(request, -32602);

                schema.methods[request.input.method].action(request.input.params, {
                    success: function(output) {
                        obj.output(request, output);
                    },
                    failure: function(code, msg) {
                        obj.outputError(request, code, msg)
                    },
                });
            };

            obj.output = function(request, output) {
                var response = {
                    jsonrpc: "2.0",
                    result: output
                }
                if (request.input && request.input.id !== undefined)
                    response.id = request.input.id
                request.callback(response);
            };

            obj.outputError = function(request, code, msg, data) {
                if (msg == undefined && errors[code] !== undefined)
                    msg = errors[code].msg;
                if (data == undefined && errors[code] !== undefined)
                    data = errors[code].data;
                var response = {
                    jsonrpc: "2.0",
                    error: {
                        "code": code,
                        "message": msg,
                        "data": data,
                    }
                }
                if (request.input && request.input.id !== undefined)
                    response.id = request.input.id
                request.callback(response);
            };
            return obj;
        },

        server: function(type, config) {
            gateway.input({
                textInput: inputData,
                callback: function(output) {
                    console.log(output);
                }
            });
        },
    };

});