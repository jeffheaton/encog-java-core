#!/bin/bash

echo -e "Starting Sonatype publish script...\n"

if [ "$TRAVIS_REPO_SLUG" == "encog/encog-java-core" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
  if [[ $(./gradlew -q getVersion) != *SNAPSHOT* ]]; then
      echo 'Travis can only publish snapshots, not:'
      ./gradlew -q getVersion
      return 0
  fi

  echo -e "Starting publish to Sonatype...\n"

  ./gradlew uploadArchives -PnexusUsername="${SONATYPE_USERNAME}" -PnexusPassword="${SONATYPE_PASSWORD}"
  RETVAL=$?

  if [ $RETVAL -eq 0 ]; then
    echo 'Completed publish!'
  else
    echo 'Publish failed.'
    return 1
  fi

fi