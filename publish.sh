#!/usr/bin/env sh

dest=$(tr [:lower:] [:upper:] <<< "${1:0:1}")${1:1}

function publish() {
  echo "./gradlew :$1:publish$dest"
  ./gradlew ":$1:publish$dest"
}

publish rv-decoration
publish rv-itemtype
publish rv-loadmore
publish rv-polymorph
publish rv-selection
