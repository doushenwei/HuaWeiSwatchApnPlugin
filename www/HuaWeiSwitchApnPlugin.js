var exec = require('cordova/exec');

exports.setApn = function (arg0, success, error) {
    exec(success, error, 'HuaWeiSwitchApnPlugin', 'setApn', [arg0]);
};
exports.setInternet = function (arg0, success, error) {
    exec(success, error, 'HuaWeiSwitchApnPlugin', 'setInternet', [arg0]);
};
exports.initApnConfig = function (arg0, success, error) {
    exec(success, error, 'HuaWeiSwitchApnPlugin', 'initApnConfig', [arg0]);
};
