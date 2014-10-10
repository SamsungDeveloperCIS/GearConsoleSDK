define(function (require) {
    var result = true;
    return {
        params: {
        },
        action: function(params, output) {
            output.success(result);
        }
    };
});