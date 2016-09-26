package org.myorg;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class StockGrowth extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new StockGrowth(), args);
		System.exit(ret);
	}
	
	public int run(String[] args) throws Exception {
		
		// Sort (weekly-average,week) BY (symbol,week)
		Job job1 = setWeeklyMean(args[0], args[1]);
		ControlledJob cJob1 = new ControlledJob(job1.getConfiguration());
		
		// Sort growth rate BY symbol
		Job job2 = setGrowthRate(args[1] + "/part*", args[2]);
		ControlledJob cJob2 = new ControlledJob(job2.getConfiguration());
		cJob2.addDependingJob(cJob1);
		
		// Sort symbol BY growth rate
		Job job3 = setSortGrowth(args[2] + "/part*", args[3]);
		ControlledJob cJob3 = new ControlledJob(job3.getConfiguration());
		cJob3.addDependingJob(cJob2);

		JobControl jobControl = new JobControl("Job Control");
		jobControl.addJob(cJob1);
		jobControl.addJob(cJob2);
		jobControl.addJob(cJob3);

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

	public static Job setWeeklyMean(String input, String output) 
			throws Exception {
		
		Job job = Job.getInstance(new Configuration());
		job.setJarByClass(StockGrowth.class);
		job.setJobName("StockGrowth");
		
		job.setMapperClass(WeeklyMean.Map.class);
		job.setReducerClass(WeeklyMean.Reduce.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
				
		FileInputFormat.setInputPaths(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job;
	}

	public static Job setGrowthRate(String input, String output) 
			throws Exception {
		
		Job job = Job.getInstance(new Configuration());
		job.setJarByClass(StockGrowth.class);
		job.setJobName("StockGrowth");
		
		job.setMapperClass(GrowthRate.Map.class);
		job.setReducerClass(GrowthRate.Reduce.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
				
		FileInputFormat.setInputPaths(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job;
	}

	public static Job setSortGrowth(String input, String output) 
			throws Exception {
		
		Job job = Job.getInstance(new Configuration());
		job.setJarByClass(StockGrowth.class);
		job.setJobName("StockGrowth");
		
		job.setMapperClass(SortGrowth.Map.class);
		job.setReducerClass(SortGrowth.Reduce.class);
		
		job.setMapOutputKeyClass(DoubleWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
				
		FileInputFormat.setInputPaths(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job;
	}

}
