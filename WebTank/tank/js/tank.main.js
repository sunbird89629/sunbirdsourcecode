// Interfaces.

var Tank = new Interface('Tank', ['explode','clear','die','fire','isShot','move','stopMoving','isBlocked','attachEvents','init']);
var Bullet = new Interface('Bullet',['explode','clear','die','fire','init']);
var Block = new Interface('Block', ['explode','clear','die','isShot','display']);

// Abstract tank, impl base methods.
var AbstractTank = function(opt) { // Constructor
	this.id = opt.id;
	this.isAlive = true;
	this.speed = opt.speed;
	this.pos = opt.pos;
	this.top = opt.pos.y;
	this.left = opt.pos.x;
	this.movingToward = 'up';
	this.init();
	this.attachEvents();
};

AbstractTank.prototype = { // Public methods
	move: function(_d) {
		if (!this.isAlive || !_d || TankWar.state.exit || TankWar.state.pause) {
			return this;
		}
		var thisTank = $('div#' + this.id),
		    speed = this.speed * TankWar.util.plus_minus[_d],
		    that = this,
		    dir = TankWar.util.direction[_d],
		    pos = parseInt(this[dir]);
		if (thisTank.hasClass('moving' + _d)) { // Avoid rebinding
        	return this;
        } else {
        	this.stopMoving();
        }
        this.movingToward = _d;
        thisTank.addClass('moving' + _d);
        thisTank.css('background-position', TankWar.util.bg_pos_key[_d]);
        this.moveTimr = setInterval(function() {
        	if (that.isBlocked() || !that.isAlive || TankWar.state.exit || TankWar.state.pause) {
        		that.stopMoving();
        		return this;
        	}
			thisTank.css(dir, pos += speed);
        	that[dir] = pos;
        }, 10);
		return this;
    },
	stopMoving: function() {
		var _d = this.movingToward, thisTank = $('div#' + this.id);
	    clearInterval(this.moveTimr);
	    thisTank.removeClass('moving' + _d);
	},
	isBlocked: function() {
		var _l = this.left,
			_t = this.top,
			barriers = TankWar.barrier.normalBlocks.concat(TankWar.barrier.waterBlocks).concat(TankWar.barrier.players).concat(TankWar.barrier.enemies);
		for (var i = 0, len = barriers.length; i < len; i++) {
			if (this.id !== barriers[i].id) {
				switch (this.movingToward) {
					case 'up': if (_l + 31 > barriers[i].left && _l - 31 < barriers[i].left && _t + 30 > barriers[i].top && _t - 36 < barriers[i].top || _t < 10) return true; break;
					case 'down': if (_l + 31 > barriers[i].left && _l - 31 < barriers[i].left && _t + 36 > barriers[i].top && _t - 31 < barriers[i].top || _t > 510) return true; break;
					case 'left': if (_l + 31 > barriers[i].left && _l - 36 < barriers[i].left && _t + 31 > barriers[i].top && _t - 31 < barriers[i].top || _l < 10) return true; break;
					case 'right': if (_l + 36 > barriers[i].left && _l - 31 < barriers[i].left && _t + 31 > barriers[i].top && _t - 31 < barriers[i].top || _l > 650) return true; break;
				}
			}
		}
		return false;
	},
	die: function() {
		this.isAlive = false;
		this.left = this.pos.x;
		this.top = this.pos.y;
		this.explode('mapbomb', 11);
	},
	fire: function() {
		TankWarFactory.createBullet(this);
		return this;
	},
	isShot: function() {
		throw new Error('isShot function is undefined.');
	},
	clear: function() {
		throw new Error('clear function is undefined.');
	},
	attachEvents: function() {
		throw new Error('attachEvents function is undefined.');
	},
	init: function() {
		throw new Error('init function is undefined.');
	}
};

var PlayerTank = function() { // implements Tank, extend AbstractTank
		var pnum = TankWar.barrier.players.length + 1;
		var opt = {};
		opt.id = 'p' + pnum;
		opt.pos = TankWar.players[opt.id].pos;
		opt.speed = TankWar.players[opt.id].speed;
		this.keys = TankWar.players[opt.id].keys;
		this.kills = {r:0,b:0,y:0,g:0};
		this.lives = TankWar.players[opt.id].lives;
		PlayerTank.superclass.constructor.call(this, opt);
};

extend(PlayerTank, AbstractTank);

PlayerTank.prototype.init = function() {
	$('<div id="' + this.id + '" class="tank"></div>')
		.css({
			'background': 'url(images/tank.png) 0% 55% no-repeat',
			'top': this.pos.y,
			'left': this.pos.x
		})
		.appendTo($('div#container'));
};
PlayerTank.prototype.attachEvents = function() {
	var that = this;
	$(document).keydown(function(e) {
       	var key = e.which || e.keyCode;
       	if (!that.isAlive) return;
       	if (that.keys.moveKeys[key]) that.move(that.keys.moveKeys[key]);
       	else if (that.keys.fireKey === key) that.fire();
	}).keyup(function(e) {
        var key = e.which || e.keyCode;
        if (that.isAlive && that.keys.moveKeys[key]) that.stopMoving();
   	});
};
PlayerTank.prototype.killOne = function(type) {
	this.kills[type]++;
};
PlayerTank.prototype.die = function(over) {
	this.clear();
	TankWarMng.setTankCount(this.id, --this.lives);
	PlayerTank.superclass.die.call(this);
	if (!over && this.lives) {
		this.reborn();	
	} else {
		TankWar.barrier.deadPlayers.push(this);
		TankWarMng.onePlayerDead();
	}
};
PlayerTank.prototype.isShot = function() {
	if (!this.isAlive) return; // 解决两颗子弹同时打中的bug
	this.die();
};
PlayerTank.prototype.clear = function() {
	TankWar.barrier.players.remove(this);
	TankWar.barrier.deadPlayers.remove(this);
};
PlayerTank.prototype.reborn = function() {
	var that = this;
	setTimeout(function() {
		that.init();
		that.isAlive = true;
		that.movingToward = 'up';
		TankWar.barrier.players.push(that);
	}, 1000);
};

var EnemyTank = (function() {
	// Private static methods.
	function checkInitData() {
		if (TankWar.enemies.posBorn.length < 1) {
			throw new Error('TankWar map is not loaded.');
		}
	}
	// Return EnemyTank constructor.
	return function(opt) { // implements Tank, extend AbstractTank
		checkInitData();
		if (!(opt && opt.id && opt.pos && opt.speed)) {
			var defaults = {};
			defaults.type = 'r';
			defaults.bgPic = 'tank1';
			defaults.id = 'enemyR_' +  TankWar.enemies[defaults.type].leftNum;
			defaults.speed = TankWar.enemies[defaults.type].speed;
			defaults.pos = TankWarFactory.createPosOfEnemy(defaults.type);
			defaults.bornPlace = defaults.pos.bid;
			opt = $.extend(defaults, opt || {});
		}
		this.type = opt.type;
		this.bgPic = opt.bgPic;
		this.bornPlace = opt.pos.bid;
		EnemyTank.superclass.constructor.call(this, opt);
	}
})();

extend(EnemyTank, AbstractTank);

EnemyTank.prototype.init = function() {
	$('<div id="' + this.id + '" class="tank"></div>')
		.css({
			'background': 'url(images/etank/'+ this.bgPic +'.png) 0% 99% no-repeat',
			'top': this.pos.y,
			'left': this.pos.x
		})
		.appendTo($('div#container'));
};
EnemyTank.prototype.attachEvents = function() {
	var that = this, directions = ['up', 'down', 'left', 'right'];
	this.move('down').fire().moveAuto = setInterval(function() {
		if (TankWar.state.exit || !that.isAlive) {
			clearInterval(that.moveAuto);
			return;
		}
		if (TankWar.state.pause) return;
		that.move(directions[Math.round(Math.random()*3)]);
		that.fire();
	}, Math.round(Math.random()*1000 + 500));
};
EnemyTank.prototype.die = function() {
	this.clear();
	EnemyTank.superclass.die.call(this, true);
	TankWarMng.oneEnemyDead(this.by, this.type);
};
EnemyTank.prototype.isShot = function(by) {
	if (!this.isAlive) return; // 解决两颗子弹同时打中的bug
	this.by = by;
	this.die();
};
EnemyTank.prototype.clear = function() {
	clearInterval(this.moveAuto);
	TankWar.enemies.posBorn[this.bornPlace].avaliable = true;
	TankWar.barrier.enemies.remove(this);
};

var EnemyRTank = function() {
	EnemyRTank.superclass.constructor.call(this);
};

extend(EnemyRTank, EnemyTank);

var EnemyBTank = function() {
	var opt = {};
	opt.type = 'b';
	opt.bgPic = 'tank2';
	opt.id = 'enemyB_' +  TankWar.enemies[opt.type].leftNum;
	opt.speed = TankWar.enemies[opt.type].speed;
	opt.pos = TankWarFactory.createPosOfEnemy(opt.type);
	EnemyBTank.superclass.constructor.call(this, opt);
};

extend(EnemyBTank, EnemyTank);

var EnemyYTank = function() {
	var opt = {};
	opt.type = 'y';
	opt.bgPic = 'tank3';
	opt.id = 'enemyY_' +  TankWar.enemies[opt.type].leftNum;
	opt.speed = TankWar.enemies[opt.type].speed;
	opt.pos = TankWarFactory.createPosOfEnemy(opt.type);
	EnemyYTank.superclass.constructor.call(this, opt);
};

extend(EnemyYTank, EnemyTank);

var EnemyGTank = function() {
	this.bloods = 4;
	this.hurtPart = '';
	var opt = {};
	opt.type = 'g';
	opt.bgPic = 'tank4';
	opt.id = 'enemyG_' +  TankWar.enemies[opt.type].leftNum;
	opt.speed = TankWar.enemies[opt.type].speed;
	opt.pos = TankWarFactory.createPosOfEnemy(opt.type);
	EnemyGTank.superclass.constructor.call(this, opt);
};

extend(EnemyGTank, EnemyTank);

EnemyGTank.prototype.isShot = (function() { // 我设计的NX坦克，呵呵，打他会还手的 :)
	var counterattack = function() {
		this.move(this.hurtPart)
		this.fire();
	};
	var methods = [
		function() { EnemyGTank.superclass.isShot.call(this, this.by); },
		function() { $('#' + this.id).css('background-image', 'url(images/etank/tank4-3.png)'); counterattack.call(this); },
		function() { $('#' + this.id).css('background-image', 'url(images/etank/tank4-2.png)'); counterattack.call(this); },
		function() { $('#' + this.id).css('background-image', 'url(images/etank/tank4-1.png)'); counterattack.call(this); }
	]
	return function(by, direction) {
		this.by = by;
		this.hurtPart = direction;
		methods[--this.bloods].call(this);
	}
})();

var BulletClass = function(opt) {
	this.tank = opt.tank;
	this.id = 'bullet_' + this.tank.id;
	if ($('#' + this.id).length > 0) return; // 屏幕只有同一坦克的一颗子弹
	this.speed = opt.speed;
	this.top = this.tank.top;
	this.left = this.tank.left;
	this.init();
	this.fire();
};

BulletClass.prototype = {
	fire: function() {
		var $bullet = $('#' + this.id),
			towards = this.tank.movingToward,
			dir = TankWar.util.direction[towards],
			speed = this.speed * TankWar.util.plus_minus[towards],
			that = this,
			barriers = TankWar.barrier.normalBlocks.concat((this instanceof PlayerBullet) ? TankWar.barrier.enemies : TankWar.barrier.players),
			isHit = function() {
				var _l = this.left, _t = this.top;
				for (var i = 0, len = barriers.length; i < len; i++) {
					if (this.tank.id !== barriers[i].id ) {
						switch (towards) {
							case 'up': if (_t < 0) return true; if (_l + 20 > barriers[i].left && _l - 20 < barriers[i].left && _t + 25 > barriers[i].top && _t - 25 < barriers[i].top) { barriers[i].isShot(this.tank, 'down'); return true; } break;
							case 'down': if (_t > 525) return true; if (_l + 20 > barriers[i].left && _l - 20 < barriers[i].left && _t + 20 > barriers[i].top && _t - 20 < barriers[i].top) { barriers[i].isShot(this.tank, 'up'); return true; } break;
							case 'left': if (_l < 0) return true; if (_l + 25 > barriers[i].left && _l - 25 < barriers[i].left && _t + 20 > barriers[i].top && _t - 20 < barriers[i].top) { barriers[i].isShot(this.tank, 'right'); return true; } break;
							case 'right': if (_l > 660) return true; if (_l + 25 > barriers[i].left && _l - 25 < barriers[i].left && _t + 20 > barriers[i].top && _t - 20 < barriers[i].top) { barriers[i].isShot(this.tank, 'left'); return true; } break;
						}
					}
				}
				return false;
			},
			timr = setInterval(function() {
				if (TankWar.state.exit) { clearInterval(timr); return; }
				if (TankWar.state.pause) return;
				if (isHit.call(that)) {
					that.die();
					clearInterval(timr);
					return;
				}
				$bullet.css(dir, that[dir] += speed);
			}, 10);
	},
	clear: function() {
		return;
	},
	die: function() {
		this.explode('bullet', 6, true);
	},
	init: function() {
		$('<span id="' + this.id + '" class="bullet"></span>')
        	.css({
        		'background': 'url(images/bullet/bullet.png) 50% 50% no-repeat',
        		'top': this.top,
        		'left': this.left
        	})
        	.appendTo($('div#container'));
	}
};

var PlayerBullet = function(tank) {
	var opt = {};
	opt.tank = tank;
	opt.speed = TankWar.bullets.speed;
	PlayerBullet.superclass.constructor.call(this, opt);
};

extend(PlayerBullet, BulletClass);

var EnemyBullet = function(tank) {
	var opt = {};
	opt.tank = tank;
	opt.speed = TankWar.bullets.speed;
	EnemyBullet.superclass.constructor.call(this, opt);
};

extend(EnemyBullet, BulletClass);

var BlockClass = function(opt) {
	this.id = opt.id;
	this.top = opt.y;
	this.left = opt.x;
	this.type = opt.type;
	this.display();
};

BlockClass.prototype = {
	display: function() {
		var that = this;
		$('<div id="'+ this.id +'" class="block '+ this.type +'"></div>')
			.css({
				'background': 'url(images/map'+ TankWarMng.getScene() +'.png) '+ TankWar.util.block_bg_pos[this.type] +' no-repeat',
	    		'top': this.top,
	    		'left': this.left
	    	})
	    	.appendTo('div#container');
	},
	clear: function() {
		TankWar.barrier.normalBlocks.remove(this);
	},
	die: function() {
		this.clear();
		this.explode('mapbomb', 11);
	},
	isShot: function() {
		this.die();
	}
};

var BrickBlock = function(opt) {
	this.bloods = 2;
	opt.id = 'block_' + opt.x + opt.y;
	opt.type = opt.type;
	BrickBlock.superclass.constructor.call(this, opt);
};

extend(BrickBlock, BlockClass);

BrickBlock.prototype.isShot = (function() { // 砖头打两次调用 die
	var methods = [
		function() { BrickBlock.superclass.isShot.call(this); },
		function() { $('#' + this.id).css('background', 'url(images/map'+ TankWarMng.getScene() +'.png) 100% no-repeat'); }
	]
	return function() {
		methods[--this.bloods].call(this);
	}
})();

var StoneBlock = function(opt) {
	opt.id = 'block_' + opt.x + opt.y;
	opt.type = opt.type;
	StoneBlock.superclass.constructor.call(this, opt);
};

extend(StoneBlock, BlockClass);

StoneBlock.prototype.isShot = function() {
	return;
};

var WaterBlock = function(opt) {
	opt.id = 'block_' + opt.x + opt.y;
	opt.type = opt.type;
	WaterBlock.superclass.constructor.call(this, opt);
};

extend(WaterBlock, BlockClass);

WaterBlock.prototype.clear = function() {
	TankWar.barrier.waterBlocks.remove(this);
};

var BornBlock = function(opt) {
	opt.id = 'block_' + opt.x + opt.y;
	opt.type = opt.type;
	BornBlock.superclass.constructor.call(this, opt);
};

extend(BornBlock, BlockClass);

var KingBlock = function(opt) {
	opt.id = 'block_' + opt.x + opt.y;
	opt.type = opt.type;
	KingBlock.superclass.constructor.call(this, opt);
};

extend(KingBlock, BlockClass);

KingBlock.prototype.display = function() {
	$('<div class="king" id="'+ this.id +'"></div>')
		.css({
			'top': this.top,
			'left': this.left,
			'background': 'url(images/king.png) no-repeat',
			'z-index': 3
		})
		.appendTo($('#container'));
};

KingBlock.prototype.die = function() {
	this.clear();
	this.explode('bomb', 10);
	TankWarMng.kingDead();
};

var LawnBlock = function(opt) {
	opt.id = 'block_' + opt.x + opt.y;
	opt.type = opt.type;
	LawnBlock.superclass.constructor.call(this, opt);
};

extend(LawnBlock, BlockClass);

var PubMother = function() {};
PubMother.prototype = {
	explode: function(map, n) {
		var i = 1,
			$bomb = $('#' + this.id),
			top = $bomb.offset().top - 30,
			left = $bomb.offset().left - 35,
			timr = setInterval(function() {
				$bomb.css({
					'background': 'url(images/'+ map +'/'+ i +'.png) 50% 50% no-repeat',
					'width': '110px',
					'height': '91px',
					'top': top,
					'left': left
				});
				if (i == n || TankWar.state.exit) {
					clearInterval(timr);
					$bomb.remove();
				}
				if (TankWar.state.pause) return;
				i++;
			}, 50);
	}
};

augment([AbstractTank,BulletClass,BlockClass], PubMother); // 让每个对象都获得爆炸方法，js的“掺元类”用法