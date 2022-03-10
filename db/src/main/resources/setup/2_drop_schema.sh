#!/bin/bash

psql -U postgres -h localhost -d registrar -f 2_drop_schema.sql
