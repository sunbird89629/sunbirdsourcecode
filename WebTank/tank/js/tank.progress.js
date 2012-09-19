var PreLoad = (function() {
	// 私有静态方法
	function obj2array(givenObj) {
		var urllist = [], patrn = /1-\d{1,2}\.(png|json)$/, level = 0, levelArr = [];
		if (TankWar.mySite) levelArr[level++] = TankWar.mySite;
		(function(obj) {
			for (var prop in obj) {
				if (prop === 'urls') {
					for (var i = 0, n = obj[prop].length; i < n; i++) {
						if (patrn.test(obj[prop][i])) {
							var tmp = obj[prop][i].split('.')[0].split('-'), suffix = patrn.exec(obj[prop][i])[1];
							for (var j = tmp[0], m = tmp[1]; j <= m; j++) {
								urllist.push(levelArr.join('/') + '/' + j + '.' + suffix);
							}
						} else {
							urllist.push(levelArr.join('/') + '/' + obj[prop][i]);
						}
					}
					levelArr.splice(--level, 1);
				} else {
					levelArr[level++] = prop;
					arguments.callee(obj[prop]);
				}
			}
		})(givenObj);
		return urllist;
	};
	// 构造器
	return function(urlObj, callback) {
		this.callback = callback;
		if (!TankWar.mySite) { // 如果没有启动预加载，直接进入回调
			this.progressBar(100);
			return;
		}
		this.urlList = obj2array(urlObj);
		this.total = this.urlList.length;
		this.succeedcount = 0;
		this.errorcount = 0;
		this.init();
	}
})();

PreLoad.prototype = {
	loadImg: function(url) {
		var img = new Image(), that = this;
		img.onload = function() {
			that.complete(url, '图片');
		}
		img.onerror = function() {
			that.error(url);
		}
		img.src = url;
	},
	loadMap: function(url) {
		var that = this;
		$.getJSON(url, function(map) {
			TankWar.maps.push(map);
			that.complete(url, '地图');
		});
	},
	complete: function(url, type) {
		this.progressBar(Math.round(++this.succeedcount*100/this.total), url, type);
	},
	error: function(url) {
		throw new Error('load '+ url +' failed.');
	},
	progressBar: function(percent, url, type) {
		if (url && type) {
			$('#percent span').text(percent);
			$('#loading span').text(type + ': ' + url.substr(url.lastIndexOf('/') + 1, url.length));
		}
		$('#bar').stop().animate({left: 550 - 550*percent/100}, 200);
		if (percent === 100) this.over();
	},
	over: function() {
		var that = this;
		setTimeout(function() {
			that.callback();
		}, 500);
	},
	init: function() {
		$('#percent, #loading').show();
		for (var i = 0; i < this.total; i++) {
			if (/\.json$/.test(this.urlList[i]))
				this.loadMap(this.urlList[i]);
			else
				this.loadImg(this.urlList[i]);
		}
	}
};