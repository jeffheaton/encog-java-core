#!/bin/bash
problem=$1
problem_label=`echo $problem | sed 's/.*_//'`
neurons=$2
training=$3
fixedline=$4

gnuplot <<EOF
set title "Training Error ($problem_label - $neurons neurons - $training)"
set xlabel "i"
set ylabel "error"
plot "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.data" using 1:2 with lines title "MSE","~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.data" using 1:3 with lines title "test misclass.","~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.data" using 1:4 with lines title "train misclass.",$fixedline
pause 10
set terminal postscript eps enhanced color  
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.eps"
replot
set term png
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.png"
replot
EOF
