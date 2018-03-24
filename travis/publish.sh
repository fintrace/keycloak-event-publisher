#!/bin/bash

if [[ -n "$TRAVIS_TAG"]]; then
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/pubring.kbx.enc -out $GPG_DIR/pubring.kbx -d
    mvn deploy -B -U --settings $GPG_DIR/settings.xml -DperformRelease=true -DskipTests=true
    exit $?
fi