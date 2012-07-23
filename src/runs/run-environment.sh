#!/bin/bash

function run() {
	$do java -cp .:../main/java ensembles.Test $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
}

function runWithDefaults() {
	run $1 $2 $3 $4 $5 $6 0.3 $7 $8 $9
}

function averaging-rprop() {
	runWithDefaults $1 $2 $3 $4 $5 $6 rprop $7 averaging
}

function bagging-haberman() {
	averaging-rprop bagging uci_haberman $1 $2 $3 $4 $5
}

function boosting-haberman() {
	averaging-rprop adaboost uci_haberman $1 $2 $3 $4 $5
}

function bagging-letterrecognition() {
	averaging-rprop bagging uci_letterrecognition $1 $2 $3 $4 $5
}

function boosting-letterrecognition() {
	averaging-rprop adaboost uci_letterrecognition $1 $2 $3 $4 $5
}

echo "Compiling source.."
javac -sourcepath .:../main/java ensembles/Test.java

echo "Encog Ensemble Test suite ready"


