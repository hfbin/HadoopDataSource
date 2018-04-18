package cn.hfbin.main;

import java.util.List;
import java.util.Scanner;

import cn.hfbin.Avg;
import cn.hfbin.Max;
import cn.hfbin.Min;
import cn.hfbin.Parfait;
import cn.hfbin.SearchDay;
import cn.hfbin.TomorrowTemps;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int code = -1;
		while (code != 0) {
			showMenu();
			code = sc.nextInt();
			if (code == 0) {
				break;
			}
			switch (code) {
			case 1:
				System.out.print("请输入日期（yyyyMMdd）：");
				int date = sc.nextInt();
				try {
					SearchDay.start(date);
					if (SearchDay.result.size()>0) {
						showList(SearchDay.result);
						SearchDay.result.clear();
					}else {
						System.out.println("没有查询出结果");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("查询出现错误");
				}
				break;
			case 2:
				try {
					Max.start();
					if (Max.result.size()>0) {
						showList(Max.result);
						Max.result.clear();
					}else {
						System.out.println("没有查询出结果");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("查询出现错误");
				}
				break;
			case 3:
				try {
					Min.start();
					if (Min.result.size()>0) {
						showList(Min.result);
						Min.result.clear();
					}else {
						System.out.println("没有查询出结果");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("查询出现错误");
				}
				break;
			case 4:
				try {
					Avg.start();
					if (Avg.result.size()>0) {
						showList(Avg.result);
						Avg.result.clear();
					}else {
						System.out.println("没有查询出结果");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("查询出现错误");
				}
				break;
			case 5:
				try {
					Parfait.start();
					if (Parfait.result.size()>0) {
						showList(Parfait.result);
						Parfait.result.clear();
					}else {
						System.out.println("没有查询出结果");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("查询出现错误");
				}
				break;
			case 6:
				try {
					TomorrowTemps.start();
					if (TomorrowTemps.result.size()>0) {
						showList(TomorrowTemps.result);
						TomorrowTemps.result.clear();
					}else {
						System.out.println("没有查询出结果");
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("查询出现错误");
				}
				break;
			default:
				break;
			}
			System.out.print("请问是否继续（y/n）？：");
			String input = sc.next();
			if (input.charAt(0) == 'y' || input.charAt(0) == 'Y') {
				continue;
			}else {
				break;
			}
		}
	}
	public static void showMenu(){
		System.out.println("天气查询系统：");
		System.out.println("1.查询某一天的天气数据");
		System.out.println("2.查询每年的最高气温");
		System.out.println("3.查询每年的最低气温");
		System.out.println("4.查询每年的平均气温");
		System.out.println("5.查询每年下雨的天数");
		System.out.println("6.预测明天的气温");
		System.out.println("0.退出");
		System.out.print("请选择[0-6]进行操作：");
	}
	public static void showList(List<String> list){
		for (String string : list) {
			System.out.println(string);
		}
	}
}
