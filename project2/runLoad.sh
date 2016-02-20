#! /bin/bash

# Declare which table is to be used
TABLE="CS144";

# Drop tables in the database with the drop.sql script
mysql $TABLE < drop.sql

# Create all the relevant tables in the databse with create.sql
mysql $TABLE < create.sql

# Run ant command to build, parse xml, create load files
ant
ant run-all

# Load data into MySQL
mysql $TABLE < load.sql

# Delete the files created by the parser
rm *.dat
rm -rf bin
