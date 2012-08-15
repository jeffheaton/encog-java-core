#!/bin/bash
problem=$1
neurons=$2
setsize=$3
fixedline=$4
pause=$5

. run-environment.sh

function training() {
  java -cp build/classes ensembles/TrainingCurves bagging problems/${problem} ${setsize} 0.3 rprop mlp:${neurons}:sigmoid 200
	pid=$!
#	sleep $pause
#  kill $pid
}

training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-1.data
training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-2.data
training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-3.data

cat ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-1.data ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-2.data ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-3.data | sort -n > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data

./make_training_error_graph_only.sh $problem $neurons $fixedline
