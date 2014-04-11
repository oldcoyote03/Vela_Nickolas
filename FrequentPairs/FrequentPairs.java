package org.myorg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.jobcontrol.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;

public class FrequentPairs extends Configured implements Tool {

    public static Job setPairSort(String input, String output) throws Exception {

      Configuration conf = new Configuration();      
      Job job = new Job(conf, "FrequentPars");
      job.setJarByClass(FrequentPairs.class);
      
      job.setMapperClass(PairSort.Map.class);
      job.setCombinerClass(PairSort.Reduce.class);
      job.setReducerClass(PairSort.Reduce.class);
      
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);

      job.setInputFormatClass(TextInputFormat.class);
      job.setOutputFormatClass(TextOutputFormat.class);

      FileInputFormat.addInputPath(job, new Path(input));
      FileOutputFormat.setOutputPath(job, new Path(output));

      return job;

    }

    public static Job setCountSort(String input, String output) throws Exception {

      Configuration conf = new Configuration();      
      Job job = new Job(conf, "FrequentPars");
      job.setJarByClass(FrequentPairs.class);

      job.setMapperClass(CountSort.Map.class);
      job.setReducerClass(CountSort.Reduce.class);

      job.setMapOutputKeyClass(IntWritable.class);
      job.setMapOutputValueClass(Text.class);

      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);
      
      job.setInputFormatClass(KeyValueTextInputFormat.class);
      job.setOutputFormatClass(TextOutputFormat.class);

      FileInputFormat.setInputPaths(job, new Path(input));
      FileOutputFormat.setOutputPath(job, new Path(output));

      return job;

    }

    public int run(String[] args) throws Exception {
      
      Job job1 = setPairSort(args[0], args[1]);
      ControlledJob cJob1 = new ControlledJob(job1.getConfiguration());

      Job job2 = setCountSort(args[1] + "/part*", args[2]);
      ControlledJob cJob2 = new ControlledJob(job2.getConfiguration());
      cJob2.addDependingJob(cJob1);

      JobControl jobControl = new JobControl("Job Control");
      jobControl.addJob(cJob1);
      jobControl.addJob(cJob2);

      Thread jbcntrl_t = new Thread(jobControl);
      jbcntrl_t.start();
      while(!jobControl.allFinished()) {
        try {
        Thread.sleep(1000);
        } catch (InterruptedException e) {/*ignore*/}
      }
      jobControl.stop();
      return 0;
    }

    public static void main(String[] args) throws Exception {
      int ret = ToolRunner.run(new FrequentPairs(), args);
      System.exit(ret);
    }
}
