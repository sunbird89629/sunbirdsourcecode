/* 接口定义 */

var Interface = function(name, methods) {
	if (arguments.length !== 2) {
		throw new Error('Interface constructor called with ' + arguments.length + ' arguments, but expected exactly 2.');
	}
	this.name = name;
	this.methods = [];
	for (var i = 0, len = methods.length; i < len; i++) {
		if (typeof methods[i] !== 'string') {
			throw new Error('Interface constructor excepts method names to be ' + 'passed in as a string.');
		}
		this.methods.push(methods[i]);
	}
};

Interface.ensureImplements = function(object) {
	if (TankWar.develop_model === 'product') return; // 只在开发和测试时，检查是否实现接口
	if (arguments.length < 2) {
		throw new Error('Function Interface.ensureImplements called with ' + arguments.length + 'arguments, but expected at least 2.');
	}

	for (var i = 1, len = arguments.length; i < len; i++) {
		var interface = arguments[i];
		if (interface.constructor !== Interface) {
			throw new Error('Function Interface.ensureImplements expects arguments' + ' two and above to be instances of Interface.');
		}

		for (var j = 0, methodsLen = interface.methods.length; j < methodsLen; j++) {
			var method = interface.methods[j];
			if (!object[method] || typeof object[method] !== 'function') {
				throw new Error('Function Interface.ensureImplements: object '
					+ 'does not implement the ' + interface.name
					+ ' interface.Method ' + method + ' was not found.');
			}
		}
	}
};

/* 继承方法 */

function extend(subClass, superClass) {
	function F() {};
	F.prototype = superClass.prototype;
	subClass.prototype = new F;
	subClass.prototype.constructor = subClass;
	subClass.superclass = superClass.prototype;
	if (superClass.prototype.constructor == Object.prototype.constructor) {
		superClass.prototype.constructor = superClass;
	}
};

/* Augment function. */

function augment(receivingClasses, givingClass) {
	if (arguments[2]) { // Only give certain methods.
		for (var i = 0, len = arguments[2].length; i < len; i++) {
			receivingClass.prototype[arguments[2][i]] = givingClass.prototype[arguments[2][i]];
		}
	} else { // Give all methods.
		for (var methodName in givingClass.prototype) {
			for (var j = 0, n = receivingClasses.length; j < n; j++) {
				if (!receivingClasses[j].prototype[methodName]) {
					receivingClasses[j].prototype[methodName] = givingClass.prototype[methodName];
				}
			}
		}
	}
};

Array.prototype.indexOf = function(val) {
	for (var i = 0, len = this.length; i < len; i++) {
		if (this[i] == val) return i;
	}
	return -1;
};
Array.prototype.remove = function(val) {
	var index = this.indexOf(val);
	if (index > -1) {
 		this.splice(index, 1);
	}
};
Array.prototype.clone = function() {
	var arr = [];
	for (var i = 0, len = this.length; i < len; i++) {
		arr[i] = this[i];
	}
	return arr;
};