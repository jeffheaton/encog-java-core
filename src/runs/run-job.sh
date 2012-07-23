#!/bin/bash

. run-environment.sh

function logged_run() {
	date=$(date +%Y-%m-%d.%H.%M)
	($1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} > /home/nitbix/projects/mscproject/runs_output/${date}_$1.${BASHPID}.output)
}

logged_run bagging-haberman 3,30 150,300,1000 0.06 150 mlp:300:sigmoid &
logged_run bagging-haberman 1,10,100 150,300,1000 0.06 150 mlp:300:sigmoid &
