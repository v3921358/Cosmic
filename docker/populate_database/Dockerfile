# Quick Start MySQL Database Populator for DietStory
FROM mysql:8.0.18

# Create database, create tables, drop, shops in order. Apply updates after
CMD cd /mnt \
	&& mysql -h${MYSQL_HOST_NAME} -uroot -p${MYSQL_ROOT_PASSWORD} < sql/db_database.sql \
	&& mysql -h${MYSQL_HOST_NAME} -uroot -p${MYSQL_ROOT_PASSWORD} < sql/db_drops.sql \
	&& mysql -h${MYSQL_HOST_NAME} -uroot -p${MYSQL_ROOT_PASSWORD} < sql/db_shopupdate.sql \
	&& find ./sql/*_db_changes -iname '*.sql' | awk '{ print "source",$0 }' | mysql --batch -h${MYSQL_HOST_NAME} -uroot -p${MYSQL_ROOT_PASSWORD}