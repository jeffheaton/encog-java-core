#!/bin/bash
problem=$1
technique=$2
neurons=$3
setsize=$4
fixedline=$5
pause=$6

. run-environment.sh

function training() {
	${technique}-${problem} 1 $setsize 0.01 $setsize mlp:${neurons}:sigmoid true &
	pid=$!
	sleep $pause
	kill $pid
}

training > ~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-1.data
training > ~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-2.data
training > ~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-3.data

gnuplot <<EOF
set title "Training Error ($technique - $problem - $neurons neurons)"
set xlabel "i"
set ylabel "MSE"
plot "~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-1.data" with lines title "1","~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-2.data" with lines title "2","~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-3.data" with lines title "3",$fixedline
pause 10
set term post enh
set out "~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}.eps"
replot
set term png
set out "~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}.png"
replot
EOF
