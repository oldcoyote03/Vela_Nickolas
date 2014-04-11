package org.myorg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

public class CountSort {

  public static class Map extends Mapper<Text, Text, IntWritable, Text> {
      
    public void map(Text key, Text value, Context context) throws IOException, InterruptedException { 

      IntWritable val = new IntWritable(Integer.parseInt(value.toString()));
      context.write(val, key);
    }
  } // Map

  public static class Reduce extends Reducer<IntWritable, Text, Text, IntWritable> {
      
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        
      for (Text val : values) {
        if (key.get()>1) {
          context.write(val, key);
        }
      }
    }
  } // Reduce
}