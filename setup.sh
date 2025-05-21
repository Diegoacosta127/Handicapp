#!/bin/bash
DB_NAME="handicapp"
DB_USER=$(whoami)

# Check if PostgreSQL is installed
if ! command -v psql > /dev/null;
then
    echo "Error: psql not installed or not in PATH."
    exit 1
fi

# Create the database if it doesn't exist
if ! psql -U "$DB_USER" -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME";
then
    createdb -U "$DB_USER" "$DB_NAME"
    echo "Database $DB_NAME created."
else
    echo "Database $DB_NAME already exists."
fi

# createdb -U "$DB_USER" "$DB_NAME" 2>/dev/null || echo "Database $DB_NAME already exists or cannot be created."

# Import schema and data into the database from SQL file
psql -U "$DB_USER" -d "$DB_NAME" < database/handicapp.sql || {
    echo "Error importing SQL dump."
    exit 1
}

# Execute the Java program
java -cp "dist/Handicapp.jar:dist/lib/postgresql.42.7.4.jar" handicapp.JFrame_Main
