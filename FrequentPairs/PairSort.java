package org.myorg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

public class PairSort {

  public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
      
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      
      String[] fields = value.toString().split(",");
      String names = fields[0] + " " + fields[1] + " " + fields[19] + " " + fields[20];
      word.set(names);
      context.write(word, one);
    }
  } // Map

  public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
      
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      context.write(key, new IntWritable(sum));
    }
  } // Reduce
}