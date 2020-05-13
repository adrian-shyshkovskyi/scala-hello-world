#!/bin/bash
set -ev

gpg --version
gpg --list-keys
gpg --list-secret-keys

# Deploy to Maven Central
# TBD