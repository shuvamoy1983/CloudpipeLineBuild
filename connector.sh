curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" 34.86.152.101:8083/connectors/ -d '
{  
   "name": "connector1", 
   "config": {
     "connector.class": "io.debezium.connector.postgresql.PostgresConnector", 
	 "plugin.name": "pgoutput",
	 "database.hostname": "34.86.48.103", 
	 "database.port": "5432", 
	 "database.user": "start_data_engineer", 
	 "database.password": "password", 
	 "database.dbname" : "start_data_engineer", 
	 "database.server.name": "postgres", 
	 "database.whitelist": "mydb"
	}
}'
