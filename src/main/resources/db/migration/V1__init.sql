create sequence money_costs_seq start with 1 increment by 50;
create sequence user_account_seq start with 1 increment by 50;
create table money_costs (category smallint check (category between 0 and 8), date_time timestamp(6), expenses bigint, id bigint not null, user_id bigint not null, primary key (id));
create table user_account (id bigint not null, email varchar(255) not null, name varchar(255), primary key (id));
alter table if exists money_costs add constraint FKliw0mdb64r8vti7ol52e4gcfu foreign key (user_id) references user_account on delete cascade;