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
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import cn.hfbin.bean.Msg;
import cn.hfbin.staticPath.StaticPath;

public class Min {
	public static List<String> result = new ArrayList<>();
	public static class TokenizerMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			//
			String parseInt;
			String str = value.toString();
			if(!str.substring(0,3).equals("STN")){
				String year = Msg.getYear(str); 
				if(Msg.getCharAt(str, 113)){
					parseInt = Msg.getMin1(str);
				}else{
					parseInt = Msg.getMin2(str);
				}
				context.write(new Text(year), new Text(parseInt));
			}

		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text arg0, Iterable<Text> arg1, Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			Double min = Double.MAX_VALUE;
			for (Text text : arg1) {
				String qiwen = text.toString();
				Double double1 = Double.valueOf(qiwen).doubleValue();
				min = Math.min( min, double1);
			}
			result.add(String.format("年份：%s\t最低气温（华氏度）：%s", arg0, min));
			context.write(new Text(arg0), new Text(Double.toString(min)));
		}
	}


	public static void start() throws Exception {
		Configuration cfg = new Configuration();
		Job job = Job.getInstance(cfg, "music1");

		job.setJarByClass(Min.class);// 通过class查找job的jar文件

		job.setMapperClass(TokenizerMapper.class);// 指定Mapper
		job.setReducerClass(IntSumReducer.class);// 指定Reducer

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(
				StaticPath.DATA_PATH));
		// 设置输出文件目录
		FileSystem fs = FileSystem.get(cfg);
		Path f=new Path(StaticPath.MIN_PATH);
		fs.delete(f,true);
		FileOutputFormat.setOutputPath(job, new Path(
				StaticPath.MIN_PATH));

		job.waitForCompletion(true);
		//System.exit(1);

	}
}
