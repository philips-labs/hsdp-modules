# This is the file that implements a flask server to do inferences. It's the file that you will modify to
# implement the scoring for your own algorithm.

from __future__ import print_function

import csv
import glob
import io
import json
import os
import pickle

import flask
import numpy as np
import pandas as pd

prefix = "/opt/ml/"
model_path = os.path.join(prefix, "model")

# A singleton for holding the model. This simply loads the model and holds it.
# It has a predict function that does a prediction based on the model and the input data.


class ScoringService(object):
    model = None  # Where we keep the model when it's loaded

    @classmethod
    def get_model(cls):
        """Get the model object for this instance, loading it if it's not already loaded."""
        folders = [f for f in glob.glob(model_path + '/*')]

        for f in folders:
            print(f)

        if cls.model == None:
            with open(os.path.join(model_path, "hsp-heart-knn-model.pkl"), "rb") as inp:
                cls.model = pickle.load(inp)
        return cls.model

    @classmethod
    def predict(cls, input):
        """For the input, do the predictions and return them.

        Args:
            input (a pandas dataframe): The data on which to do the predictions. There will be
                one prediction per row in the dataframe"""
        clf = cls.get_model()
        return clf.predict(input)

    @classmethod
    def score(cls, predictions):
        total = len(predictions)
        unique, counts = np.unique(predictions, return_counts=True)
        items = dict(zip(unique, counts))
        print(items)
        num_1 = items.get(1)
        act_perc = (num_1/total)*100
        acc_total = 10
        if total > 10:
            acc_total = total+2
        acc_perc = (num_1/acc_total)*100
        return (act_perc+acc_perc)/2

# The flask app for serving predictions
app = flask.Flask(__name__)


@app.route("/ping", methods=["GET"])
def ping():
    """Determine if the container is working and healthy. In this sample container, we declare
    it healthy if we can load the model successfully."""
    health = ScoringService.get_model() is not None  # You can insert a health check here

    status = 200 if health else 404
    return flask.Response(response="\n", status=status, mimetype="application/json")


@app.route("/invocations", methods=["POST"])
def transformation():
    """Do an inference on a single batch of data. In this sample server, we take data as CSV, convert
    it to a pandas data frame for internal use and then convert the predictions back to CSV (which really
    just means one prediction per line, since there's a single column.
    """
    data = None

    # Convert from CSV to pandas
    if flask.request.content_type == "text/csv":
        data = flask.request.data.decode("utf-8")
        s = io.StringIO(data)
        data = pd.read_csv(s, skiprows=1, header=None)
    else:
        return flask.Response(
            response="This predictor only supports CSV data", status=415, mimetype="text/plain"
        )

    print("Invoked with {} records".format(data.shape[0]))

    # Do the prediction
    predictions = ScoringService.predict(data)

    # Calculate accuracy
    score = ScoringService.score(predictions)

    # Convert from numpy back to JSON
    result = {
        'results': predictions.tolist(),
        'accuracy': score
    }
    return flask.jsonify(result), 200
