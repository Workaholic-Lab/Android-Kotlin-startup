#include"parse.h"
#include<vector>
class markdown {
private:
	vector<string>infile;  //��ȡ�ļ�����
	string content;        //����ļ�����
	string mdName;         //��ȡ�ļ�����
public:
	markdown(string _filename) {
		mdName = _filename;
	}
	void getfile(); //���ļ�����������д������
	void process();  //ת������
	void toHTML();     //��content����д��html�ļ�
};
#pragma once
