package cn.hfbin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cn.hfbin.Min.IntSumReducer;
import cn.hfbin.Min.TokenizerMapper;
import cn.hfbin.bean.Msg;
import cn.hfbin.staticPath.StaticPath;

public class Parfait {
	public static List<String> result = new ArrayList<>();
	public static class TokenizerMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String str = value.toString();
			String year = Msg.getYear(str); 
			if (!str.substring(0, 3).equals("STN")) {
				if(Msg.getCharAt(str, 119) || Float.valueOf(Msg.getParfait(str)).floatValue() > 0){
					context.write(new Text(year), new IntWritable(1));
				}
				
			}

		}
	}
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		protected void reduce(Text arg0, Iterable<IntWritable> arg1, Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			int sum = 0;
			for (IntWritable s : arg1) {
				sum += s.get();
			}
			result.add(String.format("年份：%s\t 总下雨天数：%s", arg0, sum));
			context.write(arg0, new IntWritable(sum));
		}
	}

	public static void start() throws Exception {
		Configuration cfg = new Configuration();
		Job job = Job.getInstance(cfg, "music1");

		job.setJarByClass(Min.class);// 通过class查找job的jar文件

		job.setMapperClass(TokenizerMapper.class);// 指定Mapper
		job.setReducerClass(IntSumReducer.class);// 指定Reducer

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(StaticPath.DATA_PATH));
		// 设置输出文件目录
		FileSystem fs = FileSystem.get(cfg);
		Path f = new Path(StaticPath.PAR_PATH);
		fs.delete(f, true);
		FileOutputFormat.setOutputPath(job, new Path(StaticPath.PAR_PATH));

		job.waitForCompletion(true);
		//System.exit(0);

	}

}
