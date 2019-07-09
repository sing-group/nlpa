#!/bin/bash

#  NLPA
# 
#  Copyright (C) 2018 - 2019 SING Group (University of Vigo)
# 
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as
#  published by the Free Software Foundation, either version 3 of the
#  License, or (at your option) any later version.
# 
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public
#  License along with this program.  If not, see
#  <http://www.gnu.org/licenses/gpl-3.0.html>.

# This script generates the ytbid files for the Youtube dataset included
# in UCI ML Repository. Ham files are included in _ham_ subfolder
# while spam files are included in a _spam_ subfolder
# (https://archive.ics.uci.edu/ml/datasets/YouTube+Spam+Collection)
# Usage:
# 1- Download the zip file (YouTube-Spam-Collection-v1.zip) included in 
#    the Data Folder for the repository 
# 2- Uncompress the zip file 
#     $ unzip YouTube-Spam-Collection-v1.zip
# 3- Move this file into the directory extracted form the zip File
#     $ mv transform-youtube-uci-ml.sh YouTube-Spam-Collection-v1
# 4- Execute this file in the target directory
#     $ cd YouTube-Spam-Collection-v1
#     $ chmod +x transform-youtube-uci-ml.sh
#     $ ./transform-youtube-uci-ml.sh 
# 5- The output will be generated in the folder youtube-uci-ml

SPAM_DIR="youtube-uci-ml/_spam_/"
HAM_DIR="youtube-uci-ml/_ham_/"

mkdir -p ${SPAM_DIR}
mkdir -p ${HAM_DIR}

(( cspam = 0 ))
(( cham = 0 ))

for file in *.csv
do
    while read line 
    do
      id=$(echo $line | sed "s/,.*//g")   #pick the first field
      class=$(echo $line | sed "s/^.*,//g")  #pick the last field

      if [ $class -eq 0 ] 
      then
          (( cham++ ))			
          echo "${id}" > ${HAM_DIR}/${id}.ytbid 
      else
          (( cspam++ ))			
          echo "${id}" > ${SPAM_DIR}/${id}.ytbid
      fi
    done <<< "$(tail -$(expr $(wc -l $file | sed -e "s/^[[:space:]]*//g" -e "s/[[:space:]].*$//g") - 1) $file)"
done

echo "Done converting. Converted: "
echo "+ ${cspam} spam messages in ${SPAM_DIR}"
echo "+ ${cham} ham messages in ${HAM_DIR}"
echo "TOTAL: $(expr $cspam + $cham )"
