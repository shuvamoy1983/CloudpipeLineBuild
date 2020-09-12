## Add your table 
create database mydb;
CREATE TABLE mydb.emp ( id int, Name varchar(20), Salary int );
CREATE TABLE mydb.dept ( DEPT_ID int, Dept_Name varchar(20), EMP_ID int );
GRANT DROP,CREATE,ALTER,SELECT,INSERT,UPDATE,DELETE,LOCK TABLES,EXECUTE ON *.* TO mysql@'%';
