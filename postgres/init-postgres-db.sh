#!/bin/bash
set -e

echo "init db"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE "bank-users";
    GRANT ALL PRIVILEGES ON DATABASE "bank-users" TO "postgres";

    CREATE DATABASE "bank-market";
    GRANT ALL PRIVILEGES ON DATABASE "bank-market" TO "postgres";
EOSQL