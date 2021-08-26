# wdsub-spark-example-01

Executing:

```shell
./bin/spark-submit \
  --class es.weso.wdsub.MeanOutDegreeFast \
  /gfs/projects/weso-scholia/wqsub-spark-assembly-1.0.jar \
  --mode cluster \
  --name big_input_big_cluster \
  -i /gfs/projects/weso-scholia/dumps/latest-all.json \
  -o /gfs/projects/wdsub
```

Produces:
```
==========================================
JOB INFORMATION
Job Name: big_input_big_cluster
Job Date: 2021-45-26 05:45:01
Job Cores: 104
Job Mem: 5125439488
Job Time: 4991.203025523 seconds
==========================================
JOB RESULTS
Total Out Degree -> 825874335
Total Number of Items -> 93517685
Average Out Degree -> 8.831210214410248
