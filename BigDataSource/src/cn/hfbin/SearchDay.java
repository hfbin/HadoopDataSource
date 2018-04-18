package cn.hfbin;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import cn.hfbin.bean.Msg;
import cn.hfbin.staticPath.StaticPath;

public class SearchDay {
	public  static String  DATE = null ;
	
	public static List<String> result = new ArrayList<>();
	public static class TokenizerMapper extends Mapper<LongWritable,Text,Text,IntWritable> {
		@Override
		protected void map(LongWritable key, Text value,	Context context)
				throws IOException, InterruptedException {
			String str = value.toString();
			String substring = str.substring(14, 22);
			if(substring.equals(DATE)){
				context.write(new Text(value.toString()), new IntWritable(1));
				
				result.add(String.format("站点：%s\t日期：%s\t平均气温：%s\t最高气温：%s\t最低气温:%s\t",
						Msg.getZhandian(str), Msg.getDate(str),Msg.getAvg(str)
						,Msg.getCharAt(str, 103) == true ? Msg.getMax1(str): Msg.getMax2(str)
						,Msg.getCharAt(str, 113) == true ? Msg.getMin1(str): Msg.getMin2(str)));
			}
		}
	}
	public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,Context context)
				throws IOException, InterruptedException {			
			context.write(key, new IntWritable());
		}
	}	
	
	
	public static void start(Integer data) throws Exception {
		SearchDay.DATE =""+data;
		Configuration cfg = new Configuration();
		Job job = Job.getInstance(cfg, "music1");
		
		job.setJarByClass(SearchDay.class);//通过class查找job的jar文件		
		
		job.setMapperClass(TokenizerMapper.class);//指定Mapper
		job.setReducerClass(IntSumReducer.class);//指定Reducer
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		//设置输入文件路径或目录
		FileInputFormat.addInputPath(job, new Path(StaticPath.DATA_PATH));
		//设置输出文件目录
		
		FileSystem fs = FileSystem.get(cfg);
		Path f=new Path(StaticPath.DAY_PATH);	
		fs.delete(f,true);
		
		FileOutputFormat.setOutputPath(job, new Path(StaticPath.DAY_PATH));
		
		job.waitForCompletion(true);
		//System.exit(1);//提交作业
		
	}
}
