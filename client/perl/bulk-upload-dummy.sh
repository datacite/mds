#!/bin/bash
doi_xpath="//*[local-name() = 'identifier' and @identifierType='DOI']"
number=$1
shift
for I in `seq 1 $number`; do
  doi=10.5072/TEST_`date +%s-%N`
  echo `date` - $I/$number - $doi
  for file in $@; do 
    xmlstarlet ed -u "$doi_xpath" -v "$doi" $file | ./mds-suite.pl -l metadata put $doi
  done
done


