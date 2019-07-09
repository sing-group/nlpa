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

# This script extracts the messages contained in TREC 2007 spam corpus
# from the raw files included in the tarball (tar.gz) that can be 
# downloaded from http://plg.uwaterloo.ca/~gvcormac/treccorpus07/
# Ham emails are included in _ham_ subfolder
# while spam files are included in a _spam_ subfolder
# Usage:
# 1- Download the tarball (.tar.gz) from the URL specified above 
# 3- Move this file into the directory where the tarballs have been
#    placed 
#     $ mv transform-trec2007.sh trec2007
# 4- Execute this file in the target directory
#     $ cd trec2007
#     $ chmod +x transform-trec2007.sh
#     $ ./transform-trec2007.sh
# 5- The output will be generated in the folder trec2007

OUTPUT_DIR="trec2007"
SPAM_DIR="${OUTPUT_DIR}/_spam_/"
HAM_DIR="${OUTPUT_DIR}/_ham_/"

mkdir -p ${SPAM_DIR}
mkdir -p ${HAM_DIR}

tar xzf trec07p.tgz

while read class message
do
   echo "Processing $(basename  ${message}) as $class"
   if [ ${class} == "ham" ]
   then
     mv trec07p/full/${message} ${HAM_DIR}/$(basename ${message}).eml
   else
     mv trec07p/full/${message} ${SPAM_DIR}/$(basename ${message}).eml
   fi
done < trec07p/full/index

rm -rf trec07p;

echo "Done converting. Converted: "
echo "+ $(find ${SPAM_DIR} -type f | wc -l)  spam messages in ${SPAM_DIR}"
echo "+ $(find ${HAM_DIR} -type f | wc -l) ham messages in ${HAM_DIR}"
echo "TOTAL: $(find ${OUTPUT_DIR} -type f | wc -l)"

