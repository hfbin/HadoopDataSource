package cn.hfbin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cn.hfbin.SearchDay.IntSumReducer;
import cn.hfbin.SearchDay.TokenizerMapper;
import cn.hfbin.bean.Msg;
import cn.hfbin.staticPath.StaticPath;

public class TomorrowTemps {
	public static List<String> result = new ArrayList<>();

	public static class TokenizerMapper extends
			Mapper<LongWritable, Text, Text, MapWritable> {
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			MapWritable map = new MapWritable();
			String str = value.toString();
			String[] datas = new String[] { "20161231", "20161229", "20161228",
					"20161227", "20161226", "20161225", "20161224" };
			for (int i = 0; i < datas.length; i++) {
				if (Msg.getDate(str).equals(datas[i])) {

					map.put(new Text("avg"), new FloatWritable(
							Float.valueOf(Msg.getAvg(str)).floatValue()));
					map.put(new Text("max"), new FloatWritable(
							Float.valueOf(Msg.getCharAt(str, 103) == true ? Msg.getMax1(str) : Msg.getMax2(str)).floatValue()));
					
					map.put(new Text("min"), new FloatWritable(
							Float.valueOf(Msg.getCharAt(str, 113) == true ? Msg.getMin1(str) : Msg.getMin2(str)).floatValue()));
					context.write(new Text(Msg.getZhandian(str)), map);

				}
			}
		}
	}

	public static class IntSumReducer extends
			Reducer<Text, MapWritable, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<MapWritable> values,
				Context context) throws IOException, InterruptedException {
			
			int i = 0;
			float temp = 0;
			float max = 0;
			float min = 0;

			for (MapWritable item : values) {
				FloatWritable a = (FloatWritable) item.get(new Text("avg"));
				FloatWritable b = (FloatWritable) item.get(new Text("max"));
				FloatWritable c = (FloatWritable) item.get(new Text("min"));
				temp += a.get();
				max += b.get();
				min += c.get();
				i++;
			}
			float avg = temp / i;
			float max2 = max / i;
			float min2 = min / i;
			String str = String.format(
					"预计站点 %s 在今天的平均温度：%s\t最高气温：%s\t最低气温：%s",
					key,  avg, max2, min2);
			result.add(str);
			context.write(new Text(str), new Text());
		}
	}

	public static void start() throws Exception {
		Configuration cfg = new Configuration();
		Job job = Job.getInstance(cfg, "music1");

		// 通过class查找job的jar文件
		job.setJarByClass(SearchDay.class);
		// 指定Mapper
		job.setMapperClass(TokenizerMapper.class);
		// 指定Reducer
		job.setReducerClass(IntSumReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MapWritable.class);

		// 设置输入文件路径或目录
		FileInputFormat.addInputPath(job, new Path(StaticPath.DATA_PATH));
		// 设置输出文件目录
		FileSystem fs = FileSystem.get(cfg);
		Path f = new Path(StaticPath.TAR_PATH);
		fs.delete(f, true);
		FileOutputFormat.setOutputPath(job, new Path(StaticPath.TAR_PATH));
		job.waitForCompletion(true);
		// System.exit(1);//提交作业

	}
	public static void main(String[] args) throws Exception {
		TomorrowTemps.start();
		System.exit(1);
		for (String e : result) {
			
		}
	}
}
