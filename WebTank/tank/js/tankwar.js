var TankWarMng = {
	init: function() {
		$('#progress').fadeIn('slow', function() {
			var that = $(this);
			new PreLoad(TankWar.resources, function() {
				that.fadeOut('slow', function() {
					TankWarMng.initMenu();
				});
			});
		});
	},
	initMenu: function() {
		TankWarMng.clear();
		$('#home').find('*').show().end().fadeIn('slow');
		$('#star,#star2,#help,#exit,#option,#helpCont .back, .confirm, .cancel, .level_1, .level_2, .level_3').hover(function() {
			$(this).css('background-position','25%');
		}, function() {
			$(this).css('background-position','0%');
		});
		$('#star,#star2,#help,#exit,#option,#helpCont .back, .confirm, .cancel, .level_1, .level_2, .level_3').mousedown(function() {
			$(this).css('background-position','50%');
		});
		$('#star,#star2').mouseup(function() {
			TankWar.players.num = $(this).attr('class');
			$('#home > div').hide();
			$('#home').slideUp();
			$('#levelWin').slideDown(function() {
				$('div', $(this)).show();
			});
		});
		$('#help').mouseup(function() {
			$('#home').fadeOut('fast', function() {
				$('#helpCont').fadeIn('fast');
			});
		});
		$('#helpCont .back').mouseup(function() {
			$('#helpCont').fadeOut('fast', function() {
				$('#home').fadeIn('fast');
			});
		});
		$('#option').mouseup(function() {
			$('#optionWin').animate({height: 403, width: 319, top: 190, left: 487},'1000').find('*').show();
			$('#optionWin #scene > div.'+ TankWar.param.scene).siblings().removeClass('checked').end().addClass('checked');
		});
		$('#optionWin #scene > div').click(function() {
			$(this).siblings().removeClass('checked').end().addClass('checked');
		});
		$('#optionWin .confirm').click(function() {
			TankWar.param.scene = $('#scene > .checked').attr('class').split(' ').shift();
			$('#optionWin').animate({height: 0, width: 0, top: 588, left: 800},'1000').find('*').hide();
		});
		$('#optionWin .cancel').click(function() {
			$('#optionWin').animate({height: 0, width: 0, top: 588, left: 800},'1000').find('*').hide();
		});
		$('#exit').click(function() {
			window.open('','_parent','');	     
			window.close(); 
		});
		$('#levelWin .iss').hover(function() {
			$('#levelWin .iss').removeClass('selected');
			$(this).addClass('selected');
		}).click(function() {
			TankWar.param.level = $(this).parent().attr('class').split('_')[1];
			$('#levelWin').hide().find(' > div').hide();
			TankWarMng.initGame();
		});
	},
	initGame: function() {
		TankWarMng.clear();
		var init = function() {
			getMap(TankWar.param.level, function(map) {
				$.each(map, function(i, n) {
					TankWarFactory.createBlock(n);
				});
				setTimeout(function() {
					$.each(TankWar.enemies.types, function(i, n) {
						TankWar.enemies[n].leftNum = TankWar.param.enemyNumOfLevel[TankWar.param.level-1][n];
						TankWarMng.setTankCount('enemy' + n.toUpperCase(), TankWar.enemies[n].leftNum);
					});
					for (var i = 1; i <= TankWar.players.num; i++) {
						TankWarFactory.createPlayerTank();
					}
					for (var j = 0, len = $('.b').length; j < len; j++) {
						TankWarFactory.createEnemyTank();
					}
				}, 1000);
			});
		},
		getMap = function(index, callback) {
			if (TankWar.maps[index-1])
				callback(TankWar.maps[index-1]);
			else
				$.getJSON('maps/'+ index +'.json', function(map) {
					TankWar.maps[index-1] = map;
					callback(map);
				});
		};
		TankWarMng.initParam();
		$('#container').css('background','url(images/bgrnd'+ TankWarMng.getScene() +'.bmp) no-repeat');
		$('#ln').css('background','url(images/levelnumber.png) '+ TankWar.util.level_bg_pos[TankWar.param.level-1] +' no-repeat');
		$('#startGame').show(function() {
			$('#related').show(function() {
				$('#level').css({'opacity': 1, 'top': '200px'});
				$('#level').animate({ opacity: 0, top: '100px' }, 1000);
				TankWarMng.setLevel(TankWar.param.level);
				init();
			});
			TankWarMng.initGamePage();
		});
	},
	initParam: function() {
		TankWar.state.pause = false;
		TankWar.state.exit = false;
		TankWar.param.playerDeadNum = 0;
		TankWarMng.getScene('refresh');
	},
	initGamePage: function() {
		// exit or pause
		var showInfo = function(_s) {
			$('#info').show();
			$('#info .select').removeClass('selected');
			$('#'+ _s +' .select').addClass('selected');
			TankWar.state.pause = true;
		};
		
		// continue or exit
		var processInfo = function() {
		  	$('#info').hide();
			if ($('#info .selected').parent().attr('id') == 'continue') {
		  		TankWar.state.pause = false;
		 	} else {
				TankWarMng.backToMenu();
			}
		}
		$('#goHome, #goMenu, #continue').hover(function() {
			$(this).css('background-position','25%');
		}, function() {
			$(this).css('background-position','0%');
		});
		$('#goHome, #goMenu, #continue').mousedown(function() {
			$(this).css('background-position','50%');
		});
		$('#goMenu, #continue').hover(function() {
			$('.select').removeClass('selected');
			$(this).find(' > .select').addClass('selected');
		});
		$('#goHome').mouseup(function() {
			showInfo('goMenu');
		});
		$('#goMenu, #continue').mouseup(function() {
	 		processInfo();
	 	});
	 	$(document).keydown(function(e){
	 		e = e || window.event;
	 		var key = e.which || e.keyCode;
	 		switch (key) {
		 		case 27: showInfo('goMenu'); break;
		        case 38: if ($('#info:visible').length == 1) $('#info .select').toggleClass('selected'); break;
		        case 40: if ($('#info:visible').length == 1) $('#info .select').toggleClass('selected'); break;
		        case 13: if ($('#info:visible').length == 1) processInfo(); break;
	    	}
	 	});
	 	// info menu shows on blur
	 	$(window).blur(function() {
	 		if(document.activeElement == document.body)
	 			showInfo('continue');
	 	});
	},
	backToMenu: function() {
		TankWarMng.clear();
		$('#startGame').slideUp();
		$('#home').slideDown(function() {
			TankWarMng.initMenu();
		});
		
	},
	clear: function() {
		TankWar.state.exit = true;
		$(document).unbind();
		$(window).unbind();
		$('*').unbind();
		$('#container *').remove();
		$('#related').hide();
		$('#tc_p1, #tc_p2, #score_p1, #score_p2, #tc_enemyR, #tc_enemyB, #tc_enemyY, #tc_enemyG').html('');
		var blocks = TankWar.barrier.players.concat(TankWar.barrier.deadPlayers).concat(TankWar.barrier.enemies).concat(TankWar.barrier.normalBlocks).concat(TankWar.barrier.waterBlocks);
		for (var i = 0, len = blocks.length; i < len; i++) {
			blocks[0].clear();
			blocks.remove(blocks[0]);
		}
		TankWar.enemies.posBorn = [];
	},
	onePlayerDead: function() {
		if (++TankWar.param.playerDeadNum == TankWar.players.num) {
			setTimeout(function() {
				TankWarMng.gameover('fail');
			},1000);
		}
	},
	oneEnemyDead: (function() {
		var isAnyEnemies = function() {
			for (var i = 0, len = TankWar.enemies.types.length; i < len; i++) {
				if (TankWar.enemies[TankWar.enemies.types[i]].leftNum) {
					return true;
				}
			}
			return false;
		}
		return function(by, type) {
			by.killOne(type);
			TankWarMng.setScore(by, TankWar.util.type_score[type]);
			if (isAnyEnemies()) {
				setTimeout(function() {
					try { TankWarFactory.createEnemyTank(); } catch(e) { alert('Something goes wrong when create an enemy tank.'); }
				}, 1000);
			} else if (!TankWar.barrier.enemies.length) {
				setTimeout(function() {
					TankWarMng.gameover('win');
				},1000);
			}
		}
	})(),
	kingDead: function() {
		for (var i = 0, len = TankWar.barrier.players.length; i < len; i++) {
			TankWar.barrier.players[0].lives = 1;
			TankWar.barrier.players[0].die(true);
		}
		setTimeout(function() {
			TankWarMng.gameover('fail');
		},1000);
	},
	setScore: function(tank, n) {
		var $score, _p, _w, _i;
		if (typeof tank === 'object') {
			$score = $('#score_' + tank.id);
			_n = TankWarMng.getScore(tank) + '';
		} else {
			$score = $('#' + tank);
			_n = n + '';
		}
		$score.html('');
		for (var i = 0, len = _n.length; i < len; i++) {
			_i = _n.charAt(i);
			_p = TankWar.util.score_bg_pos[_i].p;
			_w = TankWar.util.score_bg_pos[_i].w;
			$('<div></div>')
				.css({
					'float': 'left',
					'background': 'url(images/scorenum.png) '+ _p +' no-repeat',
					'height': '25px',
					'width': _w
				})
				.appendTo($score);
		}
	},
	getScore: function(tank) {
		var score = 0, type;
		for (var i = 0, len = TankWar.enemies.types.length; i < len; i++) {
			type = TankWar.enemies.types[i];
			score += tank.kills[type] * TankWar.util.type_score[type];
		}
		return score;
	},
	setTankCount: function(pid, n) {
		var $tankcount = $('#tc_'+ pid.split('_')[0]), _n = n + '', _p, _w, _i;
		$tankcount.html('');
		for (var i = 0, len = _n.length; i < _n.length; i++) {
			_i = _n.charAt(i);
			_p = TankWar.util.p_bg_pos[_i].p;
			_w = TankWar.util.p_bg_pos[_i].w;
			$('<div></div>')
				.css({
					'float': 'left',
					'background': 'url(images/tankcount.png) '+ _p +' no-repeat',
					'height': '25px',
					'width': _w
				})
				.appendTo($tankcount);
		}
	},
	setLevel: function(n) {
		var $level = $('#round'), _n = n + '', _p, _w, _i;
		$level.html('');
		for (var i = 0, len = _n.length; i < _n.length; i++) {
			_i = _n.charAt(i);
			_p = TankWar.util.l_bg_pos[_i].p;
			_w = TankWar.util.l_bg_pos[_i].w;
			$('<div></div>')
				.css({
					'float': 'left',
					'background': 'url(images/totalnum.png) '+ _p +' no-repeat',
					'height': '30px',
					'width': _w,
					'left': '750px'
				})
				.appendTo($level);
		}
	},
	getScene: (function() {
		var randomScene;
		function refrsh() {
			randomScene = Math.round(Math.random()*2+1);
		}
		return function(isRfrsh) {
			if (isRfrsh === 'refresh') refrsh(); // 重新进入游戏时，刷新静态私有属性randomScene的值
			switch (TankWar.param.scene) {
		      case 'lawn': return 1; break;
		      case 'snowfield': return 2; break;
		      case 'sandlot': return 3; break;
		      case 'random': return randomScene; break;
			}
		}
	})(),
	gameover: function(result) {
		$.each(TankWar.barrier.players.concat(TankWar.barrier.deadPlayers), function(i, n) {
			$.each(TankWar.enemies.types, function(j, m) {
				TankWarMng.setScore(n.id + m, n.kills[m] * TankWar.util.type_score[m]);
			});
			TankWarMng.setScore(n.id + 'Ttl', TankWarMng.getScore(n));
		});
		TankWarMng.clear();
		$('#startGame').slideUp();
		$('#gameover').find('*').hide().end().slideDown(function() {
			$('#wOrl').css('background', 'url(images/' + result + '.png) no-repeat');
			$('#gameover *').show();
			var timr = setInterval(function() {
				$('#anyKey').toggleClass('presscontinue');
			}, 500);
			$(document).keydown(function() {
				clearInterval(timr);
				$('#gameover *').hide();
				if (result === 'win' && TankWar.param.level++ !== 3) {
					$('#gameover').fadeOut(TankWarMng.initGame);
				} else {
					$('#gameover').fadeOut(TankWarMng.initMenu);
				}
			});
		});
	}
};