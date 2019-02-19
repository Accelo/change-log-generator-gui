#!/usr/bin/env bash

to="$1"
force="$2"
find . -name 'pom.xml' -exec perl -i -0777 -pe "s@(<groupId>com\.tuannguyen\.liquibase</groupId>\s+)<version>(.*?)</version>@\1<version>$to</version>@g" {} \;
git commit -a -m "Release v$to";
git tag -d "v$to"
git push origin -d "v$to"
git tag -m "Release v$to" "v$to"
if [ $force ]; then
    echo "Force push"
    git push -f --follow-tags
else
    git push --follow-tags
fi