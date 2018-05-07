var exec = require('cordova/exec');

exports.setApn = function (arg0, success, error) {
    exec(success, error, 'HuaWeiSwatchApnPlugin', 'setApn', [arg0]);
};
