var exec = require('cordova/exec');

exports.setApn = function (arg0, success, error) {
    exec(success, error, 'HuaWeiSwitchApnPlugin', 'setApn', [arg0]);
};
