#!/bin/bash
problem=$1
neurons=$2
setsize=$3
fixedline=$4
pause=$5

. run-environment.sh

function training() {
  java -cp build/classes ensembles/TrainingCurves bagging ${problem} ${setsize} mlp:${neurons}:sigmoid &
	pid=$!
	sleep $pause
  kill $pid
}

training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-1.data
training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-2.data
training > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-3.data

cat ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-1.data ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-2.data ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-3.data | sort -n > ~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data

gnuplot <<EOF
set title "Training Error ($problem - $neurons neurons)"
set xlabel "i"
set ylabel "error"
plot "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data" with lines using 1:2 title "MSE","~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data" with lines using 1:3 title "test misclass.","~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data" with lines using 1:4 title "train misclass.",$fixedline
pause 10
set term epslatex color 
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.tex"
replot
set term png
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.png"
replot
EOF
