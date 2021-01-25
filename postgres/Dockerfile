FROM postgres:13.0
COPY init-postgres-db.sh /docker-entrypoint-initdb.d/init-postgres-db.sh
CMD ["docker-entrypoint.sh", "postgres"]