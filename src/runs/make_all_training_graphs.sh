#!/bin/bash

. ./run-environment.sh

export ENCOG_ENV_LOADED=true

for training in rprop; do
for size in 10 30 100 300; do
  ./make_training_error_graph.sh uci_haberman $size 200 $training 0.17 1
  ./make_training_error_graph.sh uci_letterrecognition $size 16000 $training 0.015 1 1000
  ./make_training_error_graph.sh statlog_landsat $size 5000 $training 0.05 1
  ./make_training_error_graph.sh uci_magic $size 16000 $training 0.10 1
  ./make_training_error_graph.sh uci_ionosphere $size 300 $training 0.05 1 200
done
done
