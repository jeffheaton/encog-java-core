#!/bin/bash

. run-environment.sh

#logged_run bagging-haberman 3,30 150,300,1000 0.06 150 mlp:300:sigmoid &
#logged_run bagging-haberman 1,10,100 150,300,1000 0.06 150 mlp:300:sigmoid &
logged_run boosting-haberman 3,30 150,300,1000 0.06 150 mlp:300:sigmoid &
logged_run boosting-haberman 1,10,100 150,300,1000 0.06 150 mlp:300:sigmoid &
