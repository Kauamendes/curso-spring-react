create table lancamento (id bigint not null auto_increment, ano integer, data_cadastro datetime(6), descricao varchar(255), mes integer, status default 'PENDENTE', tipo varchar(255), valor decimal(19,2), id_usuario bigint, primary key (id)) engine=InnoDB;
create table usuario (id bigint not null auto_increment, email varchar(255), nome varchar(255), senha varchar(255), primary key (id)) engine=InnoDB;
alter table lancamento add constraint FKt2a5b4jc8powehfmsyeufarkr foreign key (id_usuario) references usuario (id);
