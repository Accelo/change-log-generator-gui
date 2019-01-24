#!/bin/bash
if [ $# > 0 ]
then
	if [ $1 == 'init' ]
	then
		if [ ! -f changelog-generator.jar ]
		then
			artifact_path=`curl https://circleci.com/api/v1.1/project/github/vdtn359/changelog-generator-gui/latest/artifacts?circle-token=9e14e0e6de945b89ba412b687cfe8702e48d3803 | jq ".[0].url"`
			artifact_path=${artifact_path:1:${#artifact_path} - 2}
			echo "fetching from $artifact_path";
			curl $artifact_path -o changelog-generator.jar
			echo "Successfully init"
		else
			echo "Already init"
		fi
	else
		if [ ! -f changelog-generator.jar ]
		then
			echo "Not initialise yet. Please run $0 init"
		else
			java -jar changelog-generator.jar "$@"
		fi
	fi
fi
