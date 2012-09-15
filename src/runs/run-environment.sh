#!/bin/bash

export ENCOG_ENV=1.0

function run() {
	$do java -cp build/classes ensembles.Test $1 $2 $3 $4 $5 $6 0.3 $8 $9 ${10} ${11} ${12} ${13} ${14} ${15}
}

function runWithDefaults() {
	run $1 $2 $3 $4 $5 $6 0.3 $7 $8 $9 ${10} ${11} ${12} ${13} ${14}
}

function averaging-rprop() {
	runWithDefaults $1 $2 $3 $4 $5 $6 rprop $7 averaging $8 $9 ${10} ${11} ${12} ${13}
}

function bagging-haberman() {
	averaging-rprop bagging problems/uci_haberman $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function boosting-haberman() {
	averaging-rprop adaboost problems/uci_haberman $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function stacking-haberman() {
	averaging-rprop stacking problems/uci_haberman $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function bagging-letterrecognition() {
	averaging-rprop bagging problems/uci_letterrecognition $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function boosting-letterrecognition() {
	averaging-rprop adaboost problems/uci_letterrecognition $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function stacking-letterrecognition() {
	averaging-rprop stacking problems/uci_letterrecognition $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function bagging-landsat() {
	averaging-rprop bagging problems/statlog_landsat $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function boosting-landsat() {
	averaging-rprop adaboost problems/statlog_landsat $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function stacking-landsat() {
	averaging-rprop stacking problems/statlog_landsat $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13}
}

function logged_run() {
	date=$(date +%Y-%m-%d.%H.%M)
	($1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13} > /home/nitbix/projects/mscproject/runs_output/${date}_$1.${BASHPID}.$!.${PBS_JOBNAME}-${PBS_JOBID}.output)
}

function build_lib_file() {
	class=`echo $1 | sed 's/\.java/\.class/'`
	if [[ ! -e $class ]]; then
		$do javac -sourcepath ../main/java $1
		echo '.'
	fi
}

function rebuild_from_scratch() {
	ant clean
	ant build
}

echo "Compiling source.."
ant build 

echo "Encog Ensemble Test suite ready"


