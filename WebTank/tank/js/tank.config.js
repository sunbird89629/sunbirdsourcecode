var config = {};
config.my_site = ''; // 如果将此游戏放在您网站上，请配置网址如：config.my_site = 'http://www.mysite.com/tank'，以获得更好的游戏体验

config.develop_model = 'product'; // develop | test | product 如果是product，将不进行接口检查，以提高游戏速度

config.enemy_number_of_level = [{r:5,b:3,y:2,g:1},{r:10,b:5,y:3,g:2},{r:15,b:5,y:5,g:5}]; // 每一关的敌方坦克数量

config.default_scene = 'lawn'; // 默认场景

// 游戏参数
config.player1_lives = 4;
config.player1_speed = 2;
config.player1_move_keys = { 37: 'left', 38: 'up', 39: 'right', 40: 'down'};
config.player1_fire_key = 32;

config.player2_lives = 4;
config.player2_speed = 2;
config.player2_move_keys = { 65: 'left', 87: 'up', 68: 'right', 83: 'down'};
config.player2_fire_key = 71;

config.enemy_red_speed = 1;
config.enemy_blue_speed = 1.5;
config.enemy_yellow_speed = 2;
config.enemy_green_speed = 2.5;
config.bullet_speed = 10;