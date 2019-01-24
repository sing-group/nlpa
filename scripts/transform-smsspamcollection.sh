#!/bin/bash
# This script extracts the messages contained in smsspamcollection corpus
# from the original file (smsspamcollection.zip) that can be 
# downloaded from http://www.dt.fee.unicamp.br/~tiago/smsspamcollection/
# Ham emails are included in _ham_ subfolder
# while spam files are included in a _spam_ subfolder
# Usage:
# 1- Download the zip file (smsspamcollection.zip) from the URL specified above 
# 3- Move this file into the directory where the zip file has been
#    placed
#     $ mv transform-transform-smsspamcollection.sh sms-spam-collection
# 4- Execute this file in the target directory
#     $ cd sms-spam-collection
#     $ chmod +x transform-smsspamcollection.sh
#     $ ./transform-smsspamcollection.sh
# 5- The output will be generated in the folder sms-spam-collection

OUTPUT_DIR="sms-spam-collection"
SPAM_DIR="${OUTPUT_DIR}/_spam_/"
HAM_DIR="${OUTPUT_DIR}/_ham_/"

mkdir -p ${SPAM_DIR}
mkdir -p ${HAM_DIR}

id=0

unzip -p smsspamcollection.zip SMSSpamCollection.txt | while read class text
do
   if [ ${class} == "ham" ]
   then
     echo "$text" > ${HAM_DIR}/${id}.tsms
   else
     echo "$text" > ${SPAM_DIR}/${id}.tsms
   fi
		
   id=$(expr $id + 1)
done

echo "Done converting. Converted: "
echo "+ $(find ${SPAM_DIR} -type f| wc -l)  spam sms in ${SPAM_DIR}"
echo "+ $(find ${HAM_DIR} -type f | wc -l) ham sms in ${HAM_DIR}"
echo "TOTAL: $(find ${OUTPUT_DIR} -type f | wc -l)"
