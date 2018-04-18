package cn.hfbin;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cn.hfbin.bean.Msg;
import cn.hfbin.staticPath.StaticPath;


public class Avg {
	public static List<String> result = new ArrayList<>();
	public static class TokenizerMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			//
			String parseInt;
			String str = value.toString();
			String year = Msg.getYear(str); 
			if (!str.substring(0, 3).equals("STN")) {
				parseInt = Msg.getAvg(str);
				context.write(new Text(year), new Text(parseInt));
			}

		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text arg0, Iterable<Text> arg1, Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			float sum = 0 ;
			float avg;
			int i = 0;
			for (Text text : arg1) {
				String qiwen = text.toString();
				float float1 = Float.valueOf(qiwen).floatValue();
				sum += float1;
				i++;
			}
			avg = sum / i;
			result.add(String.format("年份：%s\t平均气温（华氏度）：%s", arg0, avg));
			context.write(arg0, new Text(Float.toString(avg)));
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

		FileInputFormat.addInputPath(job, new Path(StaticPath.DATA_PATH));
		// 设置输出文件目录
		FileSystem fs = FileSystem.get(cfg);
		Path f = new Path(StaticPath.AVG_PATH);
		fs.delete(f, true);
		FileOutputFormat.setOutputPath(job, new Path(StaticPath.AVG_PATH));

		job.waitForCompletion(true);
		//System.exit(1);

	}
}
