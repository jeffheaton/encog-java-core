#!/bin/bash
problem=$1
technique=$2
neurons=$3
fixedline=$4

gnuplot <<EOF
set title "Training Error ($technique - $problem - $neurons neurons)"
set xlabel "i"
set ylabel "MSE"
plot "~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-1.data" with lines title "1","~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-2.data" with lines title "2","~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}-3.data" with lines title "3",$fixedline
pause 10
set term epslatex color 
set out "~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}.tex"
replot
set term png
set out "~/projects/mscproject/data_plots/training_curves/${technique}-${problem}-${neurons}.png"
replot
EOF
