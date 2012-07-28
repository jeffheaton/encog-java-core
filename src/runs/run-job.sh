#!/bin/bash

. run-environment.sh

#logged_run bagging-haberman 3,30 150,300,1000 0.06 150 mlp:300:sigmoid &
#logged_run bagging-haberman 1,10,100 150,300,1000 0.06 150 mlp:300:sigmoid &
logged_run boosting-landsat 3,30 5000 0.3,0.2,0.1 5000 mlp:40:sigmoid false &
logged_run boosting-landsat 1,10,100 5000 0.3,0.2,0.1 5000 mlp:40:sigmoid false &
logged_run bagging-landsat 3,30 5000 0.3,0.2,0.1 5000 mlp:40:sigmoid false &
logged_run bagging-landsat 1,10,100 5000 0.3,0.2,0.1 5000 mlp:40:sigmoid false &
