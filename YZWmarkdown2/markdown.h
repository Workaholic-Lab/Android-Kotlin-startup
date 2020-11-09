#include"parse.h"
#include<vector>
class markdown {
private:
	vector<string>infile;  //读取文件内容
	string content;        //输出文件内容
	string mdName;         //读取文件名称
public:
	markdown(string _filename) {
		mdName = _filename;
	}
	void getfile(); //读文件，将其内容写入容器
	void process();  //转换处理
	void toHTML();     //将content内容写成html文件
};
#pragma once
