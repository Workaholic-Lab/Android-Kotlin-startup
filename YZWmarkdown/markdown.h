#include"parseStruct.h"
#include<string>
class markdown {
private:
	Node* root;           //���ڵ�
	vector<string>infile;  //��ȡ�ļ�����
	string content;        //����ļ�����
	string mdName;         //��ȡ�ļ�����
public:
	markdown(string _filename) {
		mdName = _filename;
		root = new Node(nul);
	}
	void getfile(); //���ļ�����������д������
	void process();  //�����������ݣ������﷨��
	int process_min(Node*& root,const int i ,int &j, bool &ifitalic, bool &ifstrong); //�������������е����⴦��
	Node* getroot(); //�õ����ڵ�
	void dfsContent(Node* root); //���﷨������д��content
	void toHTML();     //��content����д��html�ļ�
};
#pragma once
