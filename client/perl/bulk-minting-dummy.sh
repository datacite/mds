#!/bin/bash
for I in `seq 1 $1`; do
  doi=10.5072/TEST_`date +%s-%N`
  echo `date` - $I/$1 - $doi
  ./mds-suite.pl -l doi post $doi 'http://example.com'
done


