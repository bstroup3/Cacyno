start transaction;

create user if not exists 'admin'@'localhost' identified by 'password';
grant all privileges on *.* to 'admin'@'localhost';

use cacyno;

create table game(
id varchar(50) NOT NULL,
user_id varchar(10) NOT NULL,
isSpectator boolean,
public_card1 int,
public_card2 int,
public_card3 int,
public_card4 int,
public_card5 int,
pot int,
primary key (id),
foreign key (user_id) references user(id) ON DELETE CASCADE
);

create table players(
game_id varchar(50), 
user_id varchar(50),
current_game_money int,
card1 int,
card2 int,
folded boolean,
primary key (user_id, game_id),
foreign key (user_id) references user(id)ON DELETE CASCADE,
foreign key (game_id) references game(id) ON DELETE CASCADE
);
