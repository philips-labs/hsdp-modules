#!/bin/sh

bucket=$1
program=$2
# Options: [cdr]
input=$3

# Upload training data if you want to train the model through HSP AI training service
#aws s3 cp ./input/data/training/heart.csv s3://${bucket}/${program}/input/data/training/heart.csv
# shellcheck disable=SC2039
if [ "$input" == "cdr" ]
then
    aws s3 cp ./input/data/cdr/heart_vitals.csv s3://${bucket}/${program}/input/data/test/payload.csv
else
    aws s3 cp ./input/data/test/payload.csv s3://${bucket}/${program}/input/data/test/payload.csv
fi

pkl_name="hsp-heart-knn-model.pkl"
local_model="../../local_test/test_dir/model/${pkl_name}"
hsp_model="./model/${pkl_name}"
echo "Local model - $local_model"
echo "HSP model - $hsp_model"
# shellcheck disable=SC2164
if test -f "$hsp_model"; then
  cd model
  tar cvzfp model.tar.gz $pkl_name
  aws s3 cp model.tar.gz s3://${bucket}/${program}/model/model.tar.gz
  # shellcheck disable=SC2103
  cd ..
elif test -f "$local_model"; then
  cp $local_model ./model
  cd model
  tar cvzfp model.tar.gz $pkl_name
  aws s3 cp model.tar.gz s3://${bucket}/${program}/model/model.tar.gz
  # shellcheck disable=SC2103
  cd ..
else
  echo "Model file not found. Create one before upload"
fi

