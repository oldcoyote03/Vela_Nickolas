package org.myorg;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class WeeklyMean {
	
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

	    private Text newKey = new Text();
	    private Text newVal = new Text();

		public void map(LongWritable key, Text value, Context context) 
				throws IOException, InterruptedException {
			
			// line:	[ Date:0, Symbol:1, Open:2, Close:5 ]
			String[] lineItem = value.toString().split(",");
			int Date = Integer.parseInt(lineItem[0]);
			
			// output records from first and last weeks
			// 20090821 < first < 20090827
			if( Date <= 20090827 ) {
				newKey.set(lineItem[1] + " first");
				newVal.set(lineItem[2] + " " + lineItem[5]);
		    	context.write(newKey, newVal);
			
			// 20100820 > last > 20100814
			} else if( Date >= 20100814 ) {
				newKey.set(lineItem[1] + " last");
				newVal.set(lineItem[2] + " " + lineItem[5]);
		    	context.write(newKey, newVal);
			}			
			// Output:	[ Key:(Symbol,Date), Val:(Open,Close) ]
		}
	} // class Map

	public static class Reduce extends Reducer<Text, Text, Text, Text> {

	    public void reduce(Text key, Iterable<Text> values, Context context) 
	    		throws IOException, InterruptedException {
			
			// Key:		[ Symbol:0, Date:1 ]
			// Val:		[ Open:0, Close:1 ]
			String[] oldKey = key.toString().split(" ");
			
			String[] valString = new String[2];
			double openDouble = 0;double closeDouble = 0;double midVal = 0;double dailySum = 0;
			for ( Text value : values ) {
				
				// mid-value	<--		(Open + Close) / 2
				// daily-sum	<--		daily-sum + mid-value
				valString = value.toString().split(" ");
				openDouble = Double.parseDouble(valString[0]);
				closeDouble = Double.parseDouble(valString[1]);
				midVal = (openDouble + closeDouble) / 2;
				dailySum = dailySum + midVal;
			}
			double price = dailySum / 7;

			// Output:	[ Key:Symbol, Val: Price <space> Date) ]
			Text symbol = new Text(oldKey[0]);
			Text priceDate = new Text(Double.toString(price) + " " + oldKey[1]);
			context.write(symbol, priceDate);
		}
	} // class Reduce
} // WeeklyMean
