create database db_cryptoassistant;

use db_cryptoassistant;

create table events
	(event_id varchar(15) not null,
	client_msg_id varchar(100),
	type varchar(50),
	text varchar(1000),
	user varchar(50),
	ts varchar(50),
	team varchar(20),
	channel varchar(20),
	event_ts varchar(50),
	channel_type varchar(50),
	primary key (event_id)
	);


create table questions
	(question_id int not null auto_increment,
	token varchar(50),
	team_id varchar(20),
	api_app_id varchar(20),
	type varchar(50),
    event_id varchar(15),
	event_time long,
    event_time_parsed datetime,
    authed_users varchar(100),
	primary key(question_id)
	);


create table answers
	(answer_id int not null auto_increment,
    question_id int,
    token varchar(50),
    channel varchar(20),
    text varchar(1000),
    answer_date datetime,
    success boolean,
    error_given varchar(250),
    primary key(answer_id)
	);
    
    
create table subscriptions
	(subscription_id int not null auto_increment,
	user varchar(20),
    active boolean,
    token varchar(50),
    channel varchar(20),
    subscribe_date datetime,
    unsubscribe_date datetime,
    subscription_type varchar(1),
    primary key (subscription_id)
    );
    
create table candle_history
	(closetime int not null,
    closetime_date datetime,
    period varchar(15),
    openprice double,
    highprice double,
    lowprice double,
    closeprice double,
    volume double,
    primary key (closetime, period)
    );
    

commit;


select * from questions;
select * from events;
select * from answers;
select * from subscriptions;
select * from subscriptions where user = 'UFMLG4H1R';
select subscription_id, user, active, token, channel, subscribe_date, unsubscribe_date, subscription_type from subscriptions where user = 'UFMLG4H1R';
select user, token, channel from subscriptions where active = true;
select * from candle_history;

select * from candle_history where period = '1 Day' order by closetime_date desc;
select * from candle_history where period = '4 Hours' order by closetime_date desc;
select * from candle_history where period = '2 Hours' order by closetime_date desc;
select * from candle_history where period = '1 Hour' order by closetime_date desc;
select MAX(closetime) as closetime from candle_history where period = '1 Day';
select * from candle_history where period = '1 Day' and closetime_date BETWEEN DATE_SUB(NOW(), INTERVAL 20 DAY) AND NOW() order by closetime_date asc;
select * from candle_history where period = '1 Day' and closetime_date BETWEEN '2019-05-20' AND DATE_SUB('2019-05-20', INTERVAL 20 DAY) order by closetime_date asc;
select * from candle_history where period = '1 Day' and closetime_date >= CURDATE() - INTERVAL 45 DAY order by closetime_date asc;
select * from candle_history where period = '1 Day' and closetime_date <= '2019-05-20 22:00:00' AND CLOSETIME_DATE >= '2019-05-20 22:00:00'- INTERVAL 94 DAY order by closetime_date asc;
select * from candle_history where period = '1 Day' and closetime_date LIKE '2019-05-20%';
select closeprice from candle_history where period = '1 Day' order by closetime desc limit 1;
select * from candle_history where period = '1 Day' and closetime_date >= date('2019-05-20 21:00:00') order by closetime asc limit 1;

insert into questions (token, team_id, api_app_id, type, event_id, event_time, authed_users) values('ABC', '123', '123', '123', '123', sysdate(), '123');
insert into subscriptions (user, active, token, channel, subscribe_date, unsubscribe_date) values('UFMLG4H1R', true, '63K0nrlOxGus7f2EK5xqXKNP', 'DK8UYFXMH', sysdate(), null);


select CURDATE() - INTERVAL 30 DAY from dual;

delete from candle_history where period = '4 Hours' and closetime = 1562112000;
delete from candle_history where period = '2 Hours' and closetime = 1562104800;




drop table events;
drop table questions;
drop table answers;
drop table subscriptions;
drop table candle_history;