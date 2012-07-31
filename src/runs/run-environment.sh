#!/bin/bash

function run() {
	$do java -cp .:../main/java ensembles.Test $1 $2 $3 $4 $5 $6 0.3 $8 $9 ${10} ${11} ${12}
}

function runWithDefaults() {
	run $1 $2 $3 $4 $5 $6 0.3 $7 $8 $9 ${10} ${11}
}

function averaging-rprop() {
	runWithDefaults $1 $2 $3 $4 $5 $6 rprop $7 averaging $8 $9 ${10}
}

function bagging-haberman() {
	averaging-rprop bagging problems/uci_haberman $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
}

function boosting-haberman() {
	averaging-rprop adaboost problems/uci_haberman $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
}

function bagging-letterrecognition() {
	averaging-rprop bagging problems/uci_letterrecognition $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
}

function boosting-letterrecognition() {
	averaging-rprop adaboost problems/uci_letterrecognition $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
}

function bagging-landsat() {
	averaging-rprop bagging problems/statlog_landsat $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
}

function boosting-landsat() {
	averaging-rprop adaboost problems/statlog_landsat $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
}

function logged_run() {
	date=$(date +%Y-%m-%d.%H.%M)
	($1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13} > /home/nitbix/projects/mscproject/runs_output/${date}_$1.${BASHPID}.output)
}

function build_lib_file() {
	class=`echo $1 | sed 's/\.java/\.class/'`
	if [[ ! -e $class ]]; then
		$do javac -sourcepath ../main/java $1
		echo '.'
	fi
}

function rebuild_from_scratch() {
	$do find ../main/java -name '*.class' -exec rm \{} \;
	for source in `find ../main/java -name '*.java'`; do
		build_lib_file $source;
	done
	$do javac -sourcepath .:../main/java ensembles/Test.java
}

echo "Compiling source.."
javac -sourcepath .:../main/java ensembles/Test.java

echo "Encog Ensemble Test suite ready"


