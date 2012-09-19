var TankWarFactory = {
	createPlayerTank: function() {
		var tank = new PlayerTank();
		Interface.ensureImplements(tank, Tank);
		TankWar.barrier.players.push(tank);
		TankWarMng.setTankCount(tank.id, --tank.lives);
		TankWarMng.setScore(tank, 0);
	},
	createEnemyTank: (function() {
		// Private static check type of enemies.
		function checkType(type) {
			var types = TankWar.enemies.types.clone();
			if (!type) type = 'r';
			if (TankWar.enemies[type].leftNum > 0) return type;
			types.remove(type);
			for (var i = 0, len = types.length; i < len; i++) {
				if (TankWar.enemies[types[0]].leftNum === 0) {
					types.remove(types[0]);
				} else {
					return types[0];
				}
			}
			return false;
		}
		return function(type) {
			var tank;
			type = checkType(type);
			if (!type) throw new Error('No enemies alive.');
			switch(type) {
				case 'r': tank = new EnemyRTank(); break;
				case 'b': tank = new EnemyBTank(); break;
				case 'y': tank = new EnemyYTank(); break;
				case 'g': tank = new EnemyGTank(); break;
			}
			Interface.ensureImplements(tank, Tank);
			TankWar.barrier.enemies.push(tank);
			TankWarMng.setTankCount(tank.id, --TankWar.enemies[type].leftNum);
		}
	})(),
	createBullet: function(tank) {
		var bullet;
		if (tank instanceof PlayerTank) {
			bullet = new PlayerBullet(tank);
		} else {
			bullet = new EnemyBullet(tank);
		}
		Interface.ensureImplements(bullet, Bullet);
	},
	createBlock: function(param) {
		var block;
		switch(param.type) {
			case 'e': block = new BrickBlock(param); TankWar.barrier.normalBlocks.push(block); break;
			case 'h': block = new StoneBlock(param); TankWar.barrier.normalBlocks.push(block); break;
			case 'k': block = new KingBlock(param); TankWar.barrier.normalBlocks.push(block); break;
			case 'w': block = new WaterBlock(param); TankWar.barrier.waterBlocks.push(block); break;
			case 'b': block = new BornBlock(param); TankWar.enemies.posBorn.push({x:block.left,y:block.top,avaliable:true}); break;
			case 'l': block = new LawnBlock(param); break;
		}
		Interface.ensureImplements(block, Block);
	},
	createPosOfEnemy: function(type) {
		for (var i = 0, len = TankWar.enemies.posBorn.length; i < len; i++) {
			if (TankWar.enemies.posBorn[i].avaliable) {
				TankWar.enemies.posBorn[i].avaliable = false;
				return {x: TankWar.enemies.posBorn[i].x,y: TankWar.enemies.posBorn[i].y,bid: i};
			}
		}
		throw new Error('None of born place for EnemyTank!');
	}
};