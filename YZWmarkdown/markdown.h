#include"parseStruct.h"
#include<string>
class markdown {
private:
	Node* root;           //根节点
	vector<string>infile;  //读取文件内容
	string content;        //输出文件内容
	string mdName;         //读取文件名称
public:
	markdown(string _filename) {
		mdName = _filename;
		root = new Node(nul);
	}
	void getfile(); //读文件，将其内容写入容器
	void process();  //处理容器内容，构建语法树
	int process_min(Node*& root,const int i ,int &j, bool &ifitalic, bool &ifstrong); //处理容器内容中的特殊处理
	Node* getroot(); //得到根节点
	void dfsContent(Node* root); //将语法树内容写入content
	void toHTML();     //将content内容写成html文件
};
#pragma once
