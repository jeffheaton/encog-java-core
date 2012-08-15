#!/bin/bash
problem=$1
neurons=$2
fixedline=$3

gnuplot <<EOF
set title "Training Error ($problem - $neurons neurons)"
set xlabel "i"
set ylabel "error"
plot "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data" using 1:2 with lines title "MSE","~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data" using 1:3 with lines title "test misclass.","~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.data" using 1:4 with lines title "train misclass.",$fixedline
pause 10
set terminal postscript eps enhanced color  
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.eps"
replot
set term png
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}.png"
replot
EOF
