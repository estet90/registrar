#!/bin/bash

psql -U registrar -h localhost -d registrar -f 3_create_schema.sql
