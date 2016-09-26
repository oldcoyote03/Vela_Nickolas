package org.myorg;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class GrowthRate {
	
	// Key: Symbol, Val: Price <space> Date
	public static class Map extends Mapper<Text, Text, Text, Text> {

		public void map(Text key, Text value, Context context) 
				throws IOException, InterruptedException {
			
			context.write(key, value);
		}
	}

	// Growth Rate per Symbol
	public static class Reduce extends Reducer<Text, Text, Text, Text> {

	    public void reduce(Text key, Iterable<Text> values, Context context) 
	    		throws IOException, InterruptedException {
			
			String[] valString = new String[2];
			double first = 0; double last = 0; int count = 0;
			for ( Text value : values ) {
				count++;
				valString = value.toString().split(" ");
				if( valString[1].equals("first") ) {
					first = Double.parseDouble(valString[0]); }
				else {
					last = Double.parseDouble(valString[0]); }
			}

			// Output:	[ Key:Growth, Val:Symbol ]
			if(count==2) {
				Double dGrowth = ((first - last) / first) * 100;
				Text growth = new Text(Double.toString(dGrowth));
				context.write(growth, key);
			}
		}
	} // class Reduce
} // GrowthRate
