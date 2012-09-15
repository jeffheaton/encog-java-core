#!/bin/bash

for training in rprop; do
for size in 10 30 100 300; do
  ./make_training_error_graph_only.sh uci_haberman $size $training 0.17
  ./make_training_error_graph_only.sh uci_letterrecognition $size $training 0.015
  ./make_training_error_graph_only.sh statlog_landsat $size $training 0.05
  ./make_training_error_graph_only.sh uci_magic $size $training 0.10
done
done
