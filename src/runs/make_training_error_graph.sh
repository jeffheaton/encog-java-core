#!/bin/bash
problem=$1
neurons=$2
setsize=$3
training=$4
fixedline=$5
pause=$6
howmany=200

if [[ "$7" != "" ]]; then
  howmany=$7
fi

if [[ "$ENCOG_ENV_LOADED" == "" ]]; then
  . run-environment.sh
fi

function training() {
  java -cp build/classes ensembles/TrainingCurves bagging problems/${problem} ${setsize} 0.3 ${training} mlp:${neurons}:sigmoid ${howmany}
	pid=$!
#	sleep $pause
#  kill $pid
}

training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}-1.data
training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}-2.data
training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}-3.data

cat ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}-1.data ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}-2.data ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}-3.data | sort -n > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.data

./make_training_error_graph_only.sh $problem $neurons $training $fixedline
