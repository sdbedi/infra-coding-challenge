# Coding Challenge

## Introduction

At Datafiniti, we have several millions of product records in our database that we've collected from several retailers on the internet. We're tasked with keeping those records as up-to-date as we can. This means our data pipeline needs to be blazing fast with regards to getting data from the internet into our database. This coding challenge exposes you to a miniature prototype of our pipeline and tasks you with speeding up the rate at which data moves through it. 

## Objective
To reiterate, our goal is to get data, in the form of json objects, from point A to point B as fast as we possibly can. These json objects begin by being enqueued in a shared in-memory cache (redis in this case). We provide you with code that simply dequeues records one at a time and imports them into elasticsearch. Your objective is to do whatever it takes to increase the rate at which records are inserted into elasticsearch. You can complete this challenge by improving either the java or nodejs code we provide. This may seem initially daunting but don't worry, we provide plenty of tools which we explain in the section below.

## Setup Instructions

### Prerequistes
- Docker
  - If you don't already have docker you can download and install it from the following links:
    - [mac](https://www.docker.com/docker-mac)
    - [windows](https://www.docker.com/docker-windows)

- Fork this repository, clone it down and cd into it.
- The repository contains a docker composition that sets up the following docker containers:
  - Redis
  - Elasticsearch
  - Kibana. This is a fancy web UI that you can use to monitor import metrics in order to benchmark your solution
  - A dev container with either java or nodejs installed depending on which you decide to use
- Run one of the following commands depending on what language you've decided to use.


### NodeJS
![Instructions Screencast](http://g.recordit.co/cfFbTlEuLa.gif)

1. From within the `coding-challenge` directory, run `./bin/setup-nodejs.sh`
2. Once the output is done printing to the terminal click on this [link](http://localhost:5601).
3. Click the Monitoring button on the far left
4. Click `Enable Monitoring`
5. Open another terminal and type in `docker exec -it node-dev bash`
  - This is equivalent to `ssh`ing into a virtual machine that has nodejs installed
6. To run the code we've provided run the following commands

  ```bash
  cd code
  npm install
  node baseline.js
  ```
  - This code will seed 10,000 product records into redis and then begin to import one record at a time into elasticsearch
7. Head over to the browser window that you have kibana open in
8. Click on indices and then records
9. Watch the Indexing Rate graph to see how fast the provided solution is.
  - You'll be using this graph to benchmark your solution
  - Your solution will be assessed by first running `baseline.js` and then your solution. If the graph indicates that your solution is indexing records faster than the baseline then Congratulations! you did it.
10. Provide your solution in a file named `solution.js`.

### Java