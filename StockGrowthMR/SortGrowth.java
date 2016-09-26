package org.myorg;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

// Sort by Growth
public class SortGrowth {
	
	// Key: Growth, Val: Symbol
	public static class Map extends Mapper<Text, Text, DoubleWritable, Text> {

		public void map(Text key, Text value, Context context) 
				throws IOException, InterruptedException {
			
			DoubleWritable dwKey = new DoubleWritable(Double.parseDouble(key.toString()));
			context.write(dwKey, value);
		}
	} // class Map

	public static class Reduce extends Reducer<DoubleWritable, Text, Text, Text> {

	    public void reduce(DoubleWritable key, Iterable<Text> values, Context context) 
	    		throws IOException, InterruptedException {
			
			for ( Text value : values ) {
				String strGrowth = String.format("%.2f", key.get());
				Text growth = new Text( strGrowth + "%");
				context.write(value, growth); }
		}
	} // class Reduce
} // SortGrowth
