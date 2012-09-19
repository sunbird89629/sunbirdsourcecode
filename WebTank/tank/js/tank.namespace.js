var TankWar = {
	param: {
		level: 1,
		enemyNumOfLevel: config.enemy_number_of_level,
		playerDeadNum: 0,
		scene: config.default_scene
	},
	state: {
		exit: false,
		pause: false
	},
	players: {
		num: 1,
		p1: {
			lives: config.player1_lives,
			pos: {
				x: 242,
				y: 510
			},
			speed: config.player1_speed,
			keys: {
				moveKeys: config.player1_move_keys,
				fireKey: config.player1_fire_key
			}
		},
		p2: {
			lives: config.player2_lives,
			pos: {
				x: 420,
				y: 510
			},
			speed: config.player2_speed,
			keys: {
				moveKeys: config.player2_move_keys,
				fireKey: config.player2_fire_key
			}
		}
	},
	enemies: {
		types: ['r','b','y','g'],
		posBorn: [],
		r: {
			leftNum: 0,
			speed: config.enemy_red_speed
		},
		b: {
			leftNum: 0,
			speed: config.enemy_blue_speed
		},
		y: {
			leftNum: 0,
			speed: config.enemy_yellow_speed
		},
		g: {
			leftNum: 0,
			speed: config.enemy_green_speed
		}
	},
	bullets: {
		speed: config.bullet_speed
	},
	barrier: { // 运行时容器，由于面向接口，可以统一调用接口方法，如 xx[i].isShot()
		players: [],
		enemies: [],
		normalBlocks: [],
		waterBlocks: [],
		deadPlayers: []
	},
	util: {
		bg_pos_key: {'up':'0%','down':'33%','left':'66%','right':'99%'},
		direction: {'up':'top','down':'top','left':'left','right':'left'},
		plus_minus: {'up':-1,'left':-1,'down':1,'right':1},
		type_score: {'r':100,'b':200,'y':300,'g':400},
		block_bg_pos: {'e':'14.29%','h':'28.57%','w':'57.14%','l':'71.30%','b':'85.71%'},
		level_bg_pos: ['11.1%','22.2%','33.3%'],
		score_bg_pos: [{p:'0%',w:17},{p:'12%',w:16},{p:'22.2%',w:16},{p:'33.3%',w:16},{p:'44.4%',w:16},{p:'55.5%',w:16},{p:'66.6%',w:16},{p:'77.7%',w:16},{p:'88.5%',w:16},{p:'99.9%',w:16}],
		p_bg_pos: [{p:'0%',w:15},{p:'12%',w:14},{p:'22.2%',w:15},{p:'33.3%',w:14},{p:'44.4%',w:14},{p:'55.5%',w:14},{p:'66.6%',w:14},{p:'77.7%',w:14},{p:'88.5%',w:14},{p:'99.9%',w:15}],
		l_bg_pos: [{p:'0%',w:19},{p:'12%',w:17},{p:'22.2%',w:19},{p:'33.3%',w:18},{p:'44.4%',w:18},{p:'55.5%',w:18},{p:'66.6%',w:18},{p:'77.7%',w:18},{p:'88.5%',w:18},{p:'99.9%',w:19}]
	},
	developModel: config.develop_model,
	mySite: config.my_site,
	maps: [],
	resources: { // 需要预加载的图片和地图
		'images': {
			'bomb': {
				'urls': ['1-10.png']
			},
			'bullet': {
				'urls': ['1-6.png', 'bullet.png']
			},
			'etank': {
				'urls': ['tank1.png', 'tank2.png', 'tank3.png', 'tank4.png', 'tank4-1.png', 'tank4-2.png', 'tank4-3.png']
			},
			'info': {
				'urls': ['Arrow.png', 'bgrnd.bmp', 'continue.bmp', 'gomenu.bmp']
			},
			'levelwin': {
				'urls': ['bgrnd.bmp', 'btn.png', 'choose.png']
			},
			'mapbomb': {
				'urls': ['1-11.png']
			},
			'modewin': {
				'urls': ['about.bmp', 'exit.bmp', 'help0.bmp', 'help.bmp', 'help-back.bmp', 'logout.bmp', 'option.bmp', 'star.bmp', 'star2.bmp']
			},
			'optionwin': {
				'urls': ['bgrnd.bmp', 'checkbox1.png', 'checkbox2.png', 'confirm.bmp', 'exit.bmp', 'key.bmp']
			},
			'urls': [
				'bgrnd1.bmp',
				'bgrnd2.bmp',
				'bgrnd3.bmp',
				'exit.png',
				'fail.png',
				'gameover.bmp',
				'king.png',
				'level.png',
				'levelnumber.png',
				'map1.png',
				'map2.png',
				'map3.png',
				'map4.png',
				'presscontinue.png',
				'scorenum.png',
				'tank.png',
				'tankcount.png',
				'totalnum.png',
				'win.png'
			]
		},
		'maps': {
			'urls': ['1-3.json']
		}
	}
};