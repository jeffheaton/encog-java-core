#!/bin/bash
problem=$1
problem_label=`echo $problem | sed 's/.*_//'`
neurons=$2
training=$3
fixedline=$4

gnuplot <<EOF
set style line 1 lc rgb '#8b1a0e' lw 2 # --- red                                                                                                                            
set style line 2 lc rgb '#5e9c36' lw 2 # --- green                                                                                                                          
set style line 3 lc rgb '#993300' lw 2 # --- orange                                                                                                                         
set style line 4 lc rgb '#003366' lw 2 # --- blue                                                                                                                           
set style line 11 lc rgb '#808080' lt 1                                                                                                                                     
set border 3 back ls 11                                                                                                                                                     
set tics nomirror                                                                                                                                                           
set style line 12 lc rgb '#808080' lt 0 lw 1                                                                                                                                
set grid back ls 12  
set title "Training Error ($problem_label - $neurons neurons - $training)"
set xlabel "i"
set ylabel "error"
plot "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.data" using 1:2 with lines title "train MSE",'' using 1:3 with lines title "test MSE", '' using 1:4 with lines title "test misclass.",'' using 1:5 with lines title "train misclass.",$fixedline
pause 10
set term png
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.png"
replot
set terminal postscript eps enhanced color size 4,2.5
set out "~/projects/mscproject/data_plots/training_curves/${problem}-${neurons}-${training}.eps"
replot
EOF
